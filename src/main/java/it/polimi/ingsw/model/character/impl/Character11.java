package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.IllegalMoveException;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.PawnColor;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.character.ColorCharacter;
import it.polimi.ingsw.model.character.StudentCharacter;

public class Character11 extends StudentCharacter implements ColorCharacter {
    public Character11() {
        super(10, 2, "Take 1 student from this card and place it in your dining room. " +
                "Then, draw a new student from the bag and place it on this card.");
    }

    /**
     * Uses the ability: "Take 1 student from this card and place it in your dining room.
     * Then, draw a new student from the bag and place it on this card."
     * @param match Match
     * @param playerName The username of a player
     * @param color PawnColor of a student
     * @throws IllegalMoveException When there aren't any players with the given name.
     * When the given player doesn't have enough coins to play the character.
     * When there aren't any student of the given color on the character
     */
    public void use(Match match, String playerName, PawnColor color) throws IllegalMoveException {
        Player player = match.getPlayerFromName(playerName);

        //Checks the coins of the player
        checkCost(player);

        //Checks the number of students on the character
        if (getStudentsColorCount(color) == 0) {
            throw new IllegalMoveException("There are no students with color " + color.name() + " on this character");
        }

        //Adds a student from the character to the player dining room
        player.getSchool().addStudentsToTable(removeStudentsByColor(color, 1));
        match.checkNumberStudents(playerName, color);
        match.checkProfessors(playerName, color);
        addStudents(match.extractStudents(1));

        player.removeCoins(cost);
        incrementCost();

        //Updates the state of game for the view
        match.updateView();
    }

    /**
     * Gets the initial student number on the character (4)
     * @return The initial student number on the character (4)
     */
    @Override
    public int getInitialStudentsNumber() {
        return 4;
    }
}
