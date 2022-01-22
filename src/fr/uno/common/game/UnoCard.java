package fr.uno.common.game;

import java.io.Serializable;

public class UnoCard implements Serializable {

    private UnoCardColor color;
    private String name;

    /**
     * Méthode construisant un objet de carte Uno
     *
     * @param color Couleur
     * @param name Nom
     */
    public UnoCard(UnoCardColor color, String name) {
        super();
        this.color = color;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UnoCardColor getColor() {
        return color;
    }

    public void setColor(UnoCardColor color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "UnoCard{" +
                "color='" + color + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
