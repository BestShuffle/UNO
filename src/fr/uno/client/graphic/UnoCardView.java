package fr.uno.client.graphic;

import fr.uno.client.UnoClientConstants;
import fr.uno.common.game.UnoCard;
import fr.uno.common.game.UnoCardColor;
import fr.uno.common.game.UnoCardSpecial;
import fr.uno.common.game.UnoCardSpecialEffect;
import javafx.beans.DefaultProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Classe d'objet visuel UnoCard
 */
@DefaultProperty("image")
public class UnoCardView extends ImageView implements UnoClientConstants {

    private UnoCard card;

    /**
     * M�thode construisant un objet visuel UnoCard
     *
     * @param card Carte � initialiser graphiquement
     */
    public UnoCardView(UnoCard card) {
        this.card = card;

        try {
            String cardFilePath = FOLDER_IMAGES + "/" + card.getColor() + "/" + card.getName() + "." + IMAGES_EXTENSION;
            // Carte sp�ciale mais uniquement de couleur ont un chemin sp�cifique
            if (card instanceof UnoCardSpecial) {
                UnoCardSpecial specialCard = (UnoCardSpecial) card;
                // Les cartes sp�ciales de couleur sont g�r�es plus haut
                if (!specialCard.getColor().equals(UnoCardColor.SPECIAL)) {
                    // Gestion des cartes sp�ciales g�n�rales apr�s choix de couleur
                    if (specialCard.getEffect().equals(UnoCardSpecialEffect.PLUS_4) ||
                            specialCard.getEffect().equals(UnoCardSpecialEffect.CHANGE_COLOR)) {
                        cardFilePath = FOLDER_IMAGES + "/" + UnoCardColor.OTHER + "/" + specialCard.getName() +
                                "_" + specialCard.getColor() + "." + IMAGES_EXTENSION;
                    } else {
                        // Gestion des cartes +2
                        cardFilePath = FOLDER_IMAGES + "/" + card.getColor() + "/" + UnoCardColor.SPECIAL + "/" +
                                card.getName() + "." + IMAGES_EXTENSION;
                    }
                }
            }
            FileInputStream input = new FileInputStream(cardFilePath);
            Image image = new Image(input);
            this.setImage(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * M�thode construisant un objet visuel UnoCard
     *
     * @param folderName Nom du dossier
     * @param name       Nom du fichier
     */
    public UnoCardView(String folderName, String name) {
        this.card = new UnoCard(UnoCardColor.valueOf(folderName.toUpperCase()), name);

        try {
            FileInputStream input = new FileInputStream("images/" + folderName + "/" + name + ".png");
            Image image = new Image(input);
            this.setImage(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public UnoCard getCard() {
        return card;
    }

    public void setCard(UnoCard card) {
        this.card = card;
    }
}
