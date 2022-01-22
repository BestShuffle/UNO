package fr.uno.common.game;

/**
 * Enumération contenant les différents effets des cartes spéciales de l'application
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
