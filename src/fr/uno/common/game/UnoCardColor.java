package fr.uno.common.game;

public enum UnoCardColor {

    BLUE("blue"),
    GREEN("green"),
    SPECIAL("special"),
    RED("red"),
    YELLOW("yellow"),
    OTHER("other");

    private String color;

    UnoCardColor(String color) {
        this.color = color;
    }

    public String toString() {
        return color;
    }
}
