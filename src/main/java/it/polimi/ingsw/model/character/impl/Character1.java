package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.character.StudentCharacter;

import java.util.List;

public class Character1 extends StudentCharacter {
    public Character1() {
        super(0, 1, "Take 1 student from this card and place it on an island of your choice. " +
                "Then, draw a new student from the bag and place it on this card.");
    }

    public void use(Match match, int playerId, PawnColor color, int island) throws IllegalMoveException {
        Player player = match.getPlayerFromId(playerId);
        checkCost(player);
        if (getStudentsColorCount(color) == 0) {
            throw new IllegalMoveException("There are no students with color " + color.name() + " on this character");
        }
        List<Student> students = removeStudentsByColor(color, 1);
        match.getIslands().get(island).addStudents(students);
        addStudents(match.extractStudent(1));
        player.removeCoins(cost);
        incrementCost();
    }

    @Override
    public int getInitialStudentsNumber() {
        return 4;
    }
}
