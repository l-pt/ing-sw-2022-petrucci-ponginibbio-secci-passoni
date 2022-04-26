package it.polimi.ingsw.model;

import it.polimi.ingsw.*;
import it.polimi.ingsw.character.Character;
import it.polimi.ingsw.character.StudentCharacter;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchTest extends TestCase {
    @Test
    public void setupTest() throws IllegalMoveException {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(1, "test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(0, List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player2), TowerColor.BLACK);
        Match match = new Match(0, List.of(team1, team2), List.of(player1, player2), true);

        assertEquals(8, team1.getTowers().size());
        assertEquals(8, team2.getTowers().size());

        assertEquals(2, match.getClouds().size());
        for (Cloud cloud : match.getClouds()) {
            assertEquals(3, cloud.getStudents().size());
        }

        int motherNaturePos = match.getPosMotherNature();
        for (int i = 0; i < match.getIslands().size(); ++i) {
            Island island = match.getIslands().get(i);
            if (i == motherNaturePos || i == (motherNaturePos + 6) % match.getIslands().size()) {
                assertEquals(0, island.getStudents().size());
            } else {
                assertEquals(1, island.getStudents().size());
            }
        }

        //130 -
        //10 (students on islands) -
        //6 (clouds) -
        //7*2 (players) = 100
        int expectedBagCount = 100;
        for (int i = 0; i < 3; ++i) {
            Character character = match.getCharacter(i);
            if (character instanceof StudentCharacter) {
                expectedBagCount -= ((StudentCharacter) character).getInitialStudentsNumber();
            }
        }
        assertEquals(expectedBagCount, match.getStudentBag().size());

        assertEquals(5, match.getProfessors().size());

        assertEquals(18, match.getCoins());

        for (int i = 0; i < 3; ++i) {
            for (int j = i + 1; j < 3; ++j) {
                Character c1 = match.getCharacter(i);
                Character c2 = match.getCharacter(j);
                assertNotSame(c1, c2);
            }
        }
    }
    @Test
    public void matchTest() throws IllegalMoveException {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(1, "test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player(2, "test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player(3, "test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(0, List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player3, player4), TowerColor.BLACK);
        Match match = new Match(0, List.of(team1, team2), List.of(player1, player2, player3, player4), true);

        assertEquals(team1, match.getTeamFromColor(TowerColor.WHITE));
        assertEquals(team2, match.getTeamFromColor(TowerColor.BLACK));

        assertEquals(team1, match.getTeamFromPlayer(player1));
        assertEquals(team1, match.getTeamFromPlayer(player2));
        assertEquals(team2, match.getTeamFromPlayer(player3));
        assertEquals(team2, match.getTeamFromPlayer(player4));

        assertEquals(player1, match.getPlayerFromId(0));
        assertEquals(player2, match.getPlayerFromId(1));
        assertEquals(player3, match.getPlayerFromId(2));
        assertEquals(player4, match.getPlayerFromId(3));

        assertEquals(0, match.getPosFromId(0));
        assertEquals(1, match.getPosFromId(1));
        assertEquals(2, match.getPosFromId(2));
        assertEquals(3, match.getPosFromId(3));
    }

    @Test
    public void orderTest() throws IllegalMoveException {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(1, "test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player(2, "test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player(3, "test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(0, List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player3, player4), TowerColor.BLACK);
        List<Player> playerOrder = new ArrayList<>(List.of(player1, player2, player3, player4));
        Match match = new Match(0, List.of(team1, team2), playerOrder, true);

        match.useAssistant(player1.getId(), 4);
        match.useAssistant(player2.getId(), 3);
        match.useAssistant(player3.getId(), 1);
        match.useAssistant(player4.getId(), 2);

        assertEquals(player3, match.getPlayersOrder().get(0));
        assertEquals(player4, match.getPlayersOrder().get(1));
        assertEquals(player2, match.getPlayersOrder().get(2));
        assertEquals(player1, match.getPlayersOrder().get(3));
    }

    @Test
    public void extractStudentTest() {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(1, "test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player(2, "test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player(3, "test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(0, List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player3, player4), TowerColor.BLACK);
        List<Player> playerOrder = new ArrayList<>(List.of(player1, player2, player3, player4));
        Match match = new Match(0, List.of(team1, team2), playerOrder, true);

        Map<PawnColor, Integer> oldBagState = new HashMap<>();
        for (Student student : match.getStudentBag()) {
            oldBagState.put(student.getColor(), oldBagState.getOrDefault(student.getColor(), 0) + 1);
        }
        int oldBagSize = match.getStudentBag().size();

        List<Student> extracted = match.extractStudent(10);

        assertEquals(Math.min(10, oldBagSize), extracted.size());

        Map<PawnColor, Integer> extractedMap = new HashMap<>();
        for (Student student : extracted) {
            extractedMap.put(student.getColor(), extractedMap.getOrDefault(student.getColor(), 0) + 1);
        }

        Map<PawnColor, Integer> newBagState = new HashMap<>();
        for (Student student : match.getStudentBag()) {
            newBagState.put(student.getColor(), newBagState.getOrDefault(student.getColor(), 0) + 1);
        }

        for (Map.Entry<PawnColor, Integer> entry : newBagState.entrySet()) {
            assertEquals(oldBagState.getOrDefault(entry.getKey(), 0) - extractedMap.getOrDefault(entry.getKey(), 0), (int) entry.getValue());
        }
    }

    @Test
    public void professorTest() throws IllegalMoveException {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(1, "test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player(2, "test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player(3, "test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(0, List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player3, player4), TowerColor.BLACK);
        List<Player> playerOrder = new ArrayList<>(List.of(player1, player2, player3, player4));
        Match match = new Match(0, List.of(team1, team2), playerOrder, true);

        Professor professor = match.removeProfessor(PawnColor.RED);
        assertEquals(PawnColor.RED, professor.getColor());

        assertEquals(4, match.getProfessors().size());
        for (Professor p : match.getProfessors()) {
            assertNotSame(PawnColor.RED, p.getColor());
        }

        player1.getSchool().addProfessor(professor);
        assertEquals(player1, match.whoHaveProfessor(PawnColor.RED));
    }

    @Test
    public void addStudentTest() throws IllegalMoveException {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(1, "test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player(2, "test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player(3, "test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(0, List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player3, player4), TowerColor.BLACK);
        Match match = new Match(0, List.of(team1, team2), List.of(player1, player2, player3, player4), true);

        PawnColor color = null;
        int oldEntranceCount = -1;
        for (PawnColor c : PawnColor.values()) {
            if ((oldEntranceCount = player1.getSchool().getEntranceCount(c)) > 0) {
                color = c;
                break;
            }
        }

        match.addStudent(color, player1.getId());

        assertEquals(1, player1.getSchool().getTableCount(color));
        assertEquals(oldEntranceCount - 1, player1.getSchool().getEntranceCount(color));

        assertEquals(player1, match.whoHaveProfessor(color));

        player1.getSchool().addStudentsToTable(List.of(new Student(color), new Student(color)));
        match.checkNumberStudents(color, player1);
        assertEquals(2, player1.getCoins());
    }

    @Test
    public void islandInfluenceTest() throws IllegalMoveException {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(1, "test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player(2, "test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player(3, "test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(0, List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player3, player4), TowerColor.BLACK);
        Match match = new Match(0, List.of(team1, team2), List.of(player1, player2, player3, player4), true);

        int islandIndex = -1;
        Island island = null;
        for (int i = 0; i < match.getIslands().size(); ++i) {
            if (match.getIslands().get(i).getStudents().size() > 0) {
                island = match.getIslands().get(i);
                islandIndex = i;
                break;
            }
        }
        PawnColor color = island.getStudents().get(0).getColor();
        player1.getSchool().addProfessor(new Professor(color));
        match.islandInfluence(islandIndex, false);
        assertEquals(1, island.getTowers().size());
        assertEquals(TowerColor.WHITE, island.getTowers().get(0).getColor());
    }

    @Test
    public void islandUnionTest() throws IllegalMoveException {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(1, "test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player(2, "test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player(3, "test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(0, List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player3, player4), TowerColor.BLACK);
        Match match = new Match(0, List.of(team1, team2), List.of(player1, player2, player3, player4), true);

        int islandIndex = -1;
        Island island = null;
        for (int i = 0; i < match.getIslands().size(); ++i) {
            if (match.getIslands().get(i).getStudents().size() > 0) {
                island = match.getIslands().get(i);
                islandIndex = i;
                break;
            }
        }
        PawnColor color = island.getStudents().get(0).getColor();
        player1.getSchool().addProfessor(new Professor(color));
        match.getIslands().get(match.islandIndex(islandIndex + 1)).addStudent(new Student(color));
        match.getIslands().get(match.islandIndex(islandIndex - 1)).addStudent(new Student(color));
        match.islandInfluence(match.islandIndex(islandIndex + 1), false);
        match.islandInfluence(match.islandIndex(islandIndex - 1), false);
        match.islandInfluence(islandIndex, false);

        assertEquals(5, team1.getTowers().size());
        assertEquals(10, match.getIslands().size());
        if (islandIndex == 11 || islandIndex == 0) {
            assertEquals(3, match.getIslands().get(0).getTowers().size());
        } else {
            assertEquals(3, match.getIslands().get(match.islandIndex(islandIndex - 1)).getTowers().size());
        }
        if (islandIndex == 11 || islandIndex == 0) {
            assertEquals(0, match.getPosMotherNature());
        } else {
            assertEquals(match.islandIndex(islandIndex - 1), match.getPosMotherNature());
        }
    }
    @Test
    public void cloudTest() throws IllegalMoveException {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(1, "test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player(2, "test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player(3, "test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(0, List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player3, player4), TowerColor.BLACK);
        Match match = new Match(0, List.of(team1, team2), List.of(player1, player2, player3, player4), true);

        Map<PawnColor, Integer> oldEntranceState = new HashMap<>();
        for (PawnColor c : PawnColor.values()) {
            oldEntranceState.put(c, player1.getSchool().getEntranceCount(c));
        }
        Map<PawnColor, Integer> cloudMap = new HashMap<>();
        for (Student s : match.getClouds().get(0).getStudents()) {
            cloudMap.put(s.getColor(), cloudMap.getOrDefault(s.getColor(), 0) + 1);
        }
        match.moveStudentsFromCloud(0, player1.getId());
        for (PawnColor c : PawnColor.values()) {
            assertEquals(oldEntranceState.get(c) + cloudMap.getOrDefault(c, 0), player1.getSchool().getEntranceCount(c));
        }
        assertEquals(0, match.getClouds().get(0).getStudents().size());
    }

    @Test
    public void moveMotherNatureTest() throws IllegalMoveException {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(1, "test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player(2, "test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player(3, "test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(0, List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player3, player4), TowerColor.BLACK);
        Match match = new Match(0, List.of(team1, team2), List.of(player1, player2, player3, player4), true);

        match.useAssistant(player1.getId(), 10);
        int motherNaturePos = match.getPosMotherNature();
        match.moveMotherNature(5, player1.getId());
        assertEquals(match.islandIndex(motherNaturePos + 5), match.getPosMotherNature());
    }

    @Test
    public void towersColorTest() throws IllegalMoveException {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(1, "test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player(2, "test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player(3, "test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(0, List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player3, player4), TowerColor.BLACK);
        Match match = new Match(0, List.of(team1, team2), List.of(player1, player2, player3, player4), true);

        player1.getSchool().addProfessor(new Professor(PawnColor.RED));
        match.getIslands().get(0).addStudents(List.of(new Student(PawnColor.RED)));
        match.getIslands().get(3).addStudents(List.of(new Student(PawnColor.RED)));
        match.getIslands().get(9).addStudents(List.of(new Student(PawnColor.RED)));
        match.islandInfluence(0, false);
        match.islandInfluence(3, false);
        match.islandInfluence(9, false);

        assertEquals(3, match.getTowersByColor(TowerColor.WHITE));
        assertEquals(8, match.getTowersByColor(TowerColor.WHITE) + team1.getTowers().size());
    }

    @Test
    public void winningTeamTest() throws IllegalMoveException {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(1, "test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player(2, "test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player(3, "test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(0, List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player3, player4), TowerColor.BLACK);
        Match match = new Match(0, List.of(team1, team2), List.of(player1, player2, player3, player4), true);

        player1.getSchool().addProfessor(new Professor(PawnColor.RED));
        match.getIslands().get(0).addStudents(List.of(new Student(PawnColor.RED), new Student(PawnColor.RED)));
        match.getIslands().get(3).addStudents(List.of(new Student(PawnColor.RED), new Student(PawnColor.RED)));
        match.getIslands().get(9).addStudents(List.of(new Student(PawnColor.RED), new Student(PawnColor.RED)));
        match.islandInfluence(0, false);
        match.islandInfluence(3, false);
        match.islandInfluence(9, false);

        assertEquals(team1, match.getWinningTeam());

        player3.getSchool().addProfessor(new Professor(PawnColor.YELLOW));
        player3.getSchool().addProfessor(new Professor(PawnColor.PINK));
        match.getIslands().get(1).addStudents(List.of(new Student(PawnColor.YELLOW), new Student(PawnColor.YELLOW)));
        match.getIslands().get(4).addStudents(List.of(new Student(PawnColor.YELLOW), new Student(PawnColor.YELLOW)));
        match.getIslands().get(10).addStudents(List.of(new Student(PawnColor.YELLOW), new Student(PawnColor.YELLOW)));
        match.islandInfluence(1, false);
        match.islandInfluence(4, false);
        match.islandInfluence(10, false);
        assertEquals(team2, match.getWinningTeam());
    }

    @Test
    public void useAssistantTest() throws IllegalMoveException {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(1, "test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player(2, "test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player(3, "test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(0, List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player3, player4), TowerColor.BLACK);
        Match match = new Match(0, List.of(team1, team2), List.of(player1, player2, player3, player4), true);

        match.useAssistant(player1.getId(), 1);
        assertEquals(1, player1.getCurrentAssistant().getValue());

        Exception e = assertThrows(IllegalMoveException.class, () -> match.useAssistant(player2.getId(), 1));
        assertEquals("Cannot play this assistant", e.getMessage());
    }
}
