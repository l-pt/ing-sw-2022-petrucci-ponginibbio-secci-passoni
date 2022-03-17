package it.polimi.ingsw.model;

public class Assistant {
    private int value;
    private int moves;
    private int type;

    public Assistant(int value, int moves, int type) {
        this.value = value;
        this.moves = moves;
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public int getMoves() {
        return moves;
    }

    public int getType() {
        return type;
    }
}
