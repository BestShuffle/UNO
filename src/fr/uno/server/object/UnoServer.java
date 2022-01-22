package fr.uno.server.object;

import fr.uno.common.UnoUtils;
import fr.uno.common.game.*;
import fr.uno.common.service.ServiceDatabase;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Classe d'objet serveur Uno
 * Règles provenant de : @see http://www.jdbn.fr/regles-officielles-du-uno-pour-faire-taire-ceux-qui-les-inventent/
 */
public class UnoServer implements Serializable {
    private final transient Logger log = Logger.getLogger(UnoServer.class);

    private int port;
    private List<UnoConnectedClient> clients;
    // Pile de cartes mélangée
    private List<UnoCard> mixedCards;
    // Liste des cartes qui ont étés jouées
    private List<UnoCard> playedCard;
    private int nbWantedPlayers;
    private boolean partyStarted;

    private UnoConnectedClient waitedClient;
    private List<UnoConnectedClient> waitedClientsOrder;
    private UnoCard lastPlayedCard;

    private boolean isNextPlayerSkipped;

    /**
     * Méthode construisant un serveur Uno
     *
     * @param port            Port
     * @param nbWantedPlayers Nombre de joueurs requis pour démarrer une partie
     */
    public UnoServer(int port, int nbWantedPlayers) {
        super();
        this.port = port;
        this.nbWantedPlayers = nbWantedPlayers;
        this.clients = new ArrayList<>();
        this.playedCard = new ArrayList<>();
        this.mixedCards = new ArrayList<>();
        this.waitedClientsOrder = new ArrayList<>();
        this.partyStarted = false;
        this.isNextPlayerSkipped = false;

        try {
            new Thread(new UnoConnection(this)).start();
        } catch (IOException e) {
            log.error("Erreur : " + e.getMessage());
        }
    }

    /**
     * Méthode vérifiant si la partie est prête à démarrer ou non
     *
     * @param sender Envoyeur
     */
    public void checkIfPartyCanStart(UnoConnectedClient sender) {
        if (partyStarted) {
            // Refus de la connexion du joueur si partie déjà démarrée
            sender.sendMessage(MessageType.REFUSE_CONNECT, null);
            sender.setWantToPlay(false);
        } else if (getNbClientsWantsToPlay() >= nbWantedPlayers) {
            // Partie prête à être démarrée
            startGame();
        } else {
            // Besoin de plus de joueurs
            log.info("Besoin de " + (nbWantedPlayers - clients.size()) + " joueur(s) pour commencer la partie.");
        }
    }

    /**
     * Méthode retournant le nombre de joueurs souhaitant jouer
     * @return Nombre de joueurs souhaitant jouer
     */
    private int getNbClientsWantsToPlay() {
        int nbClientsWantsToPlay = 0;
        for (UnoConnectedClient client : clients) {
            if (client.isWantToPlay()) {
                nbClientsWantsToPlay++;
            }
        }
        return nbClientsWantsToPlay;
    }

    /**
     * Méthode vérifiant si la partie est terminée
     *
     * @param lastPlayer Dernier joueur ayant joué
     */
    private void checkIfPartyIsFinished(UnoConnectedClient lastPlayer) {
        if (lastPlayer != null && lastPlayer.getCards().size() == 0) {
            calculateAndSendGamePoints();
            endGame();
        }
    }

    /**
     * Méthode calculant les points de chaque joueurs de la partie
     */
    private void calculateAndSendGamePoints() {
        List<UnoResult> playersResults = new ArrayList<>();
        for (UnoConnectedClient client : waitedClientsOrder) {
            playersResults.add(new UnoResult(client.getUsername(), calculatePoints(client)));
        }
        broadcastMessage(MessageType.GAME_FINISHED, playersResults);
    }

    /**
     * Méthode calculant les points d'un joueur à partir de sa main
     *
     * @param client Joueur
     * @return Total de points du joueur
     */
    private int calculatePoints(UnoConnectedClient client) {
        int totalPoints = 0;
        for (UnoCard card : client.getCards()) {
            if (card instanceof UnoCardSpecial) {
                UnoCardSpecial specialCard = (UnoCardSpecial) card;
                int pointsToAdd = 0;
                switch (specialCard.getEffect()) {
                    case PLUS_2:
                    case REVERSE:
                    case SKIP:
                        pointsToAdd = 20;
                        break;
                    case PLUS_4:
                    case CHANGE_COLOR:
                        pointsToAdd = 40;
                        break;
                }
                totalPoints += pointsToAdd;
            } else {
                String cardName = UnoUtils.detectCardName(card);
                Integer nbCardPoints = Integer.parseInt(cardName);
                totalPoints += nbCardPoints;
            }
        }
        return totalPoints;
    }

    /**
     * Méthode vérifiant si la partie doit être stoppée
     */
    private void checkIfPartyHasToStop() {
        if (clients.size() == 0) {
            endGame();
        }
    }

    /**
     * Méthode de démarrage de partie
     */
    private void startGame() {
        if (!partyStarted) {
            log.info("La partie peut démarrer !");

            log.info("Mélange des cartes..");

            shuffleCards();

            log.info("Distribution des cartes...");

            clients.forEach(client -> {
                Engine.distributeCardsToClient(client);
                client.sendMessage(MessageType.GIVE_CARDS, client.getCards());
            });

            log.info("Cartes distribuées.");

            partyStarted = true;

            broadcastMessage(MessageType.REQUEST_USERNAME, null);

            broadcastMessage(MessageType.GAME_START, null);

            // Initialisation de l'ordre des joueurs
            setWaitedClientsOrder();

            // Pose d'une carte aléatoire
            playFirstGameCardAndInitGame();
        }
    }

    /**
     * Méthode récupérant et mélangeant les cartes
     */
    private void shuffleCards() {
        mixedCards = Engine.loadCards();
        Collections.shuffle(mixedCards);
    }

    /**
     * Méthode de clôture de partie
     */
    private void endGame() {
        if (partyStarted) {
            isNextPlayerSkipped = false;
            partyStarted = false;
            lastPlayedCard = null;
            log.info("Partie terminée. En attente de joueurs pour une nouvelle partie.");

            setClientsDontWantToPlay();
        }
    }

    /**
     * Méthode définissant que les joueurs ne veulent pas jouer
     */
    private void setClientsDontWantToPlay() {
        for (UnoConnectedClient client : clients) {
            client.setWantToPlay(false);
        }
    }

    /**
     * Méthode d'envoi de message à tous les utilisateurs connectés
     *
     * @param messageType Type du message
     * @param content     Contenu du message
     */
    private void broadcastMessage(MessageType messageType, Object content) {
        for (UnoConnectedClient client : clients) {
            client.sendMessage(messageType, content);
        }
    }

    /**
     * Méthode d'envoi de message à tous les utilisateurs excepté l'envoyeur
     *
     * @param message Message
     */
    void broadcastMessageExceptSender(Message message) {
        for (UnoConnectedClient client : clients) {
            // Envoi à tout utilisateur excepté l'envoyeur
            if (!String.valueOf(client.getId()).equals(message.getSender())) {
                client.sendMessage(message);
            }
        }
    }

    /**
     * Méthode envoyant un message à tous les joueurs sauf celui attendu
     *
     * @param messageType Type de message
     * @param content     Contenu
     */
    private void broadcastMessageExceptWaited(MessageType messageType, Object content) {
        for (UnoConnectedClient client : clients) {
            // Envoi à tout utilisateur excepté l'envoyeur
            if (!client.equals(waitedClient)) {
                client.sendMessage(messageType, content);
            }
        }
    }

    /**
     * Méthode ordonnant aux clients de jouer une carte
     *
     * @param sender Envoyeur
     * @param card   Carte jouée
     */
    private void broadcastPlayedCard(UnoConnectedClient sender, UnoCard card) {
        broadcastMessage(MessageType.PLAY_CARD, card);

        // Si le sender est null c'est que c'est le serveur qui joue la carte
        if (sender != null) {
            sender.removeCard(card);
            sender.sendMessage(MessageType.REMOVE_CARD, card);
        }
    }

