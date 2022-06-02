package it.polimi.ingsw.client;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.protocol.message.UpdateViewMessage;

import java.util.Collections;
import java.util.List;

public abstract class View<T extends Client> {
    protected T client;
    protected List<Team> teams;
    protected List<Assistant> assistants;
    protected List<Island> islands;
    protected List<Player> playersOrder;
    protected List<String> originalPlayersOrder;
    protected int posMotherNature;
    protected List<Cloud> clouds;
    protected List<Professor> professors;
    protected int coinReserve;
    protected List<Character> characters;
    protected boolean expert;
    protected String currentPlayer;

    public void handleUpdateView(UpdateViewMessage message) {
        teams = message.getTeams();
        islands = message.getIslands();
        playersOrder = message.getPlayersOrder();
        if (originalPlayersOrder == null) {
            originalPlayersOrder = playersOrder.stream().map(Player::getName).toList();
        }
        assistants = getPlayerFromName(client.getName()).getAssistants();
        posMotherNature = message.getPosMotherNature();
        clouds = message.getClouds();
        professors = message.getProfessors();
        coinReserve = message.getCoinReserve();
        characters = message.getCharacters();
        expert = message.isExpert();
        currentPlayer = message.getCurrentPlayer();

        print();
    }

    public abstract void print();

    public Player getPlayerFromName(String name){
        for (Player player : playersOrder) {
            if (player.getName().equals(name))
                return player;
        }
        throw new IllegalArgumentException();
    }

    public T getClient() {
        return client;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public List<Assistant> getAssistants() {
        return assistants;
    }

    public List<Island> getIslands() {
        return islands;
    }

    public List<Player> getPlayersOrder() {
        return playersOrder;
    }

    public int getPosMotherNature() {
        return posMotherNature;
    }

    public List<Cloud> getClouds() {
        return clouds;
    }

    public List<Professor> getProfessors() {
        return professors;
    }

    public int getCoinReserve() {
        return coinReserve;
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public boolean isExpert() {
        return expert;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }
}
