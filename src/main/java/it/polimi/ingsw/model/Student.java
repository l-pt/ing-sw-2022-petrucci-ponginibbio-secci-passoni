package it.polimi.ingsw.model;

import it.polimi.ingsw.model.PawnColor;

public class Student {
    private final PawnColor color;

    public Student(PawnColor color) {
        this.color = color;
    }

    public PawnColor getColor() {
        return color;
    }
}