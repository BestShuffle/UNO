package fr.uno.client;

/**
 * Interface contenant les constantes du projet
 */
public interface UnoClientConstants {
    //	String PATH_JSON_CARDS = "./config/cards.json" ;

    int LOBBY_WIDTH = 400;
    int LOBBY_HEIGHT = 250;
    int GAME_FINISHED_WIDTH = 400;
    int GAME_FINISHED_HEIGHT = 500;
    int GAME_WIDTH = 800;
    int GAME_HEIGHT = 500;
    int INSCRIPTION_WIDTH = 350;
    int INSCRIPTION_HEIGHT = 250;

    String FOLDER_IMAGES = "images";
    String IMAGES_EXTENSION = "png";

    String FXML_PATH = "/fr/uno/client/graphic/fxml";

    String MAIN_FXML_PATH = FXML_PATH + "/ConnectFrame.fxml";
    String LOBBY_FXML_PATH = FXML_PATH + "/LobbyFrame.fxml";
    String REGISTER_FXML_PATH = FXML_PATH + "/RegisterFrame.fxml";

    String GAME_FXML_PATH = FXML_PATH + "/GameFrame.fxml";
    String GAME_FINISHED_FXML_PATH = FXML_PATH + "/GameFinishedFrame.fxml";
    String LOADING_FXML_PATH = FXML_PATH + "/LoadingFrame.fxml";
}


