package fr.uno.client.service;

import fr.uno.client.UnoClientDisplays;
import fr.uno.client.graphic.display.MainDisplay;
import javafx.application.Platform;

/**
 * Classe de gestion des fen�tres sous pattern Singleton
 */
public class ServiceWindow implements UnoClientDisplays {

    // Instance unique
    private static volatile ServiceWindow instance;

    /**
     * M�thode de r�cup�ration de l'instance unique
     *
     * @return Instance unique
     */
    public static ServiceWindow getInstance() {
        // M�thode pour �viter le synchronized qui est co�teux
        if (ServiceWindow.instance == null) {
            // Anti multi instanciation par multi thread
            synchronized (ServiceWindow.class) {
                if (ServiceWindow.instance == null) {
                    ServiceWindow.instance = new ServiceWindow();
                }
            }
        }
        return ServiceWindow.instance;
    }

    /**
     * M�thode affichant la fen�tre de connexion
     */
    public void showConnect() {
        MainDisplay.show();
    }

    /**
     * M�thode affichant la fen�tre lobby
     */
    public void showLobby() {
        DISPLAY_LOBBY.show();
        DISPLAY_LOBBY.setResizable(false);
        DISPLAY_LOBBY.getStage().setOnCloseRequest(event -> {
            ServiceClient.getInstance().disconnectPlayer();
            ServiceClient.getInstance().disconnect();
        });
    }

    /**
     * M�thode affichant la fen�tre d'inscription
     */
    public void showRegister() {
        DISPLAY_REGISTER.setParentStage(MainDisplay.getStage());
        DISPLAY_REGISTER.show();
        DISPLAY_REGISTER.setResizable(false);
    }

    /**
     * M�thode de chargement du Lobby
     */
    public void loadLobby() {
        DISPLAY_LOBBY.loadFxml(LOBBY_FXML_PATH);
        DISPLAY_LOBBY.resize(LOBBY_WIDTH, LOBBY_HEIGHT);
        DISPLAY_LOBBY.setResizable(false);
    }

    /**
     * M�thode de chargement de la page de chargement
     */
    public void loadLoading() {
        DISPLAY_LOBBY.loadFxml(LOADING_FXML_PATH);
    }

    /**
     * M�thode de chargement de la page de jeu
     */
    public void loadGame() {
        DISPLAY_LOBBY.loadFxml(GAME_FXML_PATH);
        DISPLAY_LOBBY.resize(GAME_WIDTH, GAME_HEIGHT);
        Platform.runLater(() -> DISPLAY_LOBBY.setResizable(true));
    }

    /**
     * M�thode de chargement de la page de jeu termin�
     */
    public void loadGameFinished() {
        DISPLAY_LOBBY.loadFxml(GAME_FINISHED_FXML_PATH);
        DISPLAY_LOBBY.resize(GAME_FINISHED_WIDTH, GAME_FINISHED_HEIGHT);
        DISPLAY_LOBBY.setResizable(false);
    }

    /**
     * M�thode de fermeture de la fen�tre d'incription
     */
    public void closeRegister() {
        DISPLAY_REGISTER.close();
    }
}
