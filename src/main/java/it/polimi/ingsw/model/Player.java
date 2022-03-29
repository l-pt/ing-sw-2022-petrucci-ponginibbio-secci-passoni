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
    private int additionalMoves;

    public Player(int id, String name, TowerColor towerColor, List<Assistant> assistants) {
        this.id = id;
        this.name = name;
        this.school = new School();
        this.towerColor = towerColor;
        this.assistants = assistants;
        this.currentAssistant = null;
        this.discardPile = null;
        this.coins = 1;
        this.additionalInfluence = 0;
        this.additionalMoves = 0;
    }

    public int getId() {
        return id;
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

    public void removeCoins(int coins) {
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
}
