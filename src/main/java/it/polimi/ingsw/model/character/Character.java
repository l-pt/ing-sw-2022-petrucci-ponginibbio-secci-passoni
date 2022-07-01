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

    /**
     * getId()
     * @return The id of the character
     */
    public int getId() {
        return id;
    }

    /**
     * getCost()
     * @return The cost of activation of the character
     */
    public int getCost() {
        return cost;
    }

    /**
     * getDescription()
     * @return The description of the ability of the character
     */
    public String getDescription() {
        return description;
    }

    /**
     * Increments the cost of the character by 1 (can be increased only 1 time)
     */
    public void incrementCost() {
        if(!coin) {
            coin = true;
            ++cost;
        }
    }

    /**
     * getCoin()
     * @return True if the cost of the character has been increased, false otherwise
     */
    public boolean getCoin() {
        return coin;
    }

    /**
     * Checks if a certain player has enough coins to play the character
     * @param player Player
     * @throws IllegalMoveException When the given player doesn't have enough coins to play the character
     */
    public void checkCost(Player player) throws IllegalMoveException {
        if (player.getCoins() < cost) {
            throw new IllegalMoveException("You don't have enough coins to activate this character ability");
        }
    }

    /**
     * Gets a character from his id
     * @param characterId Id of a character
     * @return The character with the given id
     * @throws IllegalMoveException When the given id is smaller than 0 or bigger than 11
     */
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
            default -> throw new IllegalMoveException("Wrong character id");
        }
        return characterClass;
    }
}
