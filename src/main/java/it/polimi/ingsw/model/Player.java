package it.polimi.ingsw.model;

import java.util.List;

public class Player {
    private int id;
    private String name;
    private School school;
    private TowerColor towerColor;
    private List<Assistant> assistants;
    private Assistant currentAssistant;
    private Assistant discardPile;
    private int coins;
    private int additionalInfluence;

    public Player(int id, String name, School school, TowerColor towerColor, List<Assistant> assistants) {
        this.id = id;
        this.name = name;
        this.school = school;
        this.towerColor = towerColor;
        this.assistants = assistants;
        this.currentAssistant = null;
        this.discardPile = null;
        this.coins = 1;
        this.additionalInfluence = 0;
    }

    public int getId() {
        return id;
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

    public Assistant getCurrentAssistant() {
        return currentAssistant;
    }

    public void setCurrentAssistant(Assistant assistant) {
        discardPile = currentAssistant;
        currentAssistant = assistant;
    }

    public Assistant getDiscardPile() {
        return discardPile;
    }

    public int getCoins() {
        return coins;
    }

    public void addCoin() {
        ++coins;
    }

    public void removeCoins(int coins) {
        this.coins -= coins;
    }

    public int getAdditionalInfluence() {
        return additionalInfluence;
    }

    public void setAdditionalInfluence(int additionalInfluence) {
        this.additionalInfluence = additionalInfluence;
    }
}
