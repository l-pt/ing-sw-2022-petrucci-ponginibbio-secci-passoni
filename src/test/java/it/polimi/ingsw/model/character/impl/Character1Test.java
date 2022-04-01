package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

public class Character1Test extends TestCase {
    @Test
    public void useTest() throws IllegalMoveException {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(0, "test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(0, List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player2), TowerColor.BLACK);
        Match match = new Match(0, List.of(team1, team2), List.of(player1, player2), true);
        player1.addCoin();
        player1.addCoin();
        Character1 character = new Character1();
        character.setup(match);
        PawnColor color = character.getStudents().get(0).getColor();
        PawnColor finalColor1 = color;
        int initialStudentCount = (int) match.getIslands().get(0).getStudents().stream().filter(s -> s.getColor() == finalColor1).count();
        int bagCount = match.getStudentBag().size();
        character.use(match, player1.getId(), color, 0);

        assertEquals(initialStudentCount + 1, (int) match.getIslands().get(0).getStudents().stream().filter(s -> s.getColor() == finalColor1).count());
        assertEquals(match.getStudentBag().size(), bagCount - 1);

        for (PawnColor c : PawnColor.values()) {
            if (character.getStudentsColorCount(c) == 0) {
                color = c;
                break;
            }
        }
        PawnColor finalColor = color;
        Exception e = assertThrows(IllegalMoveException.class, () -> character.use(match, player1.getId(), finalColor, 0));
        assertEquals(e.getMessage(), "There are no students with color " + color.name() + " on this character");
    }

    @Test
    public void coinTest() {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(0, "test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(0, List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player2), TowerColor.BLACK);
        Match match = new Match(0, List.of(team1, team2), List.of(player1, player2), true);
        player1.removeCoins(player1.getCoins());
        Character1 character = new Character1();
        character.setup(match);
        PawnColor color = character.getStudents().get(0).getColor();
        Exception e = assertThrows(IllegalMoveException.class, () -> character.use(match, player1.getId(), color, 0));
        assertEquals(e.getMessage(), "Insufficient coins");
    }
}
