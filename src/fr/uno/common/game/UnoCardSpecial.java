package fr.uno.common.game;

public class UnoCardSpecial extends UnoCard {

    private UnoCardSpecialEffect effect;

    /**
     * M�thode construisant un objet de carte Uno sp�ciale
     *
     * @param color Couleur
     * @param name Nom
     * @param effect Effet
     */
    public UnoCardSpecial(UnoCardColor color, String name, UnoCardSpecialEffect effect) {
        super(color, name);
        this.effect = effect;
    }

    public UnoCardSpecialEffect getEffect() {
        return this.effect;
    }
}
