package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.character.Character;

import java.util.List;

public class Controller {
    private Match match;

    public Controller(Match match) {
        this.match = match;
    }

    /**
     * Move a student from entrance to table
     */
    public void moveStudentToTable(int playerId, PawnColor color) throws IllegalMoveException {
        Player player = match.getPlayerFromId(playerId);
        player.getSchool().addStudentToTable(color);
    }

    /**
     * Move a student from entrance to an island
     */
    public void moveStudentToIsland(int playerId, PawnColor color, int island) throws IllegalMoveException {
        if (island < 0 || island >= match.getIslands().size()) {
            throw new IllegalMoveException("Island " + island + " does not exist");
        }
        Player player = match.getPlayerFromId(playerId);
        if (player.getSchool().getEntranceCount(color) == 0) {
            throw new IllegalMoveException("There are no students with color " + color.name() + " in the entrance");
        }
        List<Student> students = player.getSchool().removeEntranceStudentsByColor(color, 1);
        match.getIslands().get(island).addStudents(students);
    }

    /**
     * Move all the students on a cloud to the player's entrance
     */
    public void moveStudentsFromCloud(int cloudIndex, int playerId) throws IllegalMoveException {
        match.moveStudentsFromCloud(cloudIndex, playerId);
    }

    /**
     * Move mother nature
     */
    public void moveMotherNature(int moves, int playerId) throws IllegalMoveException {
        match.moveMotherNature(moves, playerId);
    }

    /**
     * Use the assistant with the given value
     */
    public void useAssistant(int playerId, int value) throws IllegalMoveException {
        match.useAssistant(playerId, value);
    }

    /**
     * Get the character object with given index
     */
    public Character getCharacter(int characterIndex) throws IllegalMoveException {
        return match.getCharacter(characterIndex);
    }
}
