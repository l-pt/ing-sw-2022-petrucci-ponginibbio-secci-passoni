package it.polimi.ingsw.character.impl;

import it.polimi.ingsw.*;
import it.polimi.ingsw.character.StudentCharacter;

import java.util.List;

public class Character1 extends StudentCharacter {
    public Character1() {
        super(0, 1, "Take 1 student from this card and place it on an island of your choice. " +
                "Then, draw a new student from the bag and place it on this card.");
    }

    public void use(Match match, int playerId, PawnColor color, int island) throws IllegalMoveException {
        if (island < 0 || island >= match.getIslands().size()) {
            throw new IllegalMoveException("Island must be between 0 and " + (match.getIslands().size() - 1));
        }
        Player player = match.getPlayerFromId(playerId);
        checkCost(player);
        if (getStudentsColorCount(color) == 0) {
            throw new IllegalMoveException("There are no students with color " + color.name() + " on this character");
        }
        List<Student> students = removeStudentsByColor(color, 1);
        match.getIslands().get(island).addStudents(students);
        if (!match.getStudentBag().isEmpty())
            addStudents(match.extractStudent(1));
        player.removeCoins(cost);
        incrementCost();
    }

    @Override
    public int getInitialStudentsNumber() {
        return 4;
    }
}
