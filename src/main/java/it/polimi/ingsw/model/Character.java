package it.polimi.ingsw.model;

public class Character {
    private int id;
    private int cost;
    private String description;
    private boolean coin;

    public Character(int cost, String description, int id) {
        this.id = id;
        this.cost = cost;
        this.description = description;
        coin = false;
    }
    public int getId() {
        return id;
    }

    public int getCost() {
        return cost;
    }

    public String getDescription() {
        return description;
    }

    public void incrementCost() {
        if(!coin) {
            coin = true;
            ++cost;
        }
    }
}
