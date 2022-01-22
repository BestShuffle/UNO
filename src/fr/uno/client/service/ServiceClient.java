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
     * M�thode de r�cup�ration de l'instance unique
     *
     * @return Instance unique
     */
    public static ServiceClient getInstance() {
        // M�thode pour �viter le synchronized qui est co�teux
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
     * M�thode de cr�ation d'objet client
     */
    public void createClient() {
        client = new UnoClient(
                null,
                ServiceConfig.getInstance().getProperty("serverAddress"),
                Integer.valueOf(ServiceConfig.getInstance().getProperty("serverPort"))
        );
    }

    /**
     * M�thode d'envoi de message au serveur
     *
     * @param message Message
     */
    public void sendMessage(Message message) {
        if (client != null) {
            client.sendMessage(message);
        }
    }

    /**
     * M�thode d'envoi de message au serveur
     *
     * @param messageType Type de message
     * @param sender      Envoyeur
     * @param content     Contenu du message
     */
    public void sendMessage(MessageType messageType, Object sender, Object content) {
        sendMessage(new Message(messageType, sender, content));
    }

    /**
     * M�thode d'envoi de message au serveur sans sp�cification de l'envoyeur
     *
     * @param messageType Type de message
     * @param content     Contenu du message
     */
    public void sendMessage(MessageType messageType, Object content) {
        sendMessage(messageType, client.getUsername(), content);
    }

    /**
     * M�thode d'envoi du nom d'utilisateur au serveur
     */
    public void sendUsername() {
        sendMessage(MessageType.REQUEST_USERNAME, client.getUsername());
    }

    /**
     * M�thode de jeu d'une carte durant une partie
     *
     * @param playedCard Carte jou�e
     */
    public void requestToPlayCard(UnoCard playedCard) {
        // Cartes de 1 � 9
        sendMessage(MessageType.REQUEST_PLAY_CARD, playedCard);
    }

    /**
     * M�thode de jeu d'une carte sp�ciale durant une partie
     *
     * @param specialPlayedCard Carte sp�ciale jou�e
     */
    public void requestToPlayCardSpecial(UnoCardSpecial specialPlayedCard) {
        sendMessage(MessageType.REQUEST_PLAY_CARD_SPECIAL, specialPlayedCard);
    }

    /**
     * M�thode permettant de demander un changement de couleur au serveur
     *
     * @param specialCard Carte sp�ciale jou�e
     */
    public void requestToChangeColor(UnoCardSpecial specialCard) {
        // R�cup�ration du choix de l'utilisateur
        String chooseResult = UnoClientUtils.popupChooseColor();
        if (chooseResult != null) {
            // R�cup�ration de l'objet couleur associ�
            UnoCardColor newColor = UnoCardColor.valueOf(chooseResult.toUpperCase());
            // Cr�ation de la carte � afficher
            UnoCardSpecial specialCardWithNewColor =
                    new UnoCardSpecial(newColor, UnoUtils.detectCardName(specialCard), specialCard.getEffect());
            // Envoi des cartes au serveur
            UnoCardSpecial[] specialCards = {specialCard, specialCardWithNewColor};
            sendMessage(MessageType.REQUEST_CHANGE_COLOR, specialCards);
        }
    }


    /**
     * M�thode de demande de pioche de carte
     */
    public void requestToPickCard() {
        sendMessage(MessageType.REQUEST_PICK_CARD, null);
    }

    /**
     * M�thode de connexion du client au serveur
     *
     * @return Le client s'est bien connect� ?
     */
    public void connect() {
        if (client != null && !client.isConnected()) {
            client.connect();
        }
    }

    /**
     * M�thode de login du joueur � la base de donn�es
     * V�rification des identifiants
     *
     * @param username Nom d'utilisateur
     * @param password Mot de passe
     */
    public void logPlayer(String username, String password) {
        String[] logins = {username, password};
        sendMessage(MessageType.REQUEST_CHECK_LOGIN, "", logins);
    }

    /**
     * M�thode de connexion du joueur � la base de donn�es
     * V�rification que l'utilisateur n'est pas d�j� connect�
     *
     * @param username Nom d'utilisateur
     */
    public void connectPlayer(String username) {
        sendMessage(MessageType.REQUEST_LOGIN, username);
    }

    /**
     * M�thode pr�venant le serveur que l'on est pr�t � jouer
     */
    public void requestToPlay() {
        sendMessage(MessageType.REQUEST_PLAY, null);
    }

    /**
     * M�thode demandant au serveur d'enregistrer un nouveau joueur
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
     * M�thode d�connectant le joueur dans la base de donn�es
     */
    public void disconnectPlayer() {
        sendMessage(MessageType.REQUEST_LOGOUT, client.getUsername());
    }

    /**
     * M�thode de d�connexion du client
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
