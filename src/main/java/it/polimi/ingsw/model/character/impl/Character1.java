package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.character.StudentCharacter;
import it.polimi.ingsw.model.*;

import java.util.List;

public class Character1 extends StudentCharacter {
    public Character1() {
        super(0, 1, "Take 1 student from this card and place it on an island of your choice. " +
                "Then, draw a new student from the bag and place it on this card.");
    }

    public void use(Match match, String playerName, PawnColor color, int island) throws IllegalMoveException {
        if (island < 0 || island >= match.getIslands().size()) {
            throw new IllegalMoveException("Island number must be between 1 and " + match.getIslands().size());
        }
        Player player = match.getPlayerFromName(playerName);
        checkCost(player);
        if (getStudentsColorCount(color) == 0) {
            throw new IllegalMoveException("There are no students with color " + color.name() + " on this character");
        }
        List<Student> students = removeStudentsByColor(color, 1);
        match.getIslands().get(island).addStudents(students);
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
