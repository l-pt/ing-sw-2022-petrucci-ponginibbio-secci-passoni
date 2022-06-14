package it.polimi.ingsw.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Student {
    private final PawnColor color;

    public Student(PawnColor color) {
        this.color = color;
    }

    public PawnColor getColor() {
        return color;
    }
}