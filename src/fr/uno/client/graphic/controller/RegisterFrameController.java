package fr.uno.client.graphic.controller;

import fr.uno.client.UnoClientUtils;
import fr.uno.client.service.ServiceClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.log4j.Logger;

public class RegisterFrameController {
    private static final transient Logger log = Logger.getLogger(RegisterFrameController.class);
    @FXML
    private TextField fieldUsername;
    @FXML
    private PasswordField fieldPassword;
    @FXML
    private TextField fieldMail;

    public void handleSubmitButtonRegisterAction(ActionEvent actionEvent) {
        String sUsername = fieldUsername.getText();
        String sPassword = fieldPassword.getText();
        String sEmail = fieldMail.getText();
        String usernamePattern = "^[a-zA-Z0-9]{5,}$";
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{6,}$";
        String emailPattern = "^([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)$";
        if (!this.checkRegExp(usernamePattern, sUsername)) {
            // Vérification du nom d'utilisateur
            UnoClientUtils.popupError("Le nom d'utilisateur doit être au minimum de 5 de longueur");
        } else if (!this.checkRegExp(passwordPattern, sPassword)) {
            // Vérification du mot de passe
            UnoClientUtils.popupError("Le mot de passe doit contenir au moins une majuscule et un chiffre et être de 6 de longueur");
        } else if (!this.checkRegExp(emailPattern, sEmail)) {
            // Vérification de l'email
            UnoClientUtils.popupError("Le format d'adresse e-mail est invalide");
        } else {
            Platform.runLater(() -> ServiceClient.getInstance().requestRegister(sUsername, sPassword, sEmail));
        }
    }

    /**
     * Méthode vérifiant un pattern regex
     *
     * @param pattern Pattern à vérifier
     * @param text    Texte à vérifier
     * @return Est validé ?
     */
    private boolean checkRegExp(String pattern, String text) {
        return text.matches(pattern);
    }

    public TextField getFieldUsername() {
        return fieldUsername;
    }

    public PasswordField getFieldPassword() {
        return fieldPassword;
    }

    public TextField getFieldMail() {
        return fieldMail;
    }
}