    /**
     * Méthode jouant une carte dans la partie active
     *
     * @param client Client jouant la carte
     * @param card   Carte à jouer
     */
    public void playCard(UnoConnectedClient client, UnoCard card) {
        // Partie démarrée et joueur attendu
        if (isPartyStarted() && client.equals(waitedClient)) {
            // Première carte posée ?
            if (lastPlayedCard == null) {
                newCardPlayedTreatment(client, card);
            } else {
                log.info("Le joueur " + client.getId() + " joue la carte : " + card);

                if (lastPlayedCard.getColor().equals(card.getColor()) ||
                        cardsHasSameName(lastPlayedCard, card)) {
                    /**
                     * Cartes de 1 à 9
                     */
                    newCardPlayedTreatment(client, card);
                }
            }
        }
    }

    /**
     * Méthode jouant une carte spéciale ayant un changement de couleur
     *
     * @param client       Envoyeur
     * @param specialCards Carte originale suivie de la nouvelle carte
     */
    public void playSpecialCardWithColorChange(UnoConnectedClient client, UnoCardSpecial[] specialCards) {
        playCardSpecial(client, specialCards[1]);
        client.sendMessage(MessageType.REMOVE_CARD, specialCards[0]);
    }

    /**
     * Méthode posant la première carte de la partie
     */
    public void playFirstGameCardAndInitGame() {
        newCardPlayedTreatment(null, pickRandomNormalCard());
    }

    /**
     * Méthode tirant une carte aléatoire de 1 à 9
     *
     * @return Carte de 1 à 9
     */
    private UnoCard pickRandomNormalCard() {
        UnoCard card = pickRandomCard();

        while (isCardSpecial(card)) {
            card = pickRandomCard();
        }

        return card;
    }

    /**
     * Méthode détectant si une carte est spéciale
     *
     * @param card Carte
     * @return Spéciale ou non ?
     */
    public boolean isCardSpecial(UnoCard card) {
        String cardName = UnoUtils.detectCardName(card);
        boolean isSpecialCard = false;
        for (UnoCardSpecialEffect effect : UnoCardSpecialEffect.values()) {
            if (cardName.equals(effect.toString())) {
                isSpecialCard = true;
            }
        }
        return isSpecialCard;
    }

    /**
     * Méthode permettant de jouer une carte spéciale
     *
     * @param client      Client posant la carte
     * @param specialCard Carte spéciale jouée
     */
    public void playCardSpecial(UnoConnectedClient client, UnoCardSpecial specialCard) {
        if (isPartyStarted() && waitedClient.equals(client)) {
            switch (specialCard.getEffect()) {
                case PLUS_2:
                    if (lastPlayedCard == null) {
                        // Première carte
                        pickTwoCardsToClientAfter(client);
                        skipNextPlayer(client);
                    } else if (lastPlayedCard.getColor() == specialCard.getColor() ||
                            cardsHasSameName(lastPlayedCard, specialCard)) {
                        // Pas la bonne couleur = pas bon
                        pickTwoCardsToClientAfter(client);
                        skipNextPlayer(client);
                    } else {
                        // Pas le bon moment
                        return;
                    }
                    break;
                case PLUS_4:
                    pickFourCardsToClientAfter(client);
                    skipNextPlayer(client);
                    break;
                case SKIP:
                    if (lastPlayedCard == null) {
                        // Première carte
                        skipNextPlayer(client);
                    } else if (lastPlayedCard.getColor() == specialCard.getColor() ||
                            cardsHasSameName(lastPlayedCard, specialCard)) {
                        // Pas la bonne couleur = pas bon
                        skipNextPlayer(client);
                    } else {
                        // Pas le bon moment
                        return;
                    }
                    break;
                case REVERSE:
                    if (lastPlayedCard == null) {
                        // Première carte
                        reverseWaitedClientsOrder(client);
                    } else if (lastPlayedCard.getColor() == specialCard.getColor() ||
                            cardsHasSameName(lastPlayedCard, specialCard)) {
                        // Pas la bonne couleur = pas bon
                        reverseWaitedClientsOrder(client);
                    } else {
                        // Pas le bon moment
                        return;
                    }
                    break;
                case CHANGE_COLOR:
                    break;
            }
            newCardPlayedTreatment(client, specialCard);
        }
    }

