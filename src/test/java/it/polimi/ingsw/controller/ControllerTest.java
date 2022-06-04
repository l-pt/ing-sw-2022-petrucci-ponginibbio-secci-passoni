package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.character.impl.*;
import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;
import it.polimi.ingsw.protocol.message.*;
import it.polimi.ingsw.protocol.message.character.*;
import it.polimi.ingsw.server.Server;
import junit.framework.TestCase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ControllerTest extends TestCase {
    private static Server server;
    static {
        try {
            server = new Server();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public synchronized void setup3PlayersController() throws IllegalMoveException {
        server.setMatchParameters(3, true);
        List<String> names = List.of("test1", "test2", "test3");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        Player player1 = controller.getMatch().getPlayerFromName(name1);
        Team team1 = controller.getMatch().getTeamFromPlayer(player1);

        Assertions.assertEquals(3, controller.getMatch().getPlayersOrder().size());
        Assertions.assertEquals(6, team1.getTowers().size());
        Assertions.assertEquals(4, controller.getMatch().getClouds().get(0).getStudents().size());
    }

    @Test
    public synchronized void setup4PlayersController() throws IllegalMoveException {
        server.setMatchParameters(4, true);
        List<String> names = List.of("test1", "test2", "test3", "test4");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        Player player1 = controller.getMatch().getPlayerFromName(name1);
        Team team1 = controller.getMatch().getTeamFromPlayer(player1);

        Assertions.assertEquals(4, controller.getMatch().getPlayersOrder().size());
        Assertions.assertEquals(8, team1.getTowers().size());
        Assertions.assertEquals(3, controller.getMatch().getClouds().get(0).getStudents().size());
    }

    @Test
    public synchronized void setup5PlayersController() {
        server.setMatchParameters(5, true);
        List<String> names = List.of("test1", "test2");
        Exception e = assertThrows(IllegalMoveException.class, () -> new Controller(server, names));
        Assertions.assertEquals(e.getMessage(), "The players number must be 2, 3 or 4");
    }

    @Test
    public synchronized void assistantMessage() throws IllegalMoveException {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        String name2 = controller.getMatch().getPlayersOrder().get(1).getName();
        Player player1 = controller.getMatch().getPlayerFromName(name1);
        Player player2 = controller.getMatch().getPlayerFromName(name2);

        Map<String, List<Message>> result = controller.handleMessage(name1, new SetAssistantMessage(15));
        Assertions.assertEquals(2, result.get(name1).size());
        Assertions.assertEquals("The value must be between 1 and 10", ((ErrorMessage)result.get(name1).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_ASSISTANT, result.get(name1).get(1).getMessageId());
        Assertions.assertNull(player1.getCurrentAssistant());
        Assertions.assertEquals(10, player1.getAssistants().size());

        result = controller.handleMessage(name1, new SetAssistantMessage(2));
        Assertions.assertEquals(1, result.get(name2).size());
        Assertions.assertEquals(MessageId.ASK_ASSISTANT, result.get(name2).get(0).getMessageId());
        Assertions.assertEquals(2, player1.getCurrentAssistant().getValue());
        Assertions.assertEquals(9, player1.getAssistants().size());

        result = controller.handleMessage(name2, new SetAssistantMessage(2));
        Assertions.assertEquals(2, result.get(name2).size());
        Assertions.assertEquals("You can't use this assistant because another player already used it this turn", ((ErrorMessage)result.get(name2).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_ASSISTANT, result.get(name2).get(1).getMessageId());
        Assertions.assertNull(player2.getCurrentAssistant());
        Assertions.assertEquals(10, player2.getAssistants().size());

        result = controller.handleMessage(name2, new SetAssistantMessage(5));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name1).get(0).getMessageId());
        Assertions.assertEquals(5, player2.getCurrentAssistant().getValue());
        Assertions.assertEquals(9, player2.getAssistants().size());

        result = controller.handleMessage(name1, new UseNoCharacterMessage());
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_ENTRANCE_STUDENT, result.get(name1).get(0).getMessageId());

        result = controller.handleMessage(name1, new SetAssistantMessage(2));
        Assertions.assertEquals(2, result.get(name1).size());
        Assertions.assertEquals("You don't have an assistant with value 2", ((ErrorMessage)result.get(name1).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_ASSISTANT, result.get(name1).get(1).getMessageId());
        Assertions.assertEquals(2, player1.getCurrentAssistant().getValue());
        Assertions.assertEquals(9, player1.getAssistants().size());
    }

    @Test
    public synchronized void entranceStudentMessageWithoutCharacter() throws IllegalMoveException {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        Player player1 = controller.getMatch().getPlayerFromName(name1);
        Map<Integer, Map<PawnColor, Integer>> islandStudents = new HashMap<>();
        Map<PawnColor, Integer> tableStudents = new HashMap<>();

        Map<String, List<Message>> result = controller.handleMessage(name1, new SetEntranceStudentMessage(islandStudents, tableStudents));
        Assertions.assertEquals(2, result.get(name1).size());
        Assertions.assertEquals("You have to move exactly three students from the entrance", ((ErrorMessage)result.get(name1).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_ENTRANCE_STUDENT, result.get(name1).get(1).getMessageId());

        PawnColor color1 = player1.getSchool().getEntrance().get(0).getColor();
        PawnColor color2 = player1.getSchool().getEntrance().get(1).getColor();
        PawnColor color3 = player1.getSchool().getEntrance().get(2).getColor();

        islandStudents.put(13, new HashMap<>());
        islandStudents.get(13).put(color1, islandStudents.get(13).getOrDefault(color1, 0) + 1);
        islandStudents.put(10, new HashMap<>());
        islandStudents.get(10).put(color2, islandStudents.get(10).getOrDefault(color2, 0) + 1);
        tableStudents.put(color3, tableStudents.getOrDefault(color3, 0) + 1);

        result = controller.handleMessage(name1, new SetEntranceStudentMessage(islandStudents, tableStudents));
        Assertions.assertEquals(2, result.get(name1).size());
        Assertions.assertEquals("Island 13 does not exist", ((ErrorMessage)result.get(name1).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_ENTRANCE_STUDENT, result.get(name1).get(1).getMessageId());

        islandStudents.clear();
        tableStudents.clear();
        islandStudents.put(3, new HashMap<>());
        islandStudents.get(3).put(color1, islandStudents.get(3).getOrDefault(color1, 0) + 1);
        islandStudents.put(10, new HashMap<>());
        islandStudents.get(10).put(color2, islandStudents.get(10).getOrDefault(color2, 0) + 1);
        tableStudents.put(color3, tableStudents.getOrDefault(color3, 0) + 1);

        result = controller.handleMessage(name1, new SetEntranceStudentMessage(islandStudents, tableStudents));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name1).get(0).getMessageId());
        Assertions.assertEquals(4, player1.getSchool().getEntrance().size());
        Assertions.assertEquals(color1, controller.getMatch().getIslands().get(3).getStudents().get(controller.getMatch().getIslands().get(3).getStudents().size() - 1).getColor());
        Assertions.assertEquals(color2, controller.getMatch().getIslands().get(10).getStudents().get(controller.getMatch().getIslands().get(10).getStudents().size() - 1).getColor());
        Assertions.assertEquals(1, player1.getSchool().getTableCount(color3));
    }

    @Test
    public synchronized void entranceStudentMessageWithCharacter() throws IllegalMoveException {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        Player player1 = controller.getMatch().getPlayerFromName(name1);
        player1.addCoin();
        controller.getMatch().getCharacters().set(0, new Character2());

        controller.getNextMessage().put(name1, new AskEntranceStudentMessage());
        Map<String, List<Message>> result = controller.handleMessage(name1, new UseCharacterMessage(1));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_ENTRANCE_STUDENT, result.get(name1).get(0).getMessageId());

        Map<Integer, Map<PawnColor, Integer>> islandStudents = new HashMap<>();
        Map<PawnColor, Integer> tableStudents = new HashMap<>();
        PawnColor color1 = player1.getSchool().getEntrance().get(0).getColor();
        PawnColor color2 = player1.getSchool().getEntrance().get(1).getColor();
        PawnColor color3 = player1.getSchool().getEntrance().get(2).getColor();

        islandStudents.put(3, new HashMap<>());
        islandStudents.get(3).put(color1, islandStudents.get(3).getOrDefault(color1, 0) + 1);
        islandStudents.put(10, new HashMap<>());
        islandStudents.get(10).put(color2, islandStudents.get(10).getOrDefault(color2, 0) + 1);
        tableStudents.put(color3, tableStudents.getOrDefault(color3, 0) + 1);

        result = controller.handleMessage(name1, new SetEntranceStudentMessage(islandStudents, tableStudents));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_MOTHER_NATURE, result.get(name1).get(0).getMessageId());
        Assertions.assertEquals(4, player1.getSchool().getEntrance().size());
        Assertions.assertEquals(color1, controller.getMatch().getIslands().get(3).getStudents().get(controller.getMatch().getIslands().get(3).getStudents().size() - 1).getColor());
        Assertions.assertEquals(color2, controller.getMatch().getIslands().get(10).getStudents().get(controller.getMatch().getIslands().get(10).getStudents().size() - 1).getColor());
        Assertions.assertEquals(1, player1.getSchool().getTableCount(color3));
    }

    @Test
    public synchronized void motherNatureMessageWithoutCharacter() throws IllegalMoveException {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        String name2 = controller.getMatch().getPlayersOrder().get(1).getName();
        Player player1 = controller.getMatch().getPlayerFromName(name1);
        Player player2 = controller.getMatch().getPlayerFromName(name2);
        player1.setCurrentAssistant(player1.getAssistantFromValue(3));
        player2.setCurrentAssistant(player2.getAssistantFromValue(9));
        int pos = controller.getMatch().getPosMotherNature();

        Map<String, List<Message>> result = controller.handleMessage(name1, new SetMotherNatureMessage(15));
        Assertions.assertEquals(2, result.get(name1).size());
        Assertions.assertEquals("Mother nature moves must be between 1 and " + (player1.getCurrentAssistant().getMoves() + player1.getAdditionalMoves()), ((ErrorMessage)result.get(name1).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_MOTHER_NATURE, result.get(name1).get(1).getMessageId());
        Assertions.assertEquals(pos, controller.getMatch().getPosMotherNature());

        result = controller.handleMessage(name1, new SetMotherNatureMessage(2));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name1).get(0).getMessageId());
        Assertions.assertEquals((pos + player1.getCurrentAssistant().getMoves()) % 12, controller.getMatch().getPosMotherNature());
    }

    @Test
    public synchronized void endGameMotherNatureMessage() throws IllegalMoveException {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        Player player1 = controller.getMatch().getPlayerFromName(name1);
        Team team1 = controller.getMatch().getTeamFromPlayer(player1);
        player1.setCurrentAssistant(player1.getAssistantFromValue(9));
        int pos = controller.getMatch().getPosMotherNature();

        team1.getTowers().clear();
        team1.addTower(new Tower(team1.getTowerColor()));
        Assertions.assertEquals(1, team1.getTowers().size());
        PawnColor color = player1.getSchool().getEntrance().get(0).getColor();
        controller.getMatch().playerMoveStudent(name1, color);
        Assertions.assertTrue(player1.getSchool().isColoredProfessor(color));
        controller.getMatch().getIslands().get((pos + 4) % controller.getMatch().getIslands().size()).addStudent(new Student(color));

        Map<String, List<Message>> result = controller.handleMessage(name1, new SetMotherNatureMessage(4));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.END_GAME, result.get(name1).get(0).getMessageId());
        Assertions.assertEquals(player1, ((EndGameMessage)result.get(name1).get(0)).getWinner().getPlayers().get(0));
    }

    @Test
    public synchronized void lastTurnMotherNatureMessageWithoutCharacter() throws IllegalMoveException {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        Player player1 = controller.getMatch().getPlayerFromName(name1);
        String name2 = controller.getMatch().getPlayersOrder().get(1).getName();
        player1.getAssistants().clear();
        player1.getAssistants().add(new Assistant(1, 1));
        int pos = controller.getMatch().getPosMotherNature();

        Map<String, List<Message>> result = controller.handleMessage(name1, new SetAssistantMessage(1));
        Assertions.assertEquals(1, result.get(name2).size());
        Assertions.assertEquals(MessageId.ASK_ASSISTANT, result.get(name2).get(0).getMessageId());
        Assertions.assertEquals(1, player1.getCurrentAssistant().getValue());

        result = controller.handleMessage(name1, new SetMotherNatureMessage(1));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name1).get(0).getMessageId());
        Assertions.assertEquals((pos + player1.getCurrentAssistant().getMoves()) % 12, controller.getMatch().getPosMotherNature());
    }

    @Test
    public synchronized void lastTurnMotherNatureMessageWithCharacter1() throws IllegalMoveException {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        Player player1 = controller.getMatch().getPlayerFromName(name1);
        String name2 = controller.getMatch().getPlayersOrder().get(1).getName();
        player1.getAssistants().clear();
        player1.getAssistants().add(new Assistant(1, 1));
        int pos = controller.getMatch().getPosMotherNature();

        Map<String, List<Message>> result = controller.handleMessage(name1, new SetAssistantMessage(1));
        Assertions.assertEquals(1, result.get(name2).size());
        Assertions.assertEquals(MessageId.ASK_ASSISTANT, result.get(name2).get(0).getMessageId());
        Assertions.assertEquals(1, player1.getCurrentAssistant().getValue());

        controller.getMatch().getPlayerFromName(name1).addCoin();
        controller.getMatch().getCharacters().set(0, new Character2());

        controller.getNextMessage().put(name1, new AskMotherNatureMessage());
        result = controller.handleMessage(name1, new UseCharacterMessage(1));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_MOTHER_NATURE, result.get(name1).get(0).getMessageId());

        result = controller.handleMessage(name1, new SetMotherNatureMessage(1));
        Assertions.assertEquals(1, result.get(name2).size());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name2).get(0).getMessageId());
        Assertions.assertEquals((pos + player1.getCurrentAssistant().getMoves()) % 12, controller.getMatch().getPosMotherNature());
    }

    @Test
    public synchronized void lastTurnMotherNatureMessageWithCharacter2() throws IllegalMoveException {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        Player player1 = controller.getMatch().getPlayerFromName(name1);
        String name2 = controller.getMatch().getPlayersOrder().get(1).getName();
        Player player2 = controller.getMatch().getPlayerFromName(name2);
        player2.getAssistants().clear();
        player2.getAssistants().add(new Assistant(2, 1));
        int pos = controller.getMatch().getPosMotherNature();

        Map<String, List<Message>> result = controller.handleMessage(name1, new SetAssistantMessage(1));
        Assertions.assertEquals(1, result.get(name2).size());
        Assertions.assertEquals(MessageId.ASK_ASSISTANT, result.get(name2).get(0).getMessageId());
        Assertions.assertEquals(1, player1.getCurrentAssistant().getValue());

        result = controller.handleMessage(name2, new SetAssistantMessage(2));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name1).get(0).getMessageId());
        Assertions.assertEquals(2, player2.getCurrentAssistant().getValue());

        controller.getMatch().getPlayerFromName(name2).addCoin();
        controller.getMatch().getCharacters().set(0, new Character2());

        controller.getNextMessage().put(name2, new AskMotherNatureMessage());
        result = controller.handleMessage(name2, new UseCharacterMessage(1));
        Assertions.assertEquals(1, result.get(name2).size());
        Assertions.assertEquals(MessageId.ASK_MOTHER_NATURE, result.get(name2).get(0).getMessageId());

        result = controller.handleMessage(name2, new SetMotherNatureMessage(1));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.END_GAME, result.get(name1).get(0).getMessageId());
        Assertions.assertEquals((pos + player1.getCurrentAssistant().getMoves()) % 12, controller.getMatch().getPosMotherNature());
    }

    @Test
    public synchronized void motherNatureMessageWithCharacter() throws IllegalMoveException {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        Player player1 = controller.getMatch().getPlayerFromName(name1);
        player1.setCurrentAssistant(player1.getAssistantFromValue(1));
        int pos = controller.getMatch().getPosMotherNature();
        player1.addCoin();
        controller.getMatch().getCharacters().set(0, new Character2());

        controller.getNextMessage().put(name1, new AskMotherNatureMessage());
        Map<String, List<Message>> result = controller.handleMessage(name1, new UseCharacterMessage(1));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_MOTHER_NATURE, result.get(name1).get(0).getMessageId());

        result = controller.handleMessage(name1, new SetMotherNatureMessage(1));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_CLOUD, result.get(name1).get(0).getMessageId());
        Assertions.assertEquals((pos + player1.getCurrentAssistant().getMoves()) % 12, controller.getMatch().getPosMotherNature());
    }

    @Test
    public synchronized void cloudMessageWithoutCharacter() throws IllegalMoveException {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        String name2 = controller.getMatch().getPlayersOrder().get(1).getName();

        Map<String, List<Message>> result = controller.handleMessage(name1, new SetCloudMessage(3));
        Assertions.assertEquals(2, result.get(name1).size());
        Assertions.assertEquals("Island number must be between 1 and " + controller.getMatch().getClouds().size(), ((ErrorMessage)result.get(name1).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_CLOUD, result.get(name1).get(1).getMessageId());
        Assertions.assertEquals(3, controller.getMatch().getClouds().get(0).getStudents().size());
        Assertions.assertEquals(3, controller.getMatch().getClouds().get(1).getStudents().size());

        result = controller.handleMessage(name1, new SetCloudMessage(1));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name1).get(0).getMessageId());
        Assertions.assertEquals(0, controller.getMatch().getClouds().get(1).getStudents().size());

        result = controller.handleMessage(name2, new SetCloudMessage(1));
        Assertions.assertEquals(2, result.get(name2).size());
        Assertions.assertEquals("Cloud already chosen by another player this turn", ((ErrorMessage)result.get(name2).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_CLOUD, result.get(name2).get(1).getMessageId());
        Assertions.assertEquals(0, controller.getMatch().getClouds().get(1).getStudents().size());
    }

    @Test
    public synchronized void cloudMessageWithCharacter1() throws IllegalMoveException {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        String name2 = controller.getMatch().getPlayersOrder().get(1).getName();
        Player player1 = controller.getMatch().getPlayerFromName(name1);
        player1.addCoin();
        controller.getMatch().getCharacters().set(0, new Character2());

        controller.getNextMessage().put(name1, new AskCloudMessage());
        Map<String, List<Message>> result = controller.handleMessage(name1, new UseCharacterMessage(1));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_CLOUD, result.get(name1).get(0).getMessageId());

        result = controller.handleMessage(name1, new SetCloudMessage(1));
        Assertions.assertEquals(1, result.get(name2).size());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name2).get(0).getMessageId());
        Assertions.assertEquals(0, controller.getMatch().getClouds().get(1).getStudents().size());
    }

    @Test
    public synchronized void cloudMessageWithCharacter2() throws IllegalMoveException {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        String name2 = controller.getMatch().getPlayersOrder().get(1).getName();
        Player player1 = controller.getMatch().getPlayerFromName(name1);
        Player player2 = controller.getMatch().getPlayerFromName(name2);

        player2.addCoin();
        controller.getMatch().getCharacters().set(0, new Character2());

        Map<String, List<Message>> result = controller.handleMessage(name1, new SetAssistantMessage(1));
        Assertions.assertEquals(1, result.get(name2).size());
        Assertions.assertEquals(MessageId.ASK_ASSISTANT, result.get(name2).get(0).getMessageId());
        Assertions.assertEquals(1, player1.getCurrentAssistant().getValue());

        result = controller.handleMessage(name2, new SetAssistantMessage(2));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name1).get(0).getMessageId());
        Assertions.assertEquals(2, player2.getCurrentAssistant().getValue());

        controller.getNextMessage().put(name2, new AskCloudMessage());
        result = controller.handleMessage(name2, new UseCharacterMessage(1));
        Assertions.assertEquals(1, result.get(name2).size());
        Assertions.assertEquals(MessageId.ASK_CLOUD, result.get(name2).get(0).getMessageId());

        result = controller.handleMessage(name2, new SetCloudMessage(1));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_ASSISTANT, result.get(name1).get(0).getMessageId());
        Assertions.assertEquals(3, controller.getMatch().getClouds().get(1).getStudents().size());
    }

    @Test
    public synchronized void characterColorIslandMessage() throws IllegalMoveException {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        Player player1 = controller.getMatch().getPlayerFromName(name1);

        controller.getMatch().getCharacters().set(0, new Character1());
        Character1 character1 = (Character1)controller.getMatch().getCharacters().get(0);
        character1.setup(controller.getMatch());
        player1.removeCoins(1);

        Map<String, List<Message>> result = controller.handleMessage(name1, new UseCharacterColorIslandMessage(character1.getStudents().get(0).getColor(), 1));
        Assertions.assertEquals(2, result.get(name1).size());
        Assertions.assertEquals("You don't have enough coins to activate this character ability", ((ErrorMessage) result.get(name1).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name1).get(1).getMessageId());

        player1.addCoin();

        result = controller.handleMessage(name1, new UseCharacterColorIslandMessage(character1.getStudents().get(0).getColor(), 15));
        Assertions.assertEquals(2, result.get(name1).size());
        Assertions.assertEquals("Island number must be between 1 and " + controller.getMatch().getIslands().size(), ((ErrorMessage) result.get(name1).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name1).get(1).getMessageId());

        PawnColor color = null;
        for (PawnColor c : PawnColor.values()) {
            if (character1.getStudentsColorCount(c) == 0) {
                color = c;
                break;
            }
        }
        result = controller.handleMessage(name1, new UseCharacterColorIslandMessage(color, 1));
        Assertions.assertEquals(2, result.get(name1).size());
        Assertions.assertEquals("There are no students with color " + color.name() + " on this character", ((ErrorMessage) result.get(name1).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name1).get(1).getMessageId());

        PawnColor color1 = character1.getStudents().get(0).getColor();
        controller.getNextMessage().put(name1, new AskCloudMessage());
        result = controller.handleMessage(name1, new UseCharacterColorIslandMessage(color1, 1));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_CLOUD, result.get(name1).get(0).getMessageId());
        Assertions.assertEquals(4, character1.getStudents().size());
        Assertions.assertEquals(color1, controller.getMatch().getIslands().get(1).getStudents().get(controller.getMatch().getIslands().get(1).getStudents().size() - 1).getColor());
    }

    @Test
    public synchronized void lastMessageCharacterColorIslandMessage() throws IllegalMoveException {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        String name2 = controller.getMatch().getPlayersOrder().get(1).getName();

        controller.getMatch().getCharacters().set(0, new Character1());
        Character1 character1 = (Character1)controller.getMatch().getCharacters().get(0);
        character1.setup(controller.getMatch());
        PawnColor color1 = character1.getStudents().get(0).getColor();

        Map<String, List<Message>> result = controller.handleMessage(name1, new SetCloudMessage(1));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name1).get(0).getMessageId());
        Assertions.assertEquals(0, controller.getMatch().getClouds().get(1).getStudents().size());

        result = controller.handleMessage(name1, new UseCharacterColorIslandMessage(color1, 1));
        Assertions.assertEquals(1, result.get(name2).size());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name2).get(0).getMessageId());
        Assertions.assertEquals(4, character1.getStudents().size());
        Assertions.assertEquals(color1, controller.getMatch().getIslands().get(1).getStudents().get(controller.getMatch().getIslands().get(1).getStudents().size() - 1).getColor());
    }

    @Test
    public synchronized void characterColorMessage() throws IllegalMoveException {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        Player player1 = controller.getMatch().getPlayerFromName(name1);
        controller.getMatch().getCharacters().set(0, new Character9());

        Map<String, List<Message>> result = controller.handleMessage(name1, new UseCharacterColorMessage(8, PawnColor.RED));
        Assertions.assertEquals(2, result.get(name1).size());
        Assertions.assertEquals("You don't have enough coins to activate this character ability", ((ErrorMessage) result.get(name1).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name1).get(1).getMessageId());

        player1.addCoin();
        player1.addCoin();

        controller.getNextMessage().put(name1, new AskCloudMessage());
        result = controller.handleMessage(name1, new UseCharacterColorMessage(8, PawnColor.RED));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_CLOUD, result.get(name1).get(0).getMessageId());
        Assertions.assertEquals(PawnColor.RED, controller.getMatch().getInfluencePolicy().getExcludedColor());
    }

    @Test
    public synchronized void lastMessageCharacterColorMessage() throws IllegalMoveException {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        Player player1 = controller.getMatch().getPlayerFromName(name1);
        String name2 = controller.getMatch().getPlayersOrder().get(1).getName();
        controller.getMatch().getCharacters().set(0, new Character9());
        player1.addCoin();
        player1.addCoin();

        Map<String, List<Message>> result = controller.handleMessage(name1, new SetCloudMessage(1));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name1).get(0).getMessageId());
        Assertions.assertEquals(0, controller.getMatch().getClouds().get(1).getStudents().size());

        result = controller.handleMessage(name1, new UseCharacterColorMessage(8, PawnColor.RED));
        Assertions.assertEquals(1, result.get(name2).size());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name2).get(0).getMessageId());
    }

    @Test
    public synchronized void lastMessageCharacterMessage() throws IllegalMoveException {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        Player player1 = controller.getMatch().getPlayerFromName(name1);
        String name2 = controller.getMatch().getPlayersOrder().get(1).getName();
        controller.getMatch().getCharacters().set(0, new Character2());

        Map<String, List<Message>> result = controller.handleMessage(name1, new UseCharacterMessage(1));
        Assertions.assertEquals(2, result.get(name1).size());
        Assertions.assertEquals("You don't have enough coins to activate this character ability", ((ErrorMessage) result.get(name1).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name1).get(1).getMessageId());

        player1.addCoin();

        result = controller.handleMessage(name1, new SetCloudMessage(1));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name1).get(0).getMessageId());
        Assertions.assertEquals(0, controller.getMatch().getClouds().get(1).getStudents().size());

        result = controller.handleMessage(name1, new UseCharacterMessage(1));
        Assertions.assertEquals(1, result.get(name2).size());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name2).get(0).getMessageId());
    }

    @Test
    public synchronized void characterIslandMessage() throws IllegalMoveException {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        Player player1 = controller.getMatch().getPlayerFromName(name1);
        controller.getMatch().getCharacters().set(0, new Character5());

        Map<String, List<Message>> result = controller.handleMessage(name1, new UseCharacterIslandMessage(4, 15));
        Assertions.assertEquals(2, result.get(name1).size());
        Assertions.assertEquals("Island must be between 1 and " + controller.getMatch().getIslands().size(), ((ErrorMessage) result.get(name1).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name1).get(1).getMessageId());

        result = controller.handleMessage(name1, new UseCharacterIslandMessage(4, 1));
        Assertions.assertEquals(2, result.get(name1).size());
        Assertions.assertEquals("You don't have enough coins to activate this character ability", ((ErrorMessage) result.get(name1).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name1).get(1).getMessageId());

        player1.addCoin();

        controller.getNextMessage().put(name1, new AskCloudMessage());
        result = controller.handleMessage(name1, new UseCharacterIslandMessage(4, 1));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_CLOUD, result.get(name1).get(0).getMessageId());
        Assertions.assertEquals(1, controller.getMatch().getIslands().get(1).getNoEntry());
    }

    @Test
    public synchronized void endGameCharacterIslandMessage() throws IllegalMoveException {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        Player player1 = controller.getMatch().getPlayerFromName(name1);
        Team team1 = controller.getMatch().getTeamFromPlayer(player1);
        controller.getMatch().getCharacters().set(0, new Character3());
        player1.addCoin();
        player1.addCoin();
        int pos = controller.getMatch().getPosMotherNature();

        team1.getTowers().clear();
        team1.addTower(new Tower(team1.getTowerColor()));

        Assertions.assertEquals(1, team1.getTowers().size());

        PawnColor color = player1.getSchool().getEntrance().get(0).getColor();
        controller.getMatch().playerMoveStudent(name1, color);
        Assertions.assertTrue(player1.getSchool().isColoredProfessor(color));
        controller.getMatch().getIslands().get(1).addStudent(new Student(color));

        Map<String, List<Message>> result = controller.handleMessage(name1, new UseCharacterIslandMessage(2, 1));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.END_GAME, result.get(name1).get(0).getMessageId());
        Assertions.assertEquals(player1, ((EndGameMessage)result.get(name1).get(0)).getWinner().getPlayers().get(0));
        Assertions.assertEquals(pos, controller.getMatch().getPosMotherNature());
    }

    @Test
    public synchronized void lastMessageCharacterIslandMessage() throws IllegalMoveException {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        Player player1 = controller.getMatch().getPlayerFromName(name1);
        String name2 = controller.getMatch().getPlayersOrder().get(1).getName();
        controller.getMatch().getCharacters().set(0, new Character5());
        player1.addCoin();

        Map<String, List<Message>> result = controller.handleMessage(name1, new SetCloudMessage(1));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name1).get(0).getMessageId());
        Assertions.assertEquals(0, controller.getMatch().getClouds().get(1).getStudents().size());

        result = controller.handleMessage(name1, new UseCharacterIslandMessage(4, 1));
        Assertions.assertEquals(1, result.get(name2).size());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name2).get(0).getMessageId());
        Assertions.assertEquals(1, controller.getMatch().getIslands().get(1).getNoEntry());
    }


    @AfterAll
    public static void closeServer(){
        server.close();
    }
}
