package it.polimi.ingsw.model;

import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ThreePlayersMatchTest extends TestCase {
    @Test
    public void threePlayersMatchTest() {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.BLACK, Wizard.GREEN);
        Player player3 = new Player("test3", TowerColor.GRAY, Wizard.PURPLE);
        Team team1 = new Team(List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(List.of(player2), TowerColor.BLACK);
        Team team3 = new Team(List.of(player3), TowerColor.GRAY);
        ThreePlayersMatch match = new ThreePlayersMatch(List.of(team1, team2, team3), List.of(player1, player2, player3), true);

        Assertions.assertEquals(6, team1.getTowers().size());
        Assertions.assertEquals(6, team2.getTowers().size());
        Assertions.assertEquals(6, team3.getTowers().size());

        Assertions.assertEquals(3, match.getClouds().size());
        for (Cloud cloud : match.getClouds()) {
            Assertions.assertEquals(4, cloud.getStudents().size());
        }
    }
}
