package fr.uno.client.graphic.controller;

import fr.uno.client.UnoClientConstants;
import fr.uno.client.UnoClientDisplays;
import fr.uno.client.service.ServiceClient;
import fr.uno.client.service.ServiceWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * Classe controlleur de la fen�tre de lobby
 */
public class LobbyFrameController implements UnoClientConstants, UnoClientDisplays {

    /**
     * M�thode g�rant l'�v�nement de clic sur le bouton de connexion � une partie
     *
     * @param actionEvent �v�nement
     */
    @FXML
    private void handlePlayButton(ActionEvent actionEvent) {
        ServiceWindow.getInstance().loadLoading();
        ServiceClient.getInstance().requestToPlay();
    }
}