    private void pickTwoCardsToClientAfter(UnoConnectedClient client) {
        if (isPartyStarted() && waitedClient.equals(client)) {
            // Récupération du prochain joueur devant jouer
            UnoConnectedClient playerAfterClient = getNewWaitedClient();
            log.info("Le joueur " + playerAfterClient.getId() + " pioche 2 cartes.");
            pickCardToTimes(playerAfterClient, 2);
        }
    }

    private void pickFourCardsToClientAfter(UnoConnectedClient client) {
        if (isPartyStarted() && waitedClient.equals(client)) {
            // Récupération du prochain joueur devant jouer
            UnoConnectedClient playerAfterClient = getNewWaitedClient();
            log.info("Le joueur " + playerAfterClient.getId() + " pioche 4 cartes.");
            pickCardToTimes(playerAfterClient, 4);
        }
    }

    private void skipNextPlayer(UnoConnectedClient client) {
        if (isPartyStarted() && waitedClient.equals(client)) {
            // Récupération du prochain joueur devant jouer
            isNextPlayerSkipped = true;
        }
    }

    /**
     * Méthode inversant l'ordre de jeu des joueurs
     *
     * @param client Client ayant posé la carte
     */
    private void reverseWaitedClientsOrder(UnoConnectedClient client) {
        if (isPartyStarted() && waitedClient.equals(client)) {
            Collections.reverse(waitedClientsOrder);
        }
    }

    /**
     * Méthode de pioche de carte à un joueur attendu
     *
     * @param client Client devant piocher
     */
    public void pickCardToWaitedCLient(UnoConnectedClient client) {
        if (isPartyStarted() && client.equals(waitedClient)) {
            pickCardTo(client);
        }
        newCardPlayedTreatment(client, null);
    }

    /**
     * Méthode faisant piocher un joueur X fois
     *
     * @param client  Client devant piocher
     * @param nbTimes Nombre de cartes à piocher
     */
    private void pickCardToTimes(UnoConnectedClient client, int nbTimes) {
        for (int time = 0; time < nbTimes; time++) {
            pickCardTo(client);
        }
        newCardPlayedTreatment(client, null);
    }

    /**
     * Méthode de pioche de carte à un joueur
     *
     * @param client Client devant piocher
     */
    private void pickCardTo(UnoConnectedClient client) {
        UnoCard pickedCard = pickRandomCard();
        client.sendMessage(MessageType.PICK_CARD, pickedCard);
        client.addCard(pickedCard);
    }

    /**
     * Traitement effectué à chaque fois qu'une nouvelle carte est jouée
     *
     * @param sender Envoyeur
     * @param card   Carte jouée
     */
    private void newCardPlayedTreatment(UnoConnectedClient sender, UnoCard card) {
        if (isNextPlayerSkipped) {
            isNextPlayerSkipped = false;
            setNewWaitedClient();
        }
        setNewWaitedClient();

        if (card != null) {
            // Utilisation du setter pour l'éventuelle maintenance du code (si besoin de traitement)
            broadcastPlayedCard(sender, card);
            setLastPlayedCard(card);
            // Détection de la fin de partie
            checkIfPartyIsFinished(sender);
        }

        preventPlayerHeHasToPlay();
        preventOtherPlayersTheyDontHaveToPlay();
    }

    /**
     * Méthode prévenant les joueurs autres que le joueur attendu qu'ils ne doivent pas jouer
     */
    private void preventOtherPlayersTheyDontHaveToPlay() {
        if (isPartyStarted()) {
            broadcastMessageExceptWaited(MessageType.HAS_NOT_TO_PLAY, null);
        }
    }

    /**
     * Méthode avertissant le joueur attendu qu'il doit jouer
     */
    private void preventPlayerHeHasToPlay() {
        if (isPartyStarted()) {
            waitedClient.sendMessage(MessageType.HAS_TO_PLAY, null);
        }
    }

