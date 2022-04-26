package it.polimi.ingsw.character.impl;

import it.polimi.ingsw.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.util.List;

public class Character2Test extends TestCase {
    @Test
    public void useTest() throws IllegalMoveException {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(1, "test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(0, List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player2), TowerColor.BLACK);
        Match match = new Match(0, List.of(team1, team2), List.of(player1, player2), true);
        player2.addCoin();
        Character2 character = new Character2();
        player1.getSchool().addStudentsToTable(List.of(new Student(PawnColor.RED), new Student(PawnColor.RED)));
        player2.getSchool().addStudentsToTable(List.of(new Student(PawnColor.RED)));
        player1.getSchool().addProfessor(new Professor(PawnColor.RED));
        character.use(match, player2.getId());
        player2.getSchool().addStudentsToEntrance(List.of(new Student(PawnColor.RED)));
        match.addStudent(PawnColor.RED, player2.getId());
        assertEquals(match.whoHaveProfessor(PawnColor.RED), player2);
    }
}
