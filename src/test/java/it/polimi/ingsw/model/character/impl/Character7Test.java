package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Character7Test extends TestCase {
    @Test
    public void invalidParametersTest() {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(List.of(player2), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2), true);
        Character7 character = new Character7();
        character.setup(match);

        Exception e = assertThrows(IllegalMoveException.class, () -> character.use(match, player1.getName(), new HashMap<>(), new HashMap<>()));
        Assertions.assertEquals(e.getMessage(), "Invalid student number");

        e = assertThrows(IllegalMoveException.class, () -> character.use(match, player1.getName(), Map.of(PawnColor.RED, 1), Map.of(PawnColor.RED, 1, PawnColor.GREEN, 2)));
        Assertions.assertEquals(e.getMessage(), "Different map sizes");

        //Get a color which is not present on the card
        List<PawnColor> absentColors = new ArrayList<>(List.of(PawnColor.values()));
        PawnColor characterColor = null;
        for (Student student : character.getStudents()) {
            characterColor = student.getColor();
            absentColors.remove(student.getColor());
        }
        if (absentColors.size() == 0) {
            character.removeStudentsByColor(PawnColor.RED, character.getStudentsColorCount(PawnColor.RED));
            absentColors.add(PawnColor.RED);
        }
        PawnColor absentColor = absentColors.get(0);
        PawnColor finalAbsentColor = absentColor;
        e = assertThrows(IllegalMoveException.class, () -> character.use(match, player1.getName(), Map.of(PawnColor.RED, 1), Map.of(finalAbsentColor, 1)));
        Assertions.assertEquals("There are not enough students with color " + absentColor.name() + " on this character", e.getMessage());

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
        character.addStudents(List.of(new Student(absentColor)));
        PawnColor finalAbsentColor1 = absentColor;
        PawnColor finalCharacterColor = characterColor;
        e = assertThrows(IllegalMoveException.class, () -> character.use(match, player1.getName(), Map.of(finalAbsentColor1, 1), Map.of(finalCharacterColor, 1)));
        Assertions.assertEquals("There are not enough students with color " + absentColor.name() + " in the entrance", e.getMessage());
    }

    @Test
    public void useTest2() throws IllegalMoveException {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(List.of(player2), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2), true);
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

        character.use(match, player1.getName(), entranceToCard, cardToEntrance);

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
        Assertions.assertEquals(aNewExpected, aNew);
        Assertions.assertEquals(bNewExpected, bNew);
    }
}
