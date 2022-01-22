package fr.uno.client;

import fr.uno.client.graphic.display.FXMLDisplay;

/**
 * Interface contenant les constantes de fenêtres du projet
 */
public interface UnoClientDisplays extends UnoClientConstants {
    FXMLDisplay DISPLAY_LOBBY = new FXMLDisplay("UNO - Lobby", LOBBY_FXML_PATH, LOBBY_WIDTH, LOBBY_HEIGHT);
    FXMLDisplay DISPLAY_REGISTER = new FXMLDisplay("UNO - Inscription", REGISTER_FXML_PATH, INSCRIPTION_WIDTH, INSCRIPTION_HEIGHT);
}