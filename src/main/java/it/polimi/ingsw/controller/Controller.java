package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.server.Connection;
import it.polimi.ingsw.server.Server;

import java.util.List;

public class Controller {
    private Match match;
    private Server server;

    public Controller(Server server, List<Team> teams, List<Player> players, boolean expert) {
        this.match = new Match(teams, players, expert);
        this.server = server;
    }

    public Match getMatch() {
        return match;
    }

    public void handleClientMessage(Connection connection, Message message) {
        //TODO
    }

    /**
     * Move a student from entrance to table
     */
    public void moveStudentToTable(String playerName, PawnColor color) throws IllegalMoveException {
        Player player = match.getPlayerFromName(playerName);
        player.getSchool().addStudentToTable(color);
    }

    /**
     * Move a student from entrance to an island
     */
    public void moveStudentToIsland(String playerName, PawnColor color, int island) throws IllegalMoveException {
        if (island < 0 || island >= match.getIslands().size()) {
            throw new IllegalMoveException("Island " + island + " does not exist");
        }
        Player player = match.getPlayerFromName(playerName);
        if (player.getSchool().getEntranceCount(color) == 0) {
            throw new IllegalMoveException("There are no students with color " + color.name() + " in the entrance");
        }
        List<Student> students = player.getSchool().removeEntranceStudentsByColor(color, 1);
        match.getIslands().get(island).addStudents(students);
    }

    /**
     * Move all the students on a cloud to the player's entrance
     */
    public void moveStudentsFromCloud(int cloudIndex, String playerName) throws IllegalMoveException {
        match.moveStudentsFromCloud(cloudIndex, playerName);
    }

    /**
     * Move mother nature
     */
    public void moveMotherNature(int moves, String playerName) throws IllegalMoveException {
        match.moveMotherNature(moves, playerName);
    }

    /**
     * Use the assistant with the given value
     */
    public void useAssistant(String playerName, int value) throws IllegalMoveException {
        match.useAssistant(playerName, value);
    }

    /**
     * Get the character object with given index
     */
    public Character getCharacter(int characterIndex) throws IllegalMoveException {
        return match.getCharacter(characterIndex);
    }
}
