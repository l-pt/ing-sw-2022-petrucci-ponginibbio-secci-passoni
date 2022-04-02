package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class Character10Test extends TestCase {
    @Test
    public void useTest() throws IllegalMoveException {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(1, "test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(0, List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player2), TowerColor.BLACK);
        Match match = new Match(0, List.of(team1, team2), List.of(player1, player2), true);
        Character10 character = new Character10();

        Exception e = assertThrows(IllegalMoveException.class, () -> character.use(match, player1.getId(), new HashMap<>(), new HashMap<>()));
        assertEquals(e.getMessage(), "Invalid student number");

        e = assertThrows(IllegalMoveException.class, () -> {
            character.use(match, player1.getId(), Map.of(PawnColor.RED, 1), Map.of(PawnColor.RED, 1, PawnColor.GREEN, 2));
        });
        assertEquals(e.getMessage(), "Different map sizes");

        player1.getSchool().addStudentsToTable(List.of(new Student(PawnColor.RED)));
        PawnColor entranceColor = null;
        for (PawnColor c : PawnColor.values()) {
            if (player1.getSchool().getEntranceCount(c) > 0) {
                entranceColor = c;
                break;
            }
        }
        Map<PawnColor, Integer> entranceMap = Map.of(entranceColor, 1);
        Map<PawnColor, Integer> entranceCount = new HashMap<>();
        for (PawnColor c : PawnColor.values()) {
            entranceCount.put(c, player1.getSchool().getEntranceCount(c));
        }
        character.use(match, player1.getId(), entranceMap, Map.of(PawnColor.RED, 1));
        for (PawnColor c : PawnColor.values()) {
            if (c == entranceColor) {
                assertEquals((int) entranceCount.get(c), player1.getSchool().getEntranceCount(entranceColor) + 1 - (c == PawnColor.RED ? 1 : 0));
            } else if (c == PawnColor.RED) {
                assertEquals(1, player1.getSchool().getTableCount(c) + 1 - entranceMap.getOrDefault(c, 0));
            } else {
                assertEquals((int) entranceCount.get(c), player1.getSchool().getEntranceCount(c));
                assertEquals(0, player1.getSchool().getTableCount(c));
            }
        }
    }
}
