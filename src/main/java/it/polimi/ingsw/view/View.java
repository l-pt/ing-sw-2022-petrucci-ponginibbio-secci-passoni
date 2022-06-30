package it.polimi.ingsw.view;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.server.protocol.message.UpdateViewMessage;

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

    /**
     * Updates the view attributes and print on screen
     * @param message Contains the updated model elements
     */
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

    /**
     * Prints on screen
     */
    public abstract void print();

    /**
     * getPlayerFromName()
     * @param name The username of the player
     * @return The player with this username
     */
    public Player getPlayerFromName(String name){
        for (Player player : playersOrder) {
            if (player.getName().equals(name))
                return player;
        }
        throw new IllegalArgumentException();
    }

    /**
     * getClient()
     * @return The client linked to the view
     */
    public T getClient() {
        return client;
    }

    /**
     * getTeams()
     * @return The list of the game teams
     */
    public List<Team> getTeams() {
        return teams;
    }

    /**
     * getAssistants()
     * @return The list of the assistants
     */
    public List<Assistant> getAssistants() {
        return assistants;
    }

    /**
     * getIslands()
     * @return The list of the islands
     */
    public List<Island> getIslands() {
        return islands;
    }

    /**
     * getPlayersOrder()
     * @return The list of the players in order of play
     */
    public List<Player> getPlayersOrder() {
        return playersOrder;
    }

    /**
     * getPosMotherNature()
     * @return The position of mother nature
     */
    public int getPosMotherNature() {
        return posMotherNature;
    }

    /**
     * getClouds()
     * @return The list of the clouds
     */
    public List<Cloud> getClouds() {
        return clouds;
    }

    /**
     * getProfessors()
     * @return The list of the professors
     */
    public List<Professor> getProfessors() {
        return professors;
    }

    /**
     * getCoinReserve()
     * @return The number of coins in the coin reserve
     */
    public int getCoinReserve() {
        return coinReserve;
    }

    /**
     * getCharacters()
     * @return The list of characters
     */
    public List<Character> getCharacters() {
        return characters;
    }

    /**
     * isExpert()
     * @return True if it is an expert mode game
     */
    public boolean isExpert() {
        return expert;
    }

    /**
     * getCurrentPlayer()
     * @return The username of the player who is playing
     */
    public String getCurrentPlayer() {
        return currentPlayer;
    }
}
