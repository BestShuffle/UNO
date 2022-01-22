package fr.uno.client.graphic.controller;

import fr.uno.client.UnoClientConstants;
import fr.uno.client.graphic.UnoCardView;
import fr.uno.client.service.ServiceClient;
import fr.uno.common.game.UnoCard;
import fr.uno.common.game.UnoCardSpecial;
import fr.uno.common.game.UnoCardSpecialEffect;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Classe controlleur de la fenêtre game
 */
public class GameFrameController implements UnoClientConstants, Initializable {
    private static final transient Logger log = Logger.getLogger(GameFrameController.class);
    @FXML
    public VBox leftPlayerCardsContainer;
    public HBox mainPlayerCardsContainer;
    public ImageView imageViewCardsPile;
    public ImageView imageViewLastPlayedCard;
    public Label labelHasToPlay;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCards();
    }

    /**
     * Méthode de chargement des cartes
     */
    private void loadCards() {
        loadPile();
        loadMainPlayerCards();
    }

    /**
     * Chargement de la pile de cartes
     */
    private void loadPile() {
        imageViewCardsPile.setImage(new UnoCardView("other", "face").getImage());

        imageViewCardsPile.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            log.info("Demande de pioche d'une carte");
            ServiceClient.getInstance().requestToPickCard();
            event.consume();
        });

        log.info("Game frame controller = " + ServiceClient.getInstance().getClient().getMixedCardsNumber());
        log.info("Controller Pile size = " + ServiceClient.getInstance().getClient().getMixedCardsNumber());
    }

    /**
     * Méthode de chargement des cartes du joueur principal
     */
    private void loadMainPlayerCards() {
        for (UnoCard card : ServiceClient.getInstance().getClient().getCards()) {
            addCardInHand(card);
        }
    }

    /**
     * Méthode d'ajout de carte piochée
     * @param cardToPick Carte piochée
     */
    public void pickCard(UnoCard cardToPick) {
        log.info("Carte piochée : " + cardToPick);
        addCardInHand(cardToPick);
    }

    /**
     * Méthode jouant la carte reçu en paramètres
     * @param cardToPlay Carte à jouer
     */
    public void playCard(UnoCard cardToPlay) {
        log.info("Carte jouée devant être affichée : " + cardToPlay);
        imageViewLastPlayedCard.setImage(new UnoCardView(cardToPlay).getImage());
    }

    /**
     * Méthode d'ajout de carte dans la main
     * @param card Carte à ajouter
     */
    private void addCardInHand(UnoCard card) {
        UnoCardView unoCardView = new UnoCardView(card);

        /**
         * Évènement de clic sur la carte
         */
        unoCardView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            log.info("Demande pour jouer la carte : " + card);
            if (card instanceof UnoCardSpecial) {
                UnoCardSpecial specialCard = (UnoCardSpecial) card;
                if (specialCard.getEffect().equals(UnoCardSpecialEffect.CHANGE_COLOR) ||
                        specialCard.getEffect().equals(UnoCardSpecialEffect.PLUS_4)) {
                    ServiceClient.getInstance().requestToChangeColor(specialCard);
                } else {
                    ServiceClient.getInstance().requestToPlayCardSpecial(specialCard);
                }
            } else {
                ServiceClient.getInstance().requestToPlayCard(card);
            }
            event.consume();
        });

        mainPlayerCardsContainer.getChildren().add(unoCardView);
    }

    /**
     * Méthode supprimant une carte de la main du joueur
     * @param cardToRemove Carte à supprimer
     */
    public void removeCardFromHand(UnoCard cardToRemove) {
        UnoCardView unoCardViewToRemove = null;
        for (Node unoCardNode : mainPlayerCardsContainer.getChildren()) {
            UnoCardView unoCardView = (UnoCardView) unoCardNode;
            if (unoCardView.getCard().getColor().equals(cardToRemove.getColor()) &&
                    unoCardView.getCard().getName().equals(cardToRemove.getName())) {
                unoCardViewToRemove = unoCardView;
            }
        }

        if (unoCardViewToRemove != null) {
            log.info("Carte à supprimer du paquet : " + cardToRemove);
            mainPlayerCardsContainer.getChildren().remove(unoCardViewToRemove);
        }
    }

    /**
     * Méthode avertissant graphiquement que le joueur doit jouer
     */
    public void showHasToPlay() {
        labelHasToPlay.setText("A vous de jouer !");
    }

    /**
     * Méthode avertissant graphiquement que le joueur doit jouer
     */
    public void removeHasToPlay() {
        labelHasToPlay.setText("");
    }
}