    /**
     * Méthode définissant le prochain joueur devant jouer
     */
    private void setNewWaitedClient() {
        waitedClient = getNewWaitedClient();
        log.info("En attente du joueur : " + waitedClient.getId());
    }

    /**
     * Méthode retournant le prochain joueur devant jouer
     *
     * @return Prochain joueur devant jouer
     */
    private UnoConnectedClient getNewWaitedClient() {
        UnoConnectedClient newWaitedClient;
        // S'il s'agit du début de la partie le premier joueur attendu est null
        if (waitedClient == null) {
            newWaitedClient = waitedClientsOrder.get(0);
        } else {
            // Récupération de l'index du joueur actuellement attendu dans la liste des joueurs attendus
            int actualWaitedClientIndex = waitedClientsOrder.indexOf(waitedClient);
            // Définition du potentiel prochain index du joueur attendu
            int potentialNewWaitedClientIndex = actualWaitedClientIndex + 1;
            // Si l'index est plus grand ou égal à la taille de la liste des joueurs potentiels on repart de zéro
            // car logiquement on a fait jouer tous les joueurs
            if (potentialNewWaitedClientIndex >= waitedClientsOrder.size()) {
                newWaitedClient = waitedClientsOrder.get(0);
            } else {
                newWaitedClient = waitedClientsOrder.get(potentialNewWaitedClientIndex);
            }
        }

        return newWaitedClient;
    }

    /**
     * Méthode vérifiant si deux cartes ont le même nom
     *
     * @param firstCard  Première carte
     * @param secondCard Deuxième carte
     * @return Les cartes sont identiques ?
     */
    public boolean cardsHasSameName(UnoCard firstCard, UnoCard secondCard) {
        return UnoUtils.detectCardName(firstCard).equals(UnoUtils.detectCardName(secondCard));
    }

    /**
     * Définit la liste d'ordre des joueurs et la mélange
     */
    private void setWaitedClientsOrder() {
        waitedClientsOrder = new ArrayList<>(clients);
        Collections.shuffle(waitedClientsOrder);
    }

    /**
     * Méthode retournant un client aléatoire
     *
     * @return Client
     */
    private UnoConnectedClient getRandomClient() {
        return clients.get(new Random().nextInt(clients.size()));
    }

    /**
     * Méthode gérant la déconnexion d'un utilisateur
     *
     * @param disconnectedClient Utilisateur se déconnectant
     */
    void disconnectClient(UnoConnectedClient disconnectedClient) {
        clients.remove(disconnectedClient);
        log.info("Le client " + disconnectedClient.getId() + " s'est déconnecté.");
        broadcastMessage(MessageType.INFO, "Le client " + disconnectedClient.getId() + " s'est déconnecté.");

        checkIfPartyHasToStop();
    }

    /**
     * Méthode de gestion de connexion d'un utilisateur
     *
     * @param newClient Nouvel utilisateur
     */
    void addClient(UnoConnectedClient newClient) {
        broadcastMessage(MessageType.INFO, "Le client " + newClient.getId() + " vient de se connecter.");
        clients.add(newClient);
    }

    /**
     * Méthode retournant un client connecté à partir de son id
     *
     * @param id Id du client recherché
     * @return Client connecté
     */
    public UnoConnectedClient getClientFromId(int id) {
        for (UnoConnectedClient client : clients) {
            if (id == client.getId()) {
                return client;
            }
        }
        return null;
    }

    /**
     * Méthode de pioche de carte aléatoire
     *
     * @return Carte piochée aléatoirement
     */
    private UnoCard pickRandomCard() {
        UnoCard card = mixedCards.get(0);
        mixedCards.remove(0);
        // Vérification si la carte est spéciale ou non
        String cardName = UnoUtils.detectCardName(card);
        for (UnoCardSpecialEffect effect : UnoCardSpecialEffect.values()) {
            if (cardName.equals(effect.toString())) {
                return new UnoCardSpecial(card.getColor(), card.getName(), effect);
            }
        }
        return card;
    }

