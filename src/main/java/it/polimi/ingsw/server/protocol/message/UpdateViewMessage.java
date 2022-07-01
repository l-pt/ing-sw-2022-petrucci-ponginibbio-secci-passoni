package it.polimi.ingsw.server.protocol.message;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

import java.util.List;

/**
 * UpdateViewMessage Class
 */
public class UpdateViewMessage extends Message {
    private final List<Team> teams;
    private final List<Island> islands;
    private final List<Player> playersOrder;
    private final int posMotherNature;
    private final List<Cloud> clouds;
    private final List<Professor> professors;
    private final int coinReserve;
    private final List<Character> characters;
    private final boolean expert;
    private final String currentPlayer;

    /**
     * Constructor for UpdateViewMessage which carries all the information of the current state of a match from server to client.
     * @param teams The list of teams
     * @param islands The list of islands
     * @param playersOrder The list of players in order of play
     * @param posMotherNature The index of the island where mother nature is
     * @param clouds The list of clouds
     * @param professors The list of professors
     * @param coinReserve The number of coins in the player coin reserve
     * @param characters The list of characters
     * @param expert The boolean expert mode
     * @param currentPlayer The username of the player who is playing
     */
    public UpdateViewMessage(
        List<Team> teams,
        List<Island> islands,
        List<Player> playersOrder,
        int posMotherNature,
        List<Cloud> clouds,
        List<Professor> professors,
        int coinReserve,
        List<Character> characters,
        boolean expert,
        String currentPlayer
    ){
        super(MessageId.UPDATE_VIEW);
        this.teams = teams;
        this.islands = islands;
        this.playersOrder = playersOrder;
        this.posMotherNature = posMotherNature;
        this.clouds = clouds;
        this.professors = professors;
        this.coinReserve = coinReserve;
        this.characters = characters;
        this.expert = expert;
        this.currentPlayer = currentPlayer;
    }

    /**
     * getTeams()
     * @return The list of teams
     */
    public List<Team> getTeams() {return teams;}

    /**
     * getIslands()
     * @return The list of islands
     */
    public List<Island> getIslands() {
        return islands;
    }

    /**
     * getPlayersOrder()
     * @return The list of players in order of play
     */
    public List<Player> getPlayersOrder() {
        return playersOrder;
    }

    /**
     * getPosMotherNature()
     * @return The index of the island where mother nature is
     */
    public int getPosMotherNature() {
        return posMotherNature;
    }

    /**
     * getClouds()
     * @return The list of clouds
     */
    public List<Cloud> getClouds() {
        return clouds;
    }

    /**
     * getProfessors()
     * @return The list of professors in the player school
     */
    public List<Professor> getProfessors() {
        return professors;
    }

    /**
     * getCoinReserve()
     * @return The number of coins owned by the player
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
     * @return The boolean expert mode
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
