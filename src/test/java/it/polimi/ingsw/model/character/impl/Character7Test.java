package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Character7Test extends TestCase {
    @Test
    public void useTest() throws IllegalMoveException {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(1, "test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(0, List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player2), TowerColor.BLACK);
        Match match = new Match(0, List.of(team1, team2), List.of(player1, player2), true);
        Character7 character = new Character7();
        character.setup(match);

        Exception e = assertThrows(IllegalMoveException.class, () -> character.use(match, player1.getId(), new HashMap<>(), new HashMap<>()));
        assertEquals(e.getMessage(), "Invalid student number");

        e = assertThrows(IllegalMoveException.class, () -> {
            character.use(match, player1.getId(), Map.of(PawnColor.RED, 1), Map.of(PawnColor.RED, 1, PawnColor.GREEN, 2));
        });
        assertEquals(e.getMessage(), "Different map sizes");

        PawnColor entranceColor = null;
        for (PawnColor c : PawnColor.values()) {
            if (player1.getSchool().getEntranceCount(c) > 0) {
                entranceColor = c;
                break;
            }
        }
        
        Map<PawnColor, Integer> inMap = new HashMap<>();
        inMap.put(entranceColor, player1.getSchool().getEntranceCount(entranceColor));

        PawnColor cardColor = null;
        for (PawnColor c : PawnColor.values()) {
            if (character.getStudentsColorCount(c) > 0) {
                cardColor = c;
            }
        }

        Map<PawnColor, Integer> outMap = new HashMap<>();
        outMap.put(cardColor, 1);

        Map<PawnColor, Integer> entranceCount = new HashMap<>();
        for (PawnColor c : PawnColor.values()) {
            entranceCount.put(c, player1.getSchool().getEntranceCount(c));
        }
        Map<PawnColor, Integer> cardCount = new HashMap<>();
        for (PawnColor c : PawnColor.values()) {
            cardCount.put(c, character.getStudentsColorCount(c));
        }

        character.use(match, player1.getId(), inMap, outMap);
        for (PawnColor c : PawnColor.values()) {
            if (c == entranceColor) {
                assertEquals((int) entranceCount.get(c), player1.getSchool().getEntranceCount(c) + inMap.getOrDefault(c, 0) - outMap.getOrDefault(c, 0));
            } else if (c == cardColor) {
                assertEquals((int) cardCount.get(c), character.getStudentsColorCount(c) + outMap.getOrDefault(c, 0) - inMap.getOrDefault(c, 0));
            } else {
                assertEquals((int) entranceCount.get(c), player1.getSchool().getEntranceCount(c));
                assertEquals((int) cardCount.get(c), character.getStudentsColorCount(c));
            }
        }
    }
}
