package fr.uno.client.object;

import fr.uno.client.UnoClientDisplays;
import fr.uno.client.UnoClientUtils;
import fr.uno.client.graphic.controller.GameFinishedController;
import fr.uno.client.graphic.controller.GameFrameController;
import fr.uno.client.graphic.display.MainDisplay;
import fr.uno.client.service.ServiceClient;
import fr.uno.client.service.ServiceWindow;
import fr.uno.common.game.Message;
import fr.uno.common.game.UnoCard;
import fr.uno.common.game.UnoResult;
import javafx.application.Platform;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe de gestion de la réception des messages par le client
 */
public class UnoClientReceive implements UnoClientDisplays, Runnable {
    private final transient Logger log = Logger.getLogger(UnoClientReceive.class);

    private UnoClient oClient;

    /**
     * Méthode de construction du gestionnaire de réception de requêtes
     *
     * @param oClient Client récepteur
     */
    UnoClientReceive(UnoClient oClient) {
        super();
        this.oClient = oClient;
    }

    public void run() {
        boolean bIsActive = true;
        while (bIsActive) {
            try {
                Message message = (Message) oClient.getoIn().readObject();
                switch (Objects.requireNonNull(message).getMessageType()) {
                    case REGISTERED:
                        String registerUsername = (String) message.getContent();
                        Platform.runLater(() -> {
                            UnoClientUtils.popupInfo("Enregistrement réussi", "L'utilisateur " +
                                    registerUsername + " a bien été enregistré.");
                            ServiceWindow.getInstance().closeRegister();
                        });
                        break;
                    case REQUEST_CHECK_LOGIN:
                        String username = (String) message.getContent();
                        ServiceClient.getInstance().connectPlayer(username);
                        break;
                    case ALREADY_CONNECTED:
                        Platform.runLater(() -> UnoClientUtils.popupWarn("Erreur d'authentification",
                                "Compte déjà connecté."));
                        break;
                    case REQUEST_LOGIN:
                        String loginUsername = (String) message.getContent();
                        //ServiceDatabase.getInstance().checkLogPlayer(loginUsername);
                        ServiceClient.getInstance().setUsername(loginUsername);

                        Platform.runLater(() -> {
                            MainDisplay.close();
                            ServiceWindow.getInstance().showLobby();
                        });
                        break;
                    case BAD_LOGIN:
                        Platform.runLater(() -> UnoClientUtils.popupWarn("Erreur d'authentification",
                                "Nom d'utilisateur ou mot de passe incorrect."));
                        break;
                    case REQUEST_USERNAME:
                        ServiceClient.getInstance().sendUsername();
                        break;
                    case REFUSE_CONNECT:
                        log.info("Refus de connexion !");
                        Platform.runLater(() -> UnoClientUtils.popupInfo("Connexion refusée",
                                "Une partie est en cours ! Patientez avant de pouvoir jouer."));
                        ServiceWindow.getInstance().loadLobby();
                        break;
                    case GAME_START:
                        log.info("La partie démarre !");
                        ServiceWindow.getInstance().loadGame();
                        break;
                    case GAME_FINISHED:
                        log.info("Partie terminée !");
                        Platform.runLater(() -> {
                            ServiceWindow.getInstance().loadGameFinished();
                            GameFinishedController gameFinishedController = DISPLAY_LOBBY.getFxmlLoader().getController();
                            // Permet d'afficher le total des points malgré le fait que
                            // la demande provienne d'un autre Thread
                            ArrayList<UnoResult> playersResults = (ArrayList<UnoResult>) message.getContent();
                            Platform.runLater(() -> {
                                gameFinishedController.loadScoreTable(playersResults);
                            });
                        });
                        break;
                    case HAS_NOT_TO_PLAY:
                        GameFrameController gameFrameController = DISPLAY_LOBBY.getFxmlLoader().getController();

                        // Permet d'avertir le joueur graphiquement que ce n'est pas à lui de jouer malgré
                        // le fait que la demande provienne d'un autre Thread
                        Platform.runLater(gameFrameController::removeHasToPlay);
                        break;
                    case HAS_TO_PLAY:
                        gameFrameController = DISPLAY_LOBBY.getFxmlLoader().getController();

                        // Permet d'avertir le joueur graphiquement qu'il doit jouer malgré le
                        // fait que la demande provienne d'un autre Thread
                        Platform.runLater(gameFrameController::showHasToPlay);
                        break;
                    case GIVE_CARDS:
                        oClient.setCards((List<UnoCard>) message.getContent());
                        break;
                    case PICK_CARD:
                        UnoCard cardToPick = (UnoCard) message.getContent();
                        gameFrameController = DISPLAY_LOBBY.getFxmlLoader().getController();

                        // Permet l'ajout de la carte malgré le fait que la demande provienne d'un autre Thread
                        Platform.runLater(() -> gameFrameController.pickCard(cardToPick));
                        break;
                    case PLAY_CARD:
                        UnoCard cardToPlay = (UnoCard) message.getContent();
                        gameFrameController = DISPLAY_LOBBY.getFxmlLoader().getController();

                        // Permet l'ajout de la carte malgré le fait que la demande provienne d'un autre Thread
                        Platform.runLater(() -> gameFrameController.playCard(cardToPlay));
                        break;
                    case REMOVE_CARD:
                        UnoCard cardToRemove = (UnoCard) message.getContent();
                        gameFrameController = DISPLAY_LOBBY.getFxmlLoader().getController();

                        // Permet la suppression malgré le fait que la demande provienne d'un autre Thread
                        Platform.runLater(() -> gameFrameController.removeCardFromHand(cardToRemove));
                        break;
                    case GET_CARDS_NUMBER:
                        log.info("Message reçu du serveur  :" + message.getContent());
                        oClient.setMixedCardsNumber((Integer) message.getContent());
                        break;
                    case CHAT:
                        log.info(oClient.messageReceived(message));
                        break;
                    case INFO:
                        oClient.messageReceived(message);
                        break;
                }

                bIsActive = oClient.isConnected();
            } catch (Exception e) {
                bIsActive = false;
                oClient.disconnect();
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
        oClient.disconnect();
    }

    public UnoClient getoClient() {
        return oClient;
    }

    public void setoClient(UnoClient oClient) {
        this.oClient = oClient;
    }
}
