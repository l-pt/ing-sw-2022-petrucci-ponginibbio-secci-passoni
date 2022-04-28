package it.polimi.ingsw.model;

import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ThreePlayersMatchTest extends TestCase {
    @Test
    public void threePlayersMatchTest() {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(1, "test2", TowerColor.BLACK, Wizard.GREEN);
        Player player3 = new Player(2, "test3", TowerColor.GRAY, Wizard.PURPLE);
        Team team1 = new Team(0, List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player2), TowerColor.BLACK);
        Team team3 = new Team(2, List.of(player3), TowerColor.GRAY);
        ThreePlayersMatch match = new ThreePlayersMatch(0, List.of(team1, team2, team3), List.of(player1, player2, player3), true);

        assertEquals(6, team1.getTowers().size());
        assertEquals(6, team2.getTowers().size());
        assertEquals(6, team3.getTowers().size());

        assertEquals(3, match.getClouds().size());
        for (Cloud cloud : match.getClouds()) {
            assertEquals(4, cloud.getStudents().size());
        }
    }
}
