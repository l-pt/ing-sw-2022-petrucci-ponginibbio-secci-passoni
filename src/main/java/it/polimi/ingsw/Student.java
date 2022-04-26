package it.polimi.ingsw;

import it.polimi.ingsw.PawnColor;

public class Student {
    private final PawnColor color;

    public Student(PawnColor color) {
        this.color = color;
    }

    public PawnColor getColor() {
        return color;
    }
}