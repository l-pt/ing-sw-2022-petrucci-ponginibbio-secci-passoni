package it.polimi.ingsw.model;

import it.polimi.ingsw.model.PawnColor;

public class Professor {
    private final PawnColor color;

    public Professor(PawnColor color) {
        this.color = color;
    }

    public PawnColor getColor() {
        return color;
    }
}
