package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class Character2Test extends TestCase {
    @Test
    public void useTest() throws IllegalMoveException {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(List.of(player2), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2), true);
        player2.addCoin();
        Character2 character = new Character2();
        player1.getSchool().addStudentsToTable(List.of(new Student(PawnColor.RED), new Student(PawnColor.RED)));
        player2.getSchool().addStudentsToTable(List.of(new Student(PawnColor.RED)));
        player1.getSchool().addProfessor(new Professor(PawnColor.RED));
        character.use(match, player2.getName());
        player2.getSchool().addStudentsToEntrance(List.of(new Student(PawnColor.RED)));
        match.playerMoveStudent(PawnColor.RED, player2.getName());
        Assertions.assertEquals(match.whoHaveProfessor(PawnColor.RED), player2);
    }
}
