package it.polimi.ingsw.model;

public class Character {
    private int cost;
    private String description;
    private int id;
    private boolean coin;

    public Character(int cost, String description, int id) {
        this.cost = cost;
        this.description = description;
        this.id = id;
        coin = false;
    }

    public int getCost() {
        return cost;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public void useAbility() {
        switch(id){
            case 0:

                break;
        }
    }

    public void incrementCost() {
        coin = true;
        ++cost;
    }
}
