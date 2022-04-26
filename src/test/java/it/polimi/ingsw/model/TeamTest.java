package it.polimi.ingsw.model;

import it.polimi.ingsw.*;
import junit.framework.TestCase;
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

        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(1, "test2", TowerColor.WHITE, Wizard.GREEN);
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        Team team = new Team(0, players, TowerColor.WHITE);
        assertEquals(team.getTowers().size(), 0);
        Tower tower = new Tower(TowerColor.WHITE);
        team.addTower(tower);
        assertEquals(team.getTowers().size(), 1);
        assertEquals(team.getTowers().get(0), tower);
        team.addTowers(towers);
        assertEquals(team.getTowers().size(), 3);

        List<Tower> removedTowers = team.removeTowers(1);
        towers.removeAll(removedTowers);
        assertEquals(team.getTowers(), towers);

        Exception e = assertThrows(IllegalArgumentException.class, () -> team.removeTowers(3));
        assertEquals(e.getMessage(), "Team 0 does not have enough towers");
    }
}
