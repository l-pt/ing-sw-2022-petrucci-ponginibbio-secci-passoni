package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.IllegalMoveException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.character.impl.*;

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

    public static Class<? extends Character> getClassFromId(int characterId) throws IllegalMoveException {
        Class<? extends Character> characterClass;
        switch (characterId) {
            case 0 -> characterClass = Character1.class;
            case 1 -> characterClass = Character2.class;
            case 2 -> characterClass = Character3.class;
            case 3 -> characterClass = Character4.class;
            case 4 -> characterClass = Character5.class;
            case 5 -> characterClass = Character6.class;
            case 6 -> characterClass = Character7.class;
            case 7 -> characterClass = Character8.class;
            case 8 -> characterClass = Character9.class;
            case 9 -> characterClass = Character10.class;
            case 10 -> characterClass = Character11.class;
            case 11 -> characterClass = Character12.class;
            default -> throw new IllegalMoveException("Wron character id");
        }
        return characterClass;
    }
}
