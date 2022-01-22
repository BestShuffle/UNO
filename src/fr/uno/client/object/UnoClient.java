package fr.uno.client.object;

import fr.uno.client.UnoClientUtils;
import fr.uno.common.game.Message;
import fr.uno.common.game.UnoCard;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe d'objet UnoClient
 */
public class UnoClient {
    private static final transient Logger log = Logger.getLogger(UnoClient.class);

    private String username;
    private String sAddress;
    private int iPort;
    private Socket sockSocket;
    private ObjectInputStream oIn;
    private ObjectOutputStream oOut;
    private boolean isConnected;

    private List<UnoCard> cards;
    private int mixedCardsNumber;

    /**
     * Méthode de création d'un objet UnoClient
     *
     * @param username Nom d'utilisateur
     * @param sAddress Adresse de connexion au serveur
     * @param iPort    Port de connexion au serveur
     */
    public UnoClient(String username, String sAddress, int iPort) {
        super();
        this.username = username;
        this.sAddress = sAddress;
        this.iPort = iPort;
        this.cards = new ArrayList<>();
    }

    /**
     * Méthode de connexion du client au serveur
     *
     * @throws IOException Erreur
     */
    public void connect() {
        try {
            sockSocket = new Socket(sAddress, iPort);
            oOut = new ObjectOutputStream(sockSocket.getOutputStream());
            oIn = new ObjectInputStream(sockSocket.getInputStream());

            UnoClientSend oClsend = new UnoClientSend(sockSocket, oOut);
            Thread threadSend = new Thread(oClsend);
            threadSend.start();

            UnoClientReceive oClRecv = new UnoClientReceive(this);
            Thread threadReceive = new Thread(oClRecv);
            threadReceive.start();

            isConnected = true;
        } catch (IOException e) {
            UnoClientUtils.popupError("Impossible de se connecter au serveur.");
            isConnected = false;
        }
    }

    /**
     * Fonction d'envoi de message
     *
     * @param message Message envoyé
     */
    public void sendMessage(Message message) {
        try {
            oOut.writeObject(message);
            oOut.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Méthode de déconnexion du client
     */
    public void disconnect() {
        try {
            if (isConnected) {
                sockSocket.close();
                isConnected = false;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public String getsAddress() {
        return sAddress;
    }

    public void setsAddress(String sAddress) {
        this.sAddress = sAddress;
    }

    public int getiPort() {
        return iPort;
    }

    public void setiPort(int iPort) {
        this.iPort = iPort;
    }

    public Socket getSockSocket() {
        return sockSocket;
    }

    public void setSockSocket(Socket sockSocket) {
        this.sockSocket = sockSocket;
    }

    public ObjectInputStream getoIn() {
        return oIn;
    }

    public void setoIn(ObjectInputStream oIn) {
        this.oIn = oIn;
    }

    public ObjectOutputStream getoOut() {
        return oOut;
    }

    public void setoOut(ObjectOutputStream oOut) {
        this.oOut = oOut;
    }

    String messageReceived(Message message) {
        return message.toString();
    }

    public List<UnoCard> getCards() {
        return cards;
    }

    public void setCards(List<UnoCard> cards) {
        this.cards = cards;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getMixedCardsNumber() {
        return mixedCardsNumber;
    }

    public void setMixedCardsNumber(int mixedCardsNumber) {
        this.mixedCardsNumber = mixedCardsNumber;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}