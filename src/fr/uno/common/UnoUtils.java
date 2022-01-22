package fr.uno.common;

import fr.uno.common.game.UnoCard;

public class UnoUtils {

    /**
     * Méthode permettant de détecter le nom d'une carte
     *
     * @param card Carte
     * @return Nom détecté
     */
    public static String detectCardName(UnoCard card) {
        String cardName = card.getName();
        if (cardName.contains("_")) {
            return cardName.substring(0, cardName.indexOf("_"));
        } else {
            return cardName;
        }
    }
}
