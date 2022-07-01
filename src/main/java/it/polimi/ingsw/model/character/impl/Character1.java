package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.character.StudentCharacter;
import it.polimi.ingsw.model.*;

import java.util.List;

public class Character1 extends StudentCharacter {
    public Character1() {
        super(0, 1, "Take 1 student from this card and place it on an island of your choice. " +
                "Then, draw a new student from the bag and place it on this card.");
    }

    /**
     * Uses the ability: "Take 1 student from this card and place it on an island of your choice.
     * Then, draw a new student from the bag and place it on this card."
     * @param match Match
     * @param playerName The username of a player
     * @param color PawnColor of a student
     * @param island The island number
     * @throws IllegalMoveException When the island number is smaller than 0 or bigger than the islands size.
     * When there aren't any players with the given name.
     * When the given player doesn't have enough coins to play the character.
     * When there aren't any student of the given color on the character
     */
    public void use(Match match, String playerName, PawnColor color, int island) throws IllegalMoveException {
        //Checks the chosen island number
        if (island < 0 || island >= match.getIslands().size()) {
            throw new IllegalMoveException("Island number must be between 1 and " + match.getIslands().size());
        }

        Player player = match.getPlayerFromName(playerName);

        //Checks the coins of the player
        checkCost(player);

        //Checks the number of students of the chosen color on the character
        if (getStudentsColorCount(color) == 0) {
            throw new IllegalMoveException("There are no students with color " + color.name() + " on this character");
        }

        //Adds the student on the chosen island and removes it from the character
        List<Student> students = removeStudentsByColor(color, 1);
        match.getIslands().get(island).addStudents(students);
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
