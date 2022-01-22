package fr.uno.client.graphic.controller;

import fr.uno.client.UnoClientDisplays;
import fr.uno.client.service.ServiceClient;
import fr.uno.client.service.ServiceWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Classe controlleur de la fenêtre de connexion
 */
public class ConnectFrameController implements UnoClientDisplays {
    @FXML
    private TextField fieldUsername;
    @FXML
    private PasswordField fieldPassword;

    /**
     * Méthode gérant l'évènement de clic sur le bouton de connexion
     *
     * @param actionEvent Évènement
     */
    @FXML
    private void handleSubmitButtonConnectionAction(ActionEvent actionEvent) {
        String username = fieldUsername.getText();
        String password = fieldPassword.getText();

        ServiceClient.getInstance().logPlayer(username, password);
    }

    /**
     * Méthode gérant l'évènement de clic sur le bouton ouvrant la page d'inscription
     *
     * @param actionEvent Évènement
     */
    public void handleRegisterButtonConnectionAction(ActionEvent actionEvent) {
        ServiceWindow.getInstance().showRegister();
    }
}
