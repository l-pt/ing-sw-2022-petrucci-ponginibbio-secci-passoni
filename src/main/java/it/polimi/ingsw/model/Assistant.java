package it.polimi.ingsw.model;

public class Assistant {
    private final int value;
    private final int moves;

    public Assistant(int value, int moves) {
        this.value = value;
        this.moves = moves;
    }

    /**
     * getValue()
     * @return The value of the assistant
     */
    public int getValue() {
        return value;
    }

    /**
     * getMoves()
     * @return The moves that mother nature can make if this assistant is used
     */
    public int getMoves() {
        return moves;
    }
}