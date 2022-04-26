package it.polimi.ingsw;

public class Assistant {
    private int value;
    private int moves;
    private Wizard wizard;

    public Assistant(int value, int moves, Wizard wizard) {
        this.value = value;
        this.moves = moves;
        this.wizard = wizard;
    }

    public int getValue() {
        return value;
    }

    public int getMoves() {
        return moves;
    }

    public Wizard getWizard() {
        return wizard;
    }
}