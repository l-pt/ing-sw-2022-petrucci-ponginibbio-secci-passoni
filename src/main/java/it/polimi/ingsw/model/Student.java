package it.polimi.ingsw.model;

public class Student {
    private final PawnColor color;

    public Student(PawnColor color) {
        this.color = color;
    }

    /**
     * getColor()
     * @return The color of the student
     */
    public PawnColor getColor() {
        return color;
    }
}