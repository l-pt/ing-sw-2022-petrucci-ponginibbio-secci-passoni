package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private School school;
    private TowerColor towerColor;
    private List<Assistant> assistants;
    private Assistant currentAssistant;
    private Assistant discardPile;
    private int coins;
    private int additionalInfluence;
    private int additionalMoves;
    private Wizard wizard;

    public Player(String name, TowerColor towerColor, Wizard wizard) {
        this.name = name;
        this.school = new School();
        this.towerColor = towerColor;
        assistants = new ArrayList<>(10);
        assistants.add(new Assistant(1, 1));
        assistants.add(new Assistant(2, 1));
        assistants.add(new Assistant(3, 2));
        assistants.add(new Assistant(4, 2));
        assistants.add(new Assistant(5, 3));
        assistants.add(new Assistant(6, 3));
        assistants.add(new Assistant(7, 4));
        assistants.add(new Assistant(8, 4));
        assistants.add(new Assistant(9, 5));
        assistants.add(new Assistant(10, 5));
        this.currentAssistant = null;
        this.discardPile = null;
        this.coins = 0;
        this.additionalInfluence = 0;
        this.additionalMoves = 0;
        this.wizard = wizard;
    }

    /**
     * getName()
     * @return The username of the player
     */
    public String getName() {
        return name;
    }

    /**
     * getSchool()
     * @return The player's school
     */
    public School getSchool() {
        return school;
    }

    /**
     * getTowerColor()
     * @return The color of the player's towers
     */
    public TowerColor getTowerColor() {
        return towerColor;
    }

    /**
     * getAssistants()
     * @return The list of assistants
     */
    public List<Assistant> getAssistants() {
        return assistants;
    }

    /**
     * getCurrentAssistant()
     * @return The assistant played in this round
     */
    public Assistant getCurrentAssistant() {
        return currentAssistant;
    }

    /**
     * getDiscardPile()
     * @return The assistant played in the previous round
     */
    public Assistant getDiscardPile() {
        return discardPile;
    }

    /**
     * Searches the assistant with this value
     * @param value The value of the assistant to search
     * @return The assistant with this value
     */
    public Assistant getAssistantFromValue(int value){
        for (Assistant assistant : assistants)
            if (assistant.getValue() == value)
                return assistant;
        return null;
    }

    /**
     * Sets the assistant played in this round as current assistant
     * @param assistant The assistant played in this round
     */
    public void setCurrentAssistant(Assistant assistant) {
        discardPile = currentAssistant;
        currentAssistant = assistant;
        assistants.remove(assistant);
    }

    /**
     * getCoins()
     * @return The number of coins owned by the player
     */
    public int getCoins() {
        return coins;
    }

    /**
     * Adds a coin
     */
    public void addCoin() {
        ++coins;
    }

    /**
     * Removes a certain number of coins
     * @param coins The number of coins to remove from the player coin reserve
     * @throws IllegalArgumentException If the player does not have the number of coins indicated
     */
    public void removeCoins(int coins) throws IllegalArgumentException {
        if (coins > this.coins) {
            throw new IllegalArgumentException("Player " + name + "does not have enough coins");
        }
        this.coins -= coins;
    }

    /**
     * getAdditionalInfluence()
     * @return The additional influence value added
     */
    public int getAdditionalInfluence() {
        return additionalInfluence;
    }

    /**
     * setAdditionalInfluence()
     * @param additionalInfluence The additional influence value to add
     */
    public void setAdditionalInfluence(int additionalInfluence) {
        this.additionalInfluence = additionalInfluence;
    }

    /**
     * getAdditionalMoves()
     * @return The number of additional moves added
     */
    public int getAdditionalMoves() {
        return additionalMoves;
    }

    /**
     * setAdditionalMoves()
     * @param additionalMoves The number of additional moves to add
     */
    public void setAdditionalMoves(int additionalMoves) {
        this.additionalMoves = additionalMoves;
    }

    /**
     * getWizard()
     * @return The wizard related to the player
     */
    public Wizard getWizard() {
        return wizard;
    }
}
