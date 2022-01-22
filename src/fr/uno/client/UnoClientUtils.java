package fr.uno.client;

import fr.uno.common.game.UnoCardColor;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Classe contenant les méthodes utilitaires
 */
public class UnoClientUtils {
    private final transient Logger log = Logger.getLogger(UnoClientUtils.class);

    /**
     * Méthode ouvrant une fenêtre de popup d'information
     *
     * @param headerText  Texte d'en-tête
     * @param contentText Contenu
     */
    public static void popupInfo(String headerText, String contentText) {
        popup(headerText, contentText, Alert.AlertType.INFORMATION);
    }

    /**
     * Méthode ouvrant une fenêtre de popup d'information
     *
     * @param headerText  Texte d'en-tête
     * @param contentText Contenu
     */
    public static void popupWarn(String headerText, String contentText) {
        popup(headerText, contentText, Alert.AlertType.WARNING);
    }

    /**
     * Méthode ouvrant une fenêtre de popup d'information
     *
     * @param headerText  Texte d'en-tête
     * @param contentText Contenu
     */
    public static void popupError(String headerText, String contentText) {
        popup(headerText, contentText, Alert.AlertType.ERROR);
    }

    /**
     * Méthode ouvrant une fenêtre de popup d'information
     *
     * @param contentText Contenu
     */
    public static void popupError(String contentText) {
        popupError("Erreur", contentText);
    }

    /**
     * Méthode ouvrant une fenêtre de popup d'erreur SQL
     *
     * @param contentText Contenu
     */
    public static void popupErrorSQL(String contentText) {
        popupError("Erreur SQL", contentText);
    }

    /**
     * Méthode ouvrant une popup du type choisit
     *
     * @param headerText  Texte d'en-tête
     * @param contentText Contenu
     * @param alertType   Type d'alerte
     */
    public static void popup(String headerText, String contentText, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType.toString());
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    /**
     * Méthode affichant un popup permettant de choisir une couleur
     *
     * @return Couleur choisie
     */
    public static String popupChooseColor() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Choix d'une couleur");
        alert.setHeaderText("Choix d'une couleur");
        alert.setContentText("Choisissez une couleur");

        ArrayList<ButtonType> listButtons = new ArrayList<>();
        for (UnoCardColor color : UnoCardColor.values()) {
            if (!color.equals(UnoCardColor.SPECIAL) && !color.equals(UnoCardColor.OTHER)) {
                ButtonType buttonColor = new ButtonType(color.toString());
                listButtons.add(buttonColor);
            }
        }

        ButtonType buttonTypeCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        listButtons.add(buttonTypeCancel);

        alert.getButtonTypes().

                setAll(listButtons);

        Optional<ButtonType> result = alert.showAndWait();

        Logger tmpLog = Logger.getLogger(UnoClientUtils.class);
        if (result.get() != buttonTypeCancel) {
            tmpLog.info(result.get());
            return result.get().getText();
        }
        return null;
    }

    /**
     * Méthode de lecture du dossier contenant les cartes
     */
    private void readCardFolder() {
        URL imageFolderUrl = this.getClass().getResource("/images");
        try {
            File imageFolder = new File(imageFolderUrl.toURI());
            log.info(imageFolder);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
