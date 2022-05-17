package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

public class Character11Test extends TestCase {
    @Test
    public void useTest() throws IllegalMoveException {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(List.of(player2), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2), true);
        Character11 character = new Character11();
        character.setup(match);
        player1.addCoin();

        //Get the color of a student on the card
        PawnColor color = character.getStudents().get(0).getColor();
        int bagCount = match.getStudentBag().size();
        int tableCount = player1.getSchool().getTableCount(color);
        character.use(match, player1.getName(), color);
        //Check that one student of the given color has been added to the table
        assertEquals(player1.getSchool().getTableCount(color), tableCount + 1);
        //Check that one student has been extracted from the bag
        assertEquals(match.getStudentBag().size(), bagCount - 1);

        List<PawnColor> absentColors = new ArrayList<>(List.of(PawnColor.values()));
        for (Student student : character.getStudents()) {
            absentColors.remove(student.getColor());
        }
        PawnColor absentColor = absentColors.get(0);
        player1.addCoin();
        player1.addCoin();
        player1.addCoin();
        Exception e = assertThrows(IllegalMoveException.class, () -> character.use(match, player1.getName(), absentColor));
        assertEquals("There are no students with color " + absentColor.name() + " on this character", e.getMessage());
    }
}
