package it.polimi.ingsw.model;

import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.model.character.StudentCharacter;
import it.polimi.ingsw.model.character.impl.Character1;
import it.polimi.ingsw.model.character.impl.Character2;
import it.polimi.ingsw.model.character.impl.Character3;
import it.polimi.ingsw.model.character.impl.Character4;
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
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(List.of(player2), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2), true);

        assertTrue(match.isExpert());

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

        assertEquals(3, match.getCharacters().size());

        Exception e = assertThrows(IllegalMoveException.class, () -> match.getCharacter(3));
        assertEquals("Invalid character index 3", e.getMessage());

        List<Character> characters = match.getCharacters();
        Character1 c1 = new Character1();
        characters.set(0, c1);
        characters.set(1, new Character2());
        characters.set(2, new Character3());

        e = assertThrows(IllegalMoveException.class, () -> match.getCharacterFromType(Character4.class));
        assertEquals("There are no characters with class " + Character4.class, e.getMessage());

        assertEquals(c1, match.getCharacterFromType(Character1.class));
    }

    @Test
    public void matchTest() throws IllegalMoveException {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player("test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player("test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(List.of(player3, player4), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2, player3, player4), true);

        assertEquals(team1, match.getTeamFromColor(TowerColor.WHITE));
        assertEquals(team2, match.getTeamFromColor(TowerColor.BLACK));

        assertEquals(team1, match.getTeamFromPlayer(player1));
        assertEquals(team1, match.getTeamFromPlayer(player2));
        assertEquals(team2, match.getTeamFromPlayer(player3));
        assertEquals(team2, match.getTeamFromPlayer(player4));

        assertEquals(player1, match.getPlayerFromName("test1"));
        assertEquals(player2, match.getPlayerFromName("test2"));
        assertEquals(player3, match.getPlayerFromName("test3"));
        assertEquals(player4, match.getPlayerFromName("test4"));

        assertEquals(0, match.getPosFromName("test1"));
        assertEquals(1, match.getPosFromName("test2"));
        assertEquals(2, match.getPosFromName("test3"));
        assertEquals(3, match.getPosFromName("test4"));
    }

    @Test
    public void orderTest() throws IllegalMoveException {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player("test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player("test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(List.of(player3, player4), TowerColor.BLACK);
        List<Player> playerOrder = new ArrayList<>(List.of(player1, player2, player3, player4));
        Match match = new Match(List.of(team1, team2), playerOrder, true);

        match.useAssistant(player1.getName(), 4);
        match.useAssistant(player2.getName(), 3);
        match.useAssistant(player3.getName(), 1);
        match.useAssistant(player4.getName(), 2);

        assertEquals(player3, match.getPlayersOrder().get(0));
        assertEquals(player4, match.getPlayersOrder().get(1));
        assertEquals(player2, match.getPlayersOrder().get(2));
        assertEquals(player1, match.getPlayersOrder().get(3));
    }

    @Test
    public void extractStudentTest() {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player("test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player("test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(List.of(player3, player4), TowerColor.BLACK);
        List<Player> playerOrder = new ArrayList<>(List.of(player1, player2, player3, player4));
        Match match = new Match(List.of(team1, team2), playerOrder, true);

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
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player("test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player("test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(List.of(player3, player4), TowerColor.BLACK);
        List<Player> playerOrder = new ArrayList<>(List.of(player1, player2, player3, player4));
        Match match = new Match(List.of(team1, team2), playerOrder, true);

        Professor professor = match.removeProfessor(PawnColor.RED);
        assertEquals(PawnColor.RED, professor.getColor());
        Exception e = assertThrows(IllegalMoveException.class, () -> match.removeProfessor(PawnColor.RED));
        assertEquals("No professor of color RED on the table", e.getMessage());

        assertEquals(4, match.getProfessors().size());
        for (Professor p : match.getProfessors()) {
            assertNotSame(PawnColor.RED, p.getColor());
        }

        player1.getSchool().addProfessor(professor);
        assertEquals(player1, match.whoHaveProfessor(PawnColor.RED));
    }

    @Test
    public void addStudentTest() throws IllegalMoveException {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player("test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player("test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(List.of(player3, player4), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2, player3, player4), true);

        PawnColor color = null;
        int oldEntranceCount = -1;
        for (PawnColor c : PawnColor.values()) {
            if ((oldEntranceCount = player1.getSchool().getEntranceCount(c)) > 0) {
                color = c;
                break;
            }
        }

        match.playerMoveStudents(color, 1, player1.getName());
        Exception e = assertThrows(IllegalArgumentException.class, () -> match.playerMoveStudents(PawnColor.RED, -1, player1.getName()));
        assertEquals("n must be non negative", e.getMessage());


        assertEquals(1, player1.getSchool().getTableCount(color));
        assertEquals(oldEntranceCount - 1, player1.getSchool().getEntranceCount(color));

        assertEquals(player1, match.whoHaveProfessor(color));

        player1.getSchool().addStudentsToTable(List.of(new Student(color), new Student(color)));
        match.checkNumberStudents(color, player1);
        assertEquals(2, player1.getCoins());
    }

    @Test
    public void stealProfessorTest() throws IllegalMoveException {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player("test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player("test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(List.of(player3, player4), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2, player3, player4), true);

        player1.getSchool().addStudentsToEntrance(List.of(new Student(PawnColor.RED), new Student(PawnColor.RED)));
        match.playerMoveStudents(PawnColor.RED, 2, player1.getName());
        assertEquals(player1, match.whoHaveProfessor(PawnColor.RED));


        player3.getSchool().addStudentsToEntrance(List.of(new Student(PawnColor.RED), new Student(PawnColor.RED), new Student(PawnColor.RED)));
        match.playerMoveStudents(PawnColor.RED, 3, player3.getName());
        assertEquals(player3, match.whoHaveProfessor(PawnColor.RED));
    }

    @Test
    public void towersExchangeTest() throws IllegalMoveException {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player("test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player("test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(List.of(player3, player4), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2, player3, player4), true);

        match.getIslands().get(0).addTowers(List.of(new Tower(TowerColor.WHITE)));
        match.getIslands().get(0).addStudent(new Student(PawnColor.RED));
        match.getIslands().get(0).addStudent(new Student(PawnColor.RED));
        player3.getSchool().addStudentsToEntrance(List.of(new Student(PawnColor.RED), new Student(PawnColor.RED)));
        match.playerMoveStudents(PawnColor.RED, 2, player3.getName());
        match.islandInfluence(0, true);

        assertEquals(1, match.getIslands().get(0).getTowers().size());
        assertEquals(TowerColor.BLACK, match.getIslands().get(0).getTowers().get(0).getColor());
    }

    @Test
    public void islandInfluenceTest() throws IllegalMoveException {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player("test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player("test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(List.of(player3, player4), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2, player3, player4), true);

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
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player("test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player("test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(List.of(player3, player4), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2, player3, player4), true);

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
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player("test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player("test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(List.of(player3, player4), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2, player3, player4), true);

        Map<PawnColor, Integer> oldEntranceState = new HashMap<>();
        for (PawnColor c : PawnColor.values()) {
            oldEntranceState.put(c, player1.getSchool().getEntranceCount(c));
        }
        Map<PawnColor, Integer> cloudMap = new HashMap<>();
        for (Student s : match.getClouds().get(0).getStudents()) {
            cloudMap.put(s.getColor(), cloudMap.getOrDefault(s.getColor(), 0) + 1);
        }
        match.moveStudentsFromCloud(0, player1.getName());
        for (PawnColor c : PawnColor.values()) {
            assertEquals(oldEntranceState.get(c) + cloudMap.getOrDefault(c, 0), player1.getSchool().getEntranceCount(c));
        }
        assertEquals(0, match.getClouds().get(0).getStudents().size());
    }

    @Test
    public void moveMotherNatureTest() throws IllegalMoveException {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player("test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player("test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(List.of(player3, player4), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2, player3, player4), true);

        match.useAssistant(player1.getName(), 10);
        int motherNaturePos = match.getPosMotherNature();
        match.moveMotherNature(5, player1.getName());
        assertEquals(match.islandIndex(motherNaturePos + 5), match.getPosMotherNature());
    }

    @Test
    public void towersColorTest() throws IllegalMoveException {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player("test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player("test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(List.of(player3, player4), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2, player3, player4), true);

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
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player("test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player("test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(List.of(player3, player4), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2, player3, player4), true);

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
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.WHITE, Wizard.GREEN);
        Player player3 = new Player("test3", TowerColor.BLACK, Wizard.PINK);
        Player player4 = new Player("test4", TowerColor.BLACK, Wizard.PURPLE);
        Team team1 = new Team(List.of(player1, player2), TowerColor.WHITE);
        Team team2 = new Team(List.of(player3, player4), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2, player3, player4), true);

        Exception e = assertThrows(IllegalMoveException.class, () -> match.useAssistant(player1.getName(), 0));
        assertEquals("The value must be between 1 and 10", e.getMessage());

        match.useAssistant(player1.getName(), 1);
        assertEquals(1, player1.getCurrentAssistant().getValue());

        e = assertThrows(IllegalMoveException.class, () -> match.useAssistant(player1.getName(), 1));
        assertEquals("You don't have an assistant with value 1", e.getMessage());

        e = assertThrows(IllegalMoveException.class, () -> match.useAssistant(player2.getName(), 1));
        assertEquals("Cannot play this assistant", e.getMessage());

        for (int i = 2; i <= 10; ++i) {
            match.useAssistant(player1.getName(), i);
        }
        assertTrue(match.isLastTurn());
    }

    @Test
    public void getFromTest()  throws IllegalMoveException {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(List.of(player2), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2), true);

        Exception e = assertThrows(IllegalMoveException.class, () -> match.getTeamFromColor(TowerColor.GRAY));
        assertEquals("A team with tower color GRAY does not exist", e.getMessage());

        e = assertThrows(IllegalMoveException.class, () -> match.getTeamFromPlayer(new Player("test3", TowerColor.GRAY, Wizard.PINK)));
        assertEquals("Player is not in a team", e.getMessage());

        e = assertThrows(IllegalMoveException.class, () -> match.getPlayerFromName("abc"));
        assertEquals("Invalid Name", e.getMessage());

        e = assertThrows(IllegalMoveException.class, () -> match.getPosFromName("abc"));
        assertEquals("Invalid Name", e.getMessage());

        e = assertThrows(IllegalMoveException.class, () -> match.moveStudentsFromCloud(4, "test1"));
        assertEquals("Invalid cloud index", e.getMessage());
    }

    @Test
    public void lastTurnTest() throws IllegalMoveException {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(List.of(player2), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2), true);

        match.extractStudent(match.getStudentBag().size() + 1);
        assertTrue(match.isLastTurn());
    }

    @Test
    public void endGameTest() throws IllegalMoveException {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.BLACK, Wizard.GREEN);
        Player player3 = new Player("test3", TowerColor.GRAY, Wizard.PINK);
        Team team1 = new Team(List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(List.of(player2), TowerColor.BLACK);
        Team team3 = new Team(List.of(player3), TowerColor.GRAY);
        Match match = new ThreePlayersMatch(List.of(team1, team2, team3), List.of(player1, player2, player3), true);

        team1.removeTowers(5);
        match.getIslands().get(0).addStudent(new Student(PawnColor.RED));
        player1.getSchool().addStudentsToEntrance(List.of(new Student(PawnColor.RED)));
        match.playerMoveStudent(PawnColor.RED, player1.getName());
        match.islandInfluence(0, false);

        assertTrue(match.isGameFinished());
    }

    @Test
    public void endGameTest2() throws IllegalMoveException {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.BLACK, Wizard.GREEN);
        Player player3 = new Player("test3", TowerColor.GRAY, Wizard.PINK);
        Team team1 = new Team(List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(List.of(player2), TowerColor.BLACK);
        Team team3 = new Team(List.of(player3), TowerColor.GRAY);
        Match match = new ThreePlayersMatch(List.of(team1, team2, team3), List.of(player1, player2, player3), true);

        team1.removeTowers(5);
        match.getIslands().get(0).addStudent(new Student(PawnColor.RED));
        match.getIslands().get(0).addStudent(new Student(PawnColor.RED));
        player1.getSchool().addStudentsToEntrance(List.of(new Student(PawnColor.RED)));
        match.playerMoveStudent(PawnColor.RED, player1.getName());
        match.getIslands().get(0).addTowers(List.of(new Tower(TowerColor.BLACK)));
        match.islandInfluence(0, false);

        assertTrue(match.isGameFinished());
    }

    @Test
    public void threeIslandsEndGameTest() throws IllegalMoveException {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.BLACK, Wizard.GREEN);
        Player player3 = new Player("test3", TowerColor.GRAY, Wizard.PINK);
        Team team1 = new Team(List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(List.of(player2), TowerColor.BLACK);
        Team team3 = new Team(List.of(player3), TowerColor.GRAY);
        Match match = new ThreePlayersMatch(List.of(team1, team2, team3), List.of(player1, player2, player3), true);

        List<Island> islands = match.getIslands();
        for (int i = islands.size() - 1; i > 3; --i) {
            islands.remove(i);
        }
        match.uniteIslands(0, 1, false);
    }

    @Test
    public void resetAbilityTest() {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.BLACK, Wizard.GREEN);
        Player player3 = new Player("test3", TowerColor.GRAY, Wizard.PINK);
        Team team1 = new Team(List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(List.of(player2), TowerColor.BLACK);
        Team team3 = new Team(List.of(player3), TowerColor.GRAY);
        Match match = new ThreePlayersMatch(List.of(team1, team2, team3), List.of(player1, player2, player3), true);

        match.setDrawAllowed(true);
        match.getInfluencePolicy().setCountTowers(false);
        match.getInfluencePolicy().setExcludedColor(PawnColor.RED);
        player1.setAdditionalInfluence(1);
        player1.setAdditionalMoves(1);

        match.resetAbility();

        assertFalse(match.getDrawAllowed());
        assertTrue(match.getInfluencePolicy().getCountTowers());
        assertNull(match.getInfluencePolicy().getExcludedColor());
        assertEquals(0, player1.getAdditionalInfluence());
        assertEquals(0, player1.getAdditionalMoves());
    }
}
