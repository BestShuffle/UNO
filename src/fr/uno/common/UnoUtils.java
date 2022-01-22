package fr.uno.common;

import fr.uno.common.game.UnoCard;

public class UnoUtils {

    /**
     * M�thode permettant de d�tecter le nom d'une carte
     *
     * @param card Carte
     * @return Nom d�tect�
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
