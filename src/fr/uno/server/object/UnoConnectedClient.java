package fr.uno.server.object;

import fr.uno.common.game.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe d'objet de client connecté
 *
 * @author BestShuffle
 */
public class UnoConnectedClient implements Runnable {

    private static int idCounter = 0;
    private final transient Logger log = Logger.getLogger(UnoConnectedClient.class);
    private int id;
    private UnoServer server;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private List<UnoCard> cards;

    private String username;
    private boolean wantToPlay;

    /**
     * Construit un objet UnoConnectedClient
     * Récupère les streams à partir du socket
     *
     * @param server Serveur
     * @param socket Socket
     * @throws IOException Erreur de stream
     */
    UnoConnectedClient(UnoServer server, Socket socket) throws IOException {
        idCounter++;

        this.id = idCounter;
        this.server = server;
        this.socket = socket;
        this.in = new ObjectInputStream(socket.getInputStream());
        this.out = new ObjectOutputStream(socket.getOutputStream());

        this.cards = new ArrayList<>();
        wantToPlay = false;
    }

    @Override
    public void run() {
        boolean isActive = true;
        while (isActive) {
            try {
                Message message = (Message) in.readObject();
                if (message != null) {
                    message.setSender(String.valueOf(id));

                    switch (message.getMessageType()) {
                        case CHAT:
                            server.broadcastMessageExceptSender(message);
                            break;
                        case REQUEST_REGISTER:
                            String[] registerInfos = (String[]) message.getContent();
                            String registerUsername = registerInfos[0];
                            String registerPassword = registerInfos[1];
                            String registerEmail = registerInfos[2];
                            server.registerPlayer(this, registerUsername, registerPassword, registerEmail);
                            break;
                        case REQUEST_PLAY:
                            wantToPlay = true;
                            server.checkIfPartyCanStart(this);
                            break;
                        case REQUEST_USERNAME:
                            String username = (String) message.getContent();
                            setUsername(username);
                            break;
                        case REQUEST_PICK_CARD:
                            server.pickCardToWaitedCLient(this);
                            break;
                        case REQUEST_CHANGE_COLOR:
                            server.playSpecialCardWithColorChange(this, (UnoCardSpecial[]) message.getContent());
                            break;
                        case REQUEST_PLAY_CARD:
                            server.playCard(this, (UnoCard) message.getContent());
                            break;
                        case REQUEST_PLAY_CARD_SPECIAL:
                            server.playCardSpecial(this, (UnoCardSpecial) message.getContent());
                            break;
                        case REQUEST_CHECK_LOGIN:
                            String[] logins = (String[]) message.getContent();
                            String checkLoginUsername = logins[0];
                            String checkLoginPassword = logins[1];
                            server.checkLoginPlayer(this, checkLoginUsername, checkLoginPassword);
                            break;
                        case REQUEST_LOGIN:
                            String loginUsername = (String) message.getContent();
                            server.loginPlayer(this, loginUsername);
                            break;
                        case REQUEST_LOGOUT:
                            String logoutUsername = (String) message.getContent();
                            server.logoutPlayer(logoutUsername);
                        case INFO:
                            break;
                    }
                } else {
                    server.disconnectClient(this);
                    isActive = false;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                server.disconnectClient(this);
                isActive = false;
            }
        }
    }

    /**
     * Méthode ajoutant une carte à la main du joueur
     *
     * @param card Carte à ajouter
     */
    public void addCard(UnoCard card) {
        cards.add(card);
    }

    /**
     * Méthode supprimant une carte de la main du joueur
     *
     * @param cardToDelete Carte à supprimer
     */
    public void removeCard(UnoCard cardToDelete) {
        int cardIndex = searchCard(cardToDelete);
        if (cardIndex != -1) {
            cards.remove(cardIndex);
        }
    }

    /**
     * Méthode cherchant une carte dans la liste de cartes
     *
     * @param cardToSearch Carte à chercher
     * @return Index de la carte
     */
    private int searchCard(UnoCard cardToSearch) {
        for (int i = 0; i < cards.size(); i++) {
            UnoCard card = cards.get(i);
            if ((card.getColor().equals(cardToSearch.getColor()) || card.getColor().equals(UnoCardColor.SPECIAL)) &&
                    server.cardsHasSameName(card, cardToSearch)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Fonction d'envoi de message
     *
     * @param message Message envoyé
     */
    void sendMessage(Message message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode d'envoi de message
     *
     * @param messageType Type de message
     * @param content     contenu du message
     */
    void sendMessage(MessageType messageType, Object content) {
        sendMessage(new Message(messageType, "server", content));
    }

    /**
     * Fonction de fermeture de connexion
     */
    public void closeClient() {
        try {
            in.close();
            out.close();
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UnoServer getServer() {
        return server;
    }

    public void setServer(UnoServer server) {
        this.server = server;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public void setIn(ObjectInputStream in) {
        this.in = in;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void setOut(ObjectOutputStream out) {
        this.out = out;
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

    public boolean isWantToPlay() {
        return wantToPlay;
    }

    public void setWantToPlay(boolean wantToPlay) {
        this.wantToPlay = wantToPlay;
    }
}
