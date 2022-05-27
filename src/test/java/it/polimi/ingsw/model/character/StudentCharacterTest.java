package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.character.impl.Character7;
import junit.framework.TestCase;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class StudentCharacterTest extends TestCase {
    @Test
    public void studentCharacterTest() {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(List.of(player2), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2), true);
        Character7 character = new Character7();
        character.setup(match);
        PawnColor color = null;
        for (Student student : character.getStudents()) {
            color = student.getColor();
            break;
        }
        PawnColor finalColor = color;
        Exception e = assertThrows(IllegalArgumentException.class, () -> character.removeStudentsByColor(finalColor, 10));
        Assertions.assertEquals("There are not enough students with color " + color.name() + " on this character", e.getMessage());
    }
}
