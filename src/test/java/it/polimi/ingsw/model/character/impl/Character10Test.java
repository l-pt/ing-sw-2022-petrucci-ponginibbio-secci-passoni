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
    public void invalidParametersTest() throws IllegalMoveException {
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
    }

    @Test
    public void useTest() throws IllegalMoveException {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(1, "test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(0, List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player2), TowerColor.BLACK);
        Match match = new Match(0, List.of(team1, team2), List.of(player1, player2), true);
        Character10 character = new Character10();

        //Get the color of a student that we can extract from the entrance
        PawnColor entranceColor = null;
        for (PawnColor c : PawnColor.values()) {
            if (player1.getSchool().getEntranceCount(c) > 0) {
                entranceColor = c;
                break;
            }
        }
        //Put it in the entranceToCard map
        Map<PawnColor, Integer> entranceToDiningRoom = new HashMap<>();
        entranceToDiningRoom.put(entranceColor, 1);

        //Get the color of a student that we can extract from the dining room
        player1.getSchool().addStudentsToTable(match.extractStudent(1));
        PawnColor diningRoomColor = null;
        for (PawnColor c : PawnColor.values()) {
            if (player1.getSchool().getTableCount(c) > 0) {
                diningRoomColor = c;
            }
        }
        //Put it in the cardToEntrance map
        Map<PawnColor, Integer> diningRoomToEntrance = new HashMap<>();
        diningRoomToEntrance.put(diningRoomColor, 1);

        //Save current number of all students on the dining room
        Map<PawnColor, Integer> oldDiningRoomState = new HashMap<>();
        for (PawnColor c : PawnColor.values()) {
            oldDiningRoomState.put(c, player1.getSchool().getTableCount(c));
        }

        //Save current number of all students on the entrance
        Map<PawnColor, Integer> oldEntranceState = new HashMap<>();
        for (PawnColor c : PawnColor.values()) {
            oldEntranceState.put(c, player1.getSchool().getEntranceCount(c));
        }

        character.use(match, player1.getId(), entranceToDiningRoom, diningRoomToEntrance);

        //Save new number of all students on the dining room
        Map<PawnColor, Integer> newDiningRoomState = new HashMap<>();
        for (PawnColor c : PawnColor.values()) {
            newDiningRoomState.put(c, player1.getSchool().getTableCount(c));
        }

        //Save new number of all students in the entrance
        Map<PawnColor, Integer> newEntranceState = new HashMap<>();
        for (PawnColor c : PawnColor.values()) {
            newEntranceState.put(c, player1.getSchool().getEntranceCount(c));
        }

        Character7Test.checkTransaction(entranceToDiningRoom, diningRoomToEntrance, oldEntranceState, newEntranceState, oldDiningRoomState, newDiningRoomState);
    }
}
