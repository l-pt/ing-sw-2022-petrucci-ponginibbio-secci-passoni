package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

public class Character1Test extends TestCase {
    @Test
    public void useTest() throws IllegalMoveException {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(List.of(player2), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2), true);
        player1.addCoin();
        player1.addCoin();
        Character1 character = new Character1();
        character.setup(match);
        PawnColor color = character.getStudents().get(0).getColor();
        PawnColor finalColor1 = color;

        Exception e = assertThrows(IllegalMoveException.class, () -> character.use(match, player1.getName(), finalColor1, 50));
        Assertions.assertEquals(e.getMessage(), "Island number must be between 1 and " + match.getIslands().size());

        int initialStudentCount = (int) match.getIslands().get(0).getStudents().stream().filter(s -> s.getColor() == finalColor1).count();
        int bagCount = match.getStudentBag().size();
        character.use(match, player1.getName(), color, 0);

        Assertions.assertEquals(initialStudentCount + 1, (int) match.getIslands().get(0).getStudents().stream().filter(s -> s.getColor() == finalColor1).count());
        Assertions.assertEquals(match.getStudentBag().size(), bagCount - 1);

        for (PawnColor c : PawnColor.values()) {
            if (character.getStudentsColorCount(c) == 0) {
                color = c;
                break;
            }
        }
        PawnColor finalColor = color;
        e = assertThrows(IllegalMoveException.class, () -> character.use(match, player1.getName(), finalColor, 0));
        Assertions.assertEquals(e.getMessage(), "There are no students with color " + color.name() + " on this character");
    }

    @Test
    public void coinTest() {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(List.of(player2), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2), true);
        player1.removeCoins(player1.getCoins());
        Character1 character = new Character1();
        character.setup(match);
        PawnColor color = character.getStudents().get(0).getColor();
        Exception e = assertThrows(IllegalMoveException.class, () -> character.use(match, player1.getName(), color, 0));
        Assertions.assertEquals(e.getMessage(), "Insufficient coins");
    }

    @Test
    public void incrementCostTest() throws IllegalMoveException {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(List.of(player2), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2), true);
        Character1 character = new Character1();
        character.setup(match);
        PawnColor color = character.getStudents().get(0).getColor();
        int cost = character.getCost();
        character.use(match, player1.getName(), color, 0);
        Assertions.assertEquals(character.getCost(), cost + 1);
    }
}
