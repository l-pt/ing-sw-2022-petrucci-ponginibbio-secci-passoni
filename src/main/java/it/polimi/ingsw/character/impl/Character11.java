package it.polimi.ingsw.character.impl;

import it.polimi.ingsw.IllegalMoveException;
import it.polimi.ingsw.Match;
import it.polimi.ingsw.PawnColor;
import it.polimi.ingsw.Player;
import it.polimi.ingsw.character.StudentCharacter;

public class Character11 extends StudentCharacter {
    public Character11() {
        super(10, 2, "Take 1 student from this card and place it in your dining room. " +
                "Then, draw a new student from the bag and place it on this card.");
    }

    public void use(Match match, int playerId, PawnColor color) throws IllegalMoveException {
        Player player = match.getPlayerFromId(playerId);
        checkCost(player);
        if (getStudentsColorCount(color) == 0) {
            throw new IllegalMoveException("There are no students with color " + color.name() + " on this character");
        }
        player.getSchool().addStudentsToTable(removeStudentsByColor(color, 1));
        match.checkNumberStudents(color, player);
        match.checkProfessors(color, playerId);
        if(!match.getStudentBag().isEmpty())
            addStudents(match.extractStudent(1));
        player.removeCoins(cost);
        incrementCost();
    }

    @Override
    public int getInitialStudentsNumber() {
        return 4;
    }
}
