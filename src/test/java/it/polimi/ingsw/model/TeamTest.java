package it.polimi.ingsw.model;

import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

public class TeamTest extends TestCase {
    @Test
    public void towersTest() {
        List<Tower> towers = new ArrayList<>();
        towers.add(new Tower(TowerColor.WHITE));
        towers.add(new Tower(TowerColor.WHITE));

        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player("test3", TowerColor.GRAY, Wizard.PINK);
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        Team team = new Team(players, TowerColor.WHITE);
        Assertions.assertTrue(team.isTeamMember(player1));
        Assertions.assertFalse(team.isTeamMember(player3));
        Assertions.assertEquals(team.getTowers().size(), 0);
        Tower tower = new Tower(TowerColor.WHITE);
        team.addTower(tower);
        Assertions.assertEquals(team.getTowers().size(), 1);
        Assertions.assertEquals(team.getTowers().get(0), tower);
        team.addTowers(towers);
        Assertions.assertEquals(team.getTowers().size(), 3);

        List<Tower> removedTowers = team.removeTowers(1);
        towers.removeAll(removedTowers);
        Assertions.assertEquals(team.getTowers(), towers);

        Exception e = assertThrows(IllegalArgumentException.class, () -> team.removeTowers(3));
        Assertions.assertEquals(e.getMessage(), "Team does not have enough towers");
    }
}
