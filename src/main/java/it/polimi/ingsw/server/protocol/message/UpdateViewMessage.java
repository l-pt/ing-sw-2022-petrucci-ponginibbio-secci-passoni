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
    private List<Team> teams;
    private List<Island> islands;
    private List<Player> playersOrder;
    private int posMotherNature;
    private List<Cloud> clouds;
    private List<Professor> professors;
    private int coinReserve;
    private List<Character> characters;
    private boolean expert;
    private String currentPlayer;

    /**
     * Constructor for UpdateViewMessage which carries all the information of the current state of a match from server to client.
     * @param teams
     * @param islands
     * @param playersOrder
     * @param posMotherNature
     * @param clouds
     * @param professors
     * @param coinReserve
     * @param characters
     * @param expert
     * @param currentPlayer
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

    public List<Team> getTeams() {return teams;}

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
