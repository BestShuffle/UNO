package fr.uno.client.graphic.controller;

import fr.uno.client.UnoClientConstants;
import fr.uno.client.UnoClientDisplays;
import fr.uno.client.service.ServiceClient;
import fr.uno.client.service.ServiceWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * Classe controlleur de la fenêtre de lobby
 */
public class LobbyFrameController implements UnoClientConstants, UnoClientDisplays {

    /**
     * Méthode gérant l'évènement de clic sur le bouton de connexion à une partie
     *
     * @param actionEvent Évènement
     */
    @FXML
    private void handlePlayButton(ActionEvent actionEvent) {
        ServiceWindow.getInstance().loadLoading();
        ServiceClient.getInstance().requestToPlay();
    }
}
