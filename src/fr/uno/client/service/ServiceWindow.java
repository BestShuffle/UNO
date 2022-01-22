package fr.uno.client.service;

import fr.uno.client.UnoClientDisplays;
import fr.uno.client.graphic.display.MainDisplay;
import javafx.application.Platform;

/**
 * Classe de gestion des fenêtres sous pattern Singleton
 */
public class ServiceWindow implements UnoClientDisplays {

    // Instance unique
    private static volatile ServiceWindow instance;

    /**
     * Méthode de récupération de l'instance unique
     *
     * @return Instance unique
     */
    public static ServiceWindow getInstance() {
        // Méthode pour éviter le synchronized qui est coûteux
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
     * Méthode affichant la fenêtre de connexion
     */
    public void showConnect() {
        MainDisplay.show();
    }

    /**
     * Méthode affichant la fenêtre lobby
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
     * Méthode affichant la fenêtre d'inscription
     */
    public void showRegister() {
        DISPLAY_REGISTER.setParentStage(MainDisplay.getStage());
        DISPLAY_REGISTER.show();
        DISPLAY_REGISTER.setResizable(false);
    }

    /**
     * Méthode de chargement du Lobby
     */
    public void loadLobby() {
        DISPLAY_LOBBY.loadFxml(LOBBY_FXML_PATH);
        DISPLAY_LOBBY.resize(LOBBY_WIDTH, LOBBY_HEIGHT);
        DISPLAY_LOBBY.setResizable(false);
    }

    /**
     * Méthode de chargement de la page de chargement
     */
    public void loadLoading() {
        DISPLAY_LOBBY.loadFxml(LOADING_FXML_PATH);
    }

    /**
     * Méthode de chargement de la page de jeu
     */
    public void loadGame() {
        DISPLAY_LOBBY.loadFxml(GAME_FXML_PATH);
        DISPLAY_LOBBY.resize(GAME_WIDTH, GAME_HEIGHT);
        Platform.runLater(() -> DISPLAY_LOBBY.setResizable(true));
    }

    /**
     * Méthode de chargement de la page de jeu terminé
     */
    public void loadGameFinished() {
        DISPLAY_LOBBY.loadFxml(GAME_FINISHED_FXML_PATH);
        DISPLAY_LOBBY.resize(GAME_FINISHED_WIDTH, GAME_FINISHED_HEIGHT);
        DISPLAY_LOBBY.setResizable(false);
    }

    /**
     * Méthode de fermeture de la fenêtre d'incription
     */
    public void closeRegister() {
        DISPLAY_REGISTER.close();
    }
}
