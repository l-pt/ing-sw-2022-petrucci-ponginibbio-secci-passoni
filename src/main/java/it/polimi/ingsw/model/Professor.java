package it.polimi.ingsw.model;

public class Professor {
    private final PawnColor color;

    public Professor(PawnColor color) {
        this.color = color;
    }

    /**
     * getColor()
     * @return The color of the professor
     */
    public PawnColor getColor() {
        return color;
    }
}
