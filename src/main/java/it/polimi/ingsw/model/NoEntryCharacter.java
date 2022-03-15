package it.polimi.ingsw.model;

public class NoEntryCharacter extends Character {
    private int noEntry;

    public NoEntryCharacter(int cost, String description, int id) {
        super(cost, description, id);
        noEntry = 4;
    }

    public int getNoEntry() {
        return noEntry;
    }

    public void addNoEntry() {
        ++noEntry;
    }

    public void removeNoEntry() {
        --noEntry;
    }
}
