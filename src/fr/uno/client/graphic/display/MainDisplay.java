package fr.uno.client.graphic.display;

import fr.uno.client.UnoClientConstants;
import fr.uno.client.service.ServiceClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe de gestion de la fenêtre principale
 */
public class MainDisplay extends Application implements UnoClientConstants {

    private static Stage stage;

    /**
     * Méthode d'affichage de la fenêtre principale
     */
    public static void show() {
        launch();
    }

    /**
     * Méthode de fermeture de la fenêtre principale
     */
    public static void close() {
        stage.close();
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        MainDisplay.stage = stage;
    }

    /**
     * Méthode de chargement de la fenêtre d'affichage
     *
     * @param primaryStage Conteneur principal
     * @throws IOException Erreur de chargement
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(MAIN_FXML_PATH));
        Scene scene = new Scene(root, 400, 250);
//        scene.getStylesheets().add("./css/style.css");

        stage = primaryStage;
        stage.setTitle("UNO - Connexion");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> {
            ServiceClient.getInstance().disconnect();
        });

        // Création du client
        ServiceClient.getInstance().createClient();
        ServiceClient.getInstance().connect();
    }
}
