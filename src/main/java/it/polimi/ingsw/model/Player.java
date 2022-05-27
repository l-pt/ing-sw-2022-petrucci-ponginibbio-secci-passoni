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

    public String getName() {
        return name;
    }

    public School getSchool() {
        return school;
    }

    public TowerColor getTowerColor() {
        return towerColor;
    }

    public List<Assistant> getAssistants() {
        return assistants;
    }

    public Assistant getCurrentAssistant() {
        return currentAssistant;
    }

    public Assistant getDiscardPile() {
        return discardPile;
    }

    public Assistant getAssistantFromValue(int value){
        for (Assistant assistant : assistants)
            if (assistant.getValue() == value)
                return assistant;
        return null;
    }

    public void setCurrentAssistant(Assistant assistant) {
        discardPile = currentAssistant;
        currentAssistant = assistant;
        assistants.remove(assistant);
    }

    public int getCoins() {
        return coins;
    }

    public void addCoin() {
        ++coins;
    }

    public void removeCoins(int coins) throws IllegalArgumentException {
        if (coins > this.coins) {
            throw new IllegalArgumentException("Player " + name + "does not have enough coins");
        }
        this.coins -= coins;
    }

    public int getAdditionalInfluence() {
        return additionalInfluence;
    }

    public void setAdditionalInfluence(int additionalInfluence) {
        this.additionalInfluence = additionalInfluence;
    }

    public int getAdditionalMoves() {
        return additionalMoves;
    }

    public void setAdditionalMoves(int additionalMoves) {
        this.additionalMoves = additionalMoves;
    }

    public Wizard getWizard() {
        return wizard;
    }
}
