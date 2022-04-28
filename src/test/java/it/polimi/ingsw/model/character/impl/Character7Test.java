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
    public void invalidParametersTest() {
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
    }

    @Test
    public void useTest2() throws IllegalMoveException {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(1, "test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(0, List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player2), TowerColor.BLACK);
        Match match = new Match(0, List.of(team1, team2), List.of(player1, player2), true);
        Character7 character = new Character7();
        character.setup(match);

        //Get the color of a student that we can extract from the entrance
        PawnColor entranceColor = null;
        for (PawnColor c : PawnColor.values()) {
            if (player1.getSchool().getEntranceCount(c) > 0) {
                entranceColor = c;
                break;
            }
        }
        //Put it in the entranceToCard map
        Map<PawnColor, Integer> entranceToCard = new HashMap<>();
        entranceToCard.put(entranceColor, 1);

        //Get the color of a student that we can extract from the card
        PawnColor cardColor = null;
        for (PawnColor c : PawnColor.values()) {
            if (character.getStudentsColorCount(c) > 0) {
                cardColor = c;
            }
        }
        //Put it in the cardToEntrance map
        Map<PawnColor, Integer> cardToEntrance = new HashMap<>();
        cardToEntrance.put(cardColor, 1);

        //Save current number of all students on the card
        Map<PawnColor, Integer> oldCardState = new HashMap<>();
        for (PawnColor c : PawnColor.values()) {
            oldCardState.put(c, character.getStudentsColorCount(c));
        }

        //Save current number of all students on the entrance
        Map<PawnColor, Integer> oldEntranceState = new HashMap<>();
        for (PawnColor c : PawnColor.values()) {
            oldEntranceState.put(c, player1.getSchool().getEntranceCount(c));
        }

        character.use(match, player1.getId(), entranceToCard, cardToEntrance);

        //Save new number of all students on the card
        Map<PawnColor, Integer> newCardState = new HashMap<>();
        for (PawnColor c : PawnColor.values()) {
            newCardState.put(c, character.getStudentsColorCount(c));
        }

        //Save new number of all students in the entrance
        Map<PawnColor, Integer> newEntranceState = new HashMap<>();
        for (PawnColor c : PawnColor.values()) {
            newEntranceState.put(c, player1.getSchool().getEntranceCount(c));
        }

        checkTransaction(entranceToCard, cardToEntrance, oldEntranceState, newEntranceState, oldCardState, newCardState);
    }

    public static void checkTransaction(Map<PawnColor, Integer> aToB, Map<PawnColor, Integer> bToA, Map<PawnColor, Integer> aOld,
                                           Map<PawnColor, Integer> aNew, Map<PawnColor, Integer> bOld, Map<PawnColor, Integer> bNew) {
        Map<PawnColor, Integer> aNewExpected = new HashMap<>(aOld);
        Map<PawnColor, Integer> bNewExpected = new HashMap<>(bOld);
        for (Map.Entry<PawnColor, Integer> entry : aToB.entrySet()) {
            aNewExpected.put(entry.getKey(), aNewExpected.get(entry.getKey()) - entry.getValue());
            bNewExpected.put(entry.getKey(), bNewExpected.get(entry.getKey()) + entry.getValue());
        }
        for (Map.Entry<PawnColor, Integer> entry : bToA.entrySet()) {
            bNewExpected.put(entry.getKey(), bNewExpected.get(entry.getKey()) - entry.getValue());
            aNewExpected.put(entry.getKey(), aNewExpected.get(entry.getKey()) + entry.getValue());
        }
        assertEquals(aNewExpected, aNew);
        assertEquals(bNewExpected, bNew);
    }
}
