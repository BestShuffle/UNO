package fr.uno.common.game;

/**
 * Enum�ration contenant les diff�rents effets des cartes sp�ciales de l'application
 */
public enum UnoCardSpecialEffect {

    PLUS_4("+4"),
    PLUS_2("+2"),
    CHANGE_COLOR("changecolor"),
    REVERSE("reverse"),
    SKIP("skip");

    private String effect;

    UnoCardSpecialEffect(String effect) {
        this.effect = effect;
    }

    public String toString() {
        return effect;
    }
}
