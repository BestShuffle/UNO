package fr.uno.client.graphic.display;

import fr.uno.client.UnoClientUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Classe de fenêtre
 */
public class FXMLDisplay {
    private static final transient Logger log = Logger.getLogger(FXMLDisplay.class);

    private Stage stage;
    private Stage parentStage;

    private String frameTitle;
    private String fxmlPath;
    private Integer width;
    private Integer height;

    private FXMLLoader fxmlLoader;

    /**
     * Méthode de création de l'objet fenêtre
     *
     * @param frameTitle Titre
     * @param fxmlPath   Chemin d'accès au modèle
     * @param width      Largeur
     * @param height     Hauteur
     */
    public FXMLDisplay(String frameTitle, String fxmlPath, Integer width, Integer height) {
        this.frameTitle = frameTitle;
        this.fxmlPath = fxmlPath;
        this.width = width;
        this.height = height;
    }

    /**
     * Méthode de chargement puis d'affichage de la fenêtre
     */
    public void show() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));

            Scene scene = new Scene(root, (width != null) ? width : 400, (height != null) ? height : 250);
            stage = new Stage();

            if (parentStage != null) {
                // Définition du type de modalité de la fenêtre
                stage.initModality(Modality.WINDOW_MODAL);

                // Définition du parent de la fenêtre
                stage.initOwner(parentStage);
            }

            stage.setTitle((frameTitle != null) ? frameTitle : "FXML Display");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            UnoClientUtils.popupError("Erreur lors du chargement du fichier FXML : " + fxmlPath);
        }
    }

    /**
     * Méthode de fermeture de la fenêtre
     */
    public void close() {
        if (stage != null) {
            stage.close();
        }
    }

    /**
     * Méthode de redimension de fenêtre
     *
     * @param width  Largeur
     * @param height Hauteur
     */
    public void resize(double width, double height) {
        stage.setWidth(width);
        stage.setHeight(height);
    }

    /**
     * Méthode définissant si la fenêtre est redimensionnable ou non
     *
     * @param resizable Est redimensionnable ?
     */
    public void setResizable(boolean resizable) {
        stage.setResizable(resizable);
    }

    /**
     * Méthode définissant le titre de la fenêtre
     *
     * @param title Titre
     */
    public void setTitle(String title) {
        stage.setTitle(title);
    }

    /**
     * Méthode de chargement de fichier FXML dans la fenêtre
     *
     * @param fxmlPathToLoad Fichier FXML à charger
     */
    public void loadFxml(String fxmlPathToLoad) {
        try {
            fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPathToLoad));
            stage.getScene().setRoot(fxmlLoader.load());
        } catch (IOException e) {
            UnoClientUtils.popupError("Erreur lors du chargement du fichier FXML : " + fxmlPathToLoad);
            e.printStackTrace();
        }
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }

    public void setFxmlPath(String fxmlPath) {
        this.fxmlPath = fxmlPath;
    }

    public String getFrameTitle() {
        return frameTitle;
    }

    public void setFrameTitle(String frameTitle) {
        this.frameTitle = frameTitle;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Stage getParentStage() {
        return parentStage;
    }

    public void setParentStage(Stage parentStage) {
        this.parentStage = parentStage;
    }

    public FXMLLoader getFxmlLoader() {
        return fxmlLoader;
    }

    public void setFxmlLoader(FXMLLoader fxmlLoader) {
        this.fxmlLoader = fxmlLoader;
    }
}
