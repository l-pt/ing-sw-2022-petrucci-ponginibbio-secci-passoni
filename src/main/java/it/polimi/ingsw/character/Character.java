package it.polimi.ingsw.character;

import it.polimi.ingsw.IllegalMoveException;
import it.polimi.ingsw.Player;

public abstract class Character {
    private int id;
    protected int cost;
    private String description;
    private boolean coin;

    public Character(int id, int cost, String description) {
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

    public void checkCost(Player player) throws IllegalMoveException {
        if (player.getCoins() < cost) {
            throw new IllegalMoveException("Insufficient coins");
        }
    }
}
