package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class Character10Test extends TestCase {
    @Test
    public void invalidParametersTest() throws IllegalMoveException {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(List.of(player2), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2), true);
        Character10 character = new Character10();

        Exception e = assertThrows(IllegalMoveException.class, () -> character.use(match, player1.getName(), new HashMap<>(), new HashMap<>()));
        Assertions.assertEquals(e.getMessage(), "Invalid student number");

        e = assertThrows(IllegalMoveException.class, () -> character.use(match, player1.getName(), Map.of(PawnColor.RED, 1), Map.of(PawnColor.RED, 1, PawnColor.GREEN, 2)));
        Assertions.assertEquals(e.getMessage(), "Different map sizes");

        //Move a student from entrance to table
        PawnColor tableColor = null;
        for (Student student : player1.getSchool().getEntrance()) {
            player1.getSchool().addStudentFromEntranceToTable(student.getColor());
            tableColor = student.getColor();
            break;
        }

        //Get a color which is not present on the table
        List<PawnColor> absentColors = new ArrayList<>(List.of(PawnColor.values()));
        for (PawnColor color : PawnColor.values()) {
            if (player1.getSchool().getTableCount(color) > 0) {
                absentColors.remove(color);
            }
        }
        PawnColor absentColor = absentColors.get(0);
        PawnColor finalAbsentColor = absentColor;
        e = assertThrows(IllegalMoveException.class, () -> character.use(match, player1.getName(), Map.of(PawnColor.RED, 1), Map.of(finalAbsentColor, 1)));
        Assertions.assertEquals("There are not enough students with color " + absentColor.name() + " in the dining room", e.getMessage());

        //Get a color which is not present in the entrance
        absentColors.clear();
        absentColors.addAll(List.of(PawnColor.values()));
        for (PawnColor color : PawnColor.values()) {
            if (player1.getSchool().getEntranceCount(color) > 0) {
                absentColors.remove(color);
            }
        }
        if (absentColors.size() == 0) {
            player1.getSchool().removeEntranceStudentsByColor(PawnColor.RED, player1.getSchool().getEntranceCount(PawnColor.RED));
            absentColors.add(PawnColor.RED);
        }
        absentColor = absentColors.get(0);
        PawnColor finalAbsentColor1 = absentColor;
        PawnColor finalTableColor = tableColor;
        e = assertThrows(IllegalMoveException.class, () -> character.use(match, player1.getName(), Map.of(finalAbsentColor1, 1), Map.of(finalTableColor, 1)));
        Assertions.assertEquals("There are not enough students with color " + absentColor.name() + " in the entrance", e.getMessage());
    }

    @Test
    public void useTest() throws IllegalMoveException {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(List.of(player2), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2), true);
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
        player1.getSchool().addStudentsToTable(match.extractStudents(1));
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

        character.use(match, player1.getName(), entranceToDiningRoom, diningRoomToEntrance);

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