    /**
     * Méthode de vérification d'identifiants de l'utilisateur à la base de données
     *
     * @param sender   Envoyeur
     * @param username Nom d'utilisateur
     * @param password Mot de passe
     */
    public void checkLoginPlayer(UnoConnectedClient sender, String username, String password) {
        try {
            if (ServiceDatabase.getInstance().checkLogPlayer(username, password)) {
                sender.sendMessage(MessageType.REQUEST_CHECK_LOGIN, username);
            } else {
                sender.sendMessage(MessageType.BAD_LOGIN, null);
            }
        } catch (SQLException e) {
            log.error("Erreur SQL : " + e.getMessage());
        }
    }

    /**
     * Méthode de connexion de l'utilisateur à la base de données
     *
     * @param sender   Envoyeur
     * @param username Nom d'utilisateur
     */
    public void loginPlayer(UnoConnectedClient sender, String username) {
        try {
            if (!ServiceDatabase.getInstance().isPlayerConnected(username)) {
                sender.setUsername(username);
                sender.sendMessage(MessageType.REQUEST_LOGIN, username);
                ServiceDatabase.getInstance().connectPlayer(username);
            } else {
                sender.sendMessage(MessageType.ALREADY_CONNECTED, username);
            }
        } catch (SQLException e) {
            log.error("Erreur SQL : " + e.getMessage());
        }
    }

    /**
     * Méthode de déconnexion de l'utilisateur dans la base de données
     *
     * @param username Nom d'utilisateur
     */
    public void logoutPlayer(String username) {
        ServiceDatabase.getInstance().disconnectPlayer(username);
    }

    /**
     * Méthode enregistrant un utilisateur en base de données
     *
     * @param sender Envoyeur
     * @param username Nom d'utilisateur
     * @param password Mot de passe
     * @param email    Email
     */
    public void registerPlayer(UnoConnectedClient sender, String username, String password, String email) {
        try {
            ServiceDatabase.getInstance().registerUser(username, password, email);
            sender.sendMessage(MessageType.REGISTERED, username);
        } catch (SQLException e) {
            log.error("Erreur SQL : " + e.getMessage());
        }
    }

    int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<UnoConnectedClient> getClients() {
        return clients;
    }

    public void setClients(List<UnoConnectedClient> clients) {
        this.clients = clients;
    }

    public int getNbWantedPlayers() {
        return nbWantedPlayers;
    }

    public void setNbWantedPlayers(int nbWantedPlayers) {
        this.nbWantedPlayers = nbWantedPlayers;
    }

    public List<UnoCard> getMixedCards() {
        return mixedCards;
    }

    public void setMixedCards(List<UnoCard> mixedCards) {
        this.mixedCards = mixedCards;
    }

    public List<UnoCard> getPlayedCard() {
        return playedCard;
    }

    public void setPlayedCard(List<UnoCard> playedCard) {
        this.playedCard = playedCard;
    }

    boolean isPartyStarted() {
        return partyStarted;
    }

    void setPartyStarted(boolean partyStarted) {
        this.partyStarted = partyStarted;
    }

    public UnoConnectedClient getWaitedClient() {
        return waitedClient;
    }

    public void setWaitedClient(UnoConnectedClient waitedClient) {
        this.waitedClient = waitedClient;
    }

    public List<UnoConnectedClient> getWaitedClientsOrder() {
        return waitedClientsOrder;
    }

    public void setWaitedClientsOrder(List<UnoConnectedClient> waitedClientsOrder) {
        this.waitedClientsOrder = waitedClientsOrder;
    }

    public UnoCard getLastPlayedCard() {
        return lastPlayedCard;
    }

    public void setLastPlayedCard(UnoCard lastPlayedCard) {
        this.lastPlayedCard = lastPlayedCard;
    }

    public boolean isNextPlayerSkipped() {
        return isNextPlayerSkipped;
    }

    public void setNextPlayerSkipped(boolean nextPlayerSkipped) {
        isNextPlayerSkipped = nextPlayerSkipped;
    }
}
