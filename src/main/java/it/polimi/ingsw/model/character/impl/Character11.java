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

    public void use(Match match, String playerName, PawnColor color) throws IllegalMoveException {
        Player player = match.getPlayerFromName(playerName);
        checkCost(player);
        if (getStudentsColorCount(color) == 0) {
            throw new IllegalMoveException("There are no students with color " + color.name() + " on this character");
        }
        player.getSchool().addStudentsToTable(removeStudentsByColor(color, 1));
        match.checkNumberStudents(player.getName(), color);
        match.checkProfessors(playerName, color);
        addStudents(match.extractStudents(1));
        player.removeCoins(cost);
        incrementCost();
        match.updateView();
    }

    @Override
    public int getInitialStudentsNumber() {
        return 4;
    }
}
