package it.polimi.ingsw.model;

import java.util.List;

public class Player {
    private int coins;
    private List<Assistant> assistants;
    private School school;
    private Assistant currentAssistant;
    private Assistant discardPile;
    private int id;
    private String name;

    public Player(List<Assistant> assistants, School school, int id, String name) {
        this.coins = 1;
        this.assistants = assistants;
        this.school = school;
        this.currentAssistant = null;
        this.discardPile = null;
        this.id = id;
        this.name = name;
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

    public Assistant getCurrentAssistant() {
        return currentAssistant;
    }

    public School getSchool() {
        return school;
    }

    public void setCurrentAssistant(Assistant assistant) {
        discardPile = currentAssistant;
        currentAssistant = assistant;
    }

    public Assistant getDiscardPile() {
        return discardPile;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
