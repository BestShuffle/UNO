package fr.uno.client.graphic.controller;

import fr.uno.client.UnoClientDisplays;
import fr.uno.client.service.ServiceClient;
import fr.uno.client.service.ServiceWindow;
import fr.uno.common.game.UnoResult;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

import java.util.ArrayList;

/**
 * Classe controlleur de la page de fin de partie
 */
public class GameFinishedController implements UnoClientDisplays {

    @FXML
    private TableView tableViewScores;

    /**
     * Méthode affichant le tableau des scores
     *
     * @param playersResults Résultats des joueurs
     */
    public void loadScoreTable(ArrayList<UnoResult> playersResults) {
        for (UnoResult playerResult : playersResults) {
            tableViewScores.getItems().add(playerResult);
        }
    }

    /**
     * Méthode gérant l'évènement de clic sur le bouton de retour à l'accueil
     *
     * @param actionEvent Évènement
     */
    @FXML
    private void handleBackButtonAction(ActionEvent actionEvent) {
        ServiceWindow.getInstance().loadLobby();
    }
}
