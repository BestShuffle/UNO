package fr.uno.client.service;

import fr.uno.client.UnoClientUtils;
import fr.uno.client.object.UnoClient;
import fr.uno.common.UnoUtils;
import fr.uno.common.game.*;
import fr.uno.common.service.ServiceDatabase;

import java.io.IOException;

/**
 * Classe de gestion du client unique, utilisation de Singleton
 */
public class ServiceClient {

    // Instance unique
    private static volatile ServiceClient instance;
    private UnoClient client;

    /**
     * Méthode de récupération de l'instance unique
     *
     * @return Instance unique
     */
    public static ServiceClient getInstance() {
        // Méthode pour éviter le synchronized qui est coûteux
        if (ServiceClient.instance == null) {
            // Anti multi instanciation par multi thread
            synchronized (ServiceClient.class) {
                if (ServiceClient.instance == null) {
                    ServiceClient.instance = new ServiceClient();
                }
            }
        }
        return ServiceClient.instance;
    }

    /**
     * Méthode de création d'objet client
     */
    public void createClient() {
        client = new UnoClient(
                null,
                ServiceConfig.getInstance().getProperty("serverAddress"),
                Integer.valueOf(ServiceConfig.getInstance().getProperty("serverPort"))
        );
    }

    /**
     * Méthode d'envoi de message au serveur
     *
     * @param message Message
     */
    public void sendMessage(Message message) {
        if (client != null) {
            client.sendMessage(message);
        }
    }

    /**
     * Méthode d'envoi de message au serveur
     *
     * @param messageType Type de message
     * @param sender      Envoyeur
     * @param content     Contenu du message
     */
    public void sendMessage(MessageType messageType, Object sender, Object content) {
        sendMessage(new Message(messageType, sender, content));
    }

    /**
     * Méthode d'envoi de message au serveur sans spécification de l'envoyeur
     *
     * @param messageType Type de message
     * @param content     Contenu du message
     */
    public void sendMessage(MessageType messageType, Object content) {
        sendMessage(messageType, client.getUsername(), content);
    }

    /**
     * Méthode d'envoi du nom d'utilisateur au serveur
     */
    public void sendUsername() {
        sendMessage(MessageType.REQUEST_USERNAME, client.getUsername());
    }

    /**
     * Méthode de jeu d'une carte durant une partie
     *
     * @param playedCard Carte jouée
     */
    public void requestToPlayCard(UnoCard playedCard) {
        // Cartes de 1 à 9
        sendMessage(MessageType.REQUEST_PLAY_CARD, playedCard);
    }

    /**
     * Méthode de jeu d'une carte spéciale durant une partie
     *
     * @param specialPlayedCard Carte spéciale jouée
     */
    public void requestToPlayCardSpecial(UnoCardSpecial specialPlayedCard) {
        sendMessage(MessageType.REQUEST_PLAY_CARD_SPECIAL, specialPlayedCard);
    }

    /**
     * Méthode permettant de demander un changement de couleur au serveur
     *
     * @param specialCard Carte spéciale jouée
     */
    public void requestToChangeColor(UnoCardSpecial specialCard) {
        // Récupération du choix de l'utilisateur
        String chooseResult = UnoClientUtils.popupChooseColor();
        if (chooseResult != null) {
            // Récupération de l'objet couleur associé
            UnoCardColor newColor = UnoCardColor.valueOf(chooseResult.toUpperCase());
            // Création de la carte à afficher
            UnoCardSpecial specialCardWithNewColor =
                    new UnoCardSpecial(newColor, UnoUtils.detectCardName(specialCard), specialCard.getEffect());
            // Envoi des cartes au serveur
            UnoCardSpecial[] specialCards = {specialCard, specialCardWithNewColor};
            sendMessage(MessageType.REQUEST_CHANGE_COLOR, specialCards);
        }
    }


    /**
     * Méthode de demande de pioche de carte
     */
    public void requestToPickCard() {
        sendMessage(MessageType.REQUEST_PICK_CARD, null);
    }

    /**
     * Méthode de connexion du client au serveur
     *
     * @return Le client s'est bien connecté ?
     */
    public void connect() {
        if (client != null && !client.isConnected()) {
            client.connect();
        }
    }

    /**
     * Méthode de login du joueur à la base de données
     * Vérification des identifiants
     *
     * @param username Nom d'utilisateur
     * @param password Mot de passe
     */
    public void logPlayer(String username, String password) {
        String[] logins = {username, password};
        sendMessage(MessageType.REQUEST_CHECK_LOGIN, "", logins);
    }

    /**
     * Méthode de connexion du joueur à la base de données
     * Vérification que l'utilisateur n'est pas déjà connecté
     *
     * @param username Nom d'utilisateur
     */
    public void connectPlayer(String username) {
        sendMessage(MessageType.REQUEST_LOGIN, username);
    }

    /**
     * Méthode prévenant le serveur que l'on est prêt à jouer
     */
    public void requestToPlay() {
        sendMessage(MessageType.REQUEST_PLAY, null);
    }

    /**
     * Méthode demandant au serveur d'enregistrer un nouveau joueur
     *
     * @param username Nom d'utilisateur
     * @param password Mot de passe
     * @param email    Adresse mail
     */
    public void requestRegister(String username, String password, String email) {
        String[] registerInfos = {username, password, email};
        sendMessage(MessageType.REQUEST_REGISTER, "", registerInfos);
    }

    /**
     * Méthode déconnectant le joueur dans la base de données
     */
    public void disconnectPlayer() {
        sendMessage(MessageType.REQUEST_LOGOUT, client.getUsername());
    }

    /**
     * Méthode de déconnexion du client
     */
    public void disconnect() {
        if (client != null && client.isConnected()) {
            client.disconnect();
        }
    }

    public UnoClient getClient() {
        return client;
    }

    public void setClient(UnoClient client) {
        this.client = client;
    }

    public String getUsername() {
        return client.getUsername();
    }

    public void setUsername(String username) {
        client.setUsername(username);
    }
}
