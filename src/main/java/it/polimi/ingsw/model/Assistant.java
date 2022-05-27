package it.polimi.ingsw.model;

public class Assistant {
    private int value;
    private int moves;

    public Assistant(int value, int moves) {
        this.value = value;
        this.moves = moves;
    }

    public int getValue() {
        return value;
    }

    public int getMoves() {
        return moves;
    }
}