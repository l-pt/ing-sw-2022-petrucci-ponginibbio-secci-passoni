package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;
import it.polimi.ingsw.protocol.message.*;
import it.polimi.ingsw.protocol.message.character.UseNoCharacterMessage;
import it.polimi.ingsw.server.Server;
import junit.framework.TestCase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControllerTest extends TestCase {
    private static Server server;
    static {
        try {
            server = new Server();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*@Test
    public synchronized void setup4PlayersController() throws IOException {
        Server server = new Server();
        server.setMatchParameters(4, true);
        List<String> names = List.of("test1", "test2", "test3", "test4");
        Controller controller = new Controller(server, names);
        server.close();
    }*/

    @Test
    public synchronized void assistantMessage() throws IllegalMoveException {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        String name2 = controller.getMatch().getPlayersOrder().get(1).getName();

        Map<String, List<Message>> result = controller.handleMessage(name1, new SetAssistantMessage(15));
        Assertions.assertEquals(2, result.get(name1).size());
        Assertions.assertEquals("The value must be between 1 and 10", ((ErrorMessage)result.get(name1).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_ASSISTANT, result.get(name1).get(1).getMessageId());
        Assertions.assertNull(controller.getMatch().getPlayerFromName(name1).getCurrentAssistant());

        result = controller.handleMessage(name1, new SetAssistantMessage(2));
        Assertions.assertEquals(1, result.get(name2).size());
        Assertions.assertEquals(MessageId.ASK_ASSISTANT, result.get(name2).get(0).getMessageId());
        Assertions.assertEquals(2, controller.getMatch().getPlayerFromName(name1).getCurrentAssistant().getValue());

        result = controller.handleMessage(name2, new SetAssistantMessage(2));
        Assertions.assertEquals(2, result.get(name2).size());
        Assertions.assertEquals("You can't use this assistant because another player already used it this turn", ((ErrorMessage)result.get(name2).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_ASSISTANT, result.get(name2).get(1).getMessageId());
        Assertions.assertNull(controller.getMatch().getPlayerFromName(name2).getCurrentAssistant());

        result = controller.handleMessage(name2, new SetAssistantMessage(5));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name1).get(0).getMessageId());
        Assertions.assertEquals(5, controller.getMatch().getPlayerFromName(name2).getCurrentAssistant().getValue());

        result = controller.handleMessage(name1, new UseNoCharacterMessage());
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_ENTRANCE_STUDENT, result.get(name1).get(0).getMessageId());

        result = controller.handleMessage(name1, new SetAssistantMessage(2));
        Assertions.assertEquals(2, result.get(name1).size());
        Assertions.assertEquals("You don't have an assistant with value 2", ((ErrorMessage)result.get(name1).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_ASSISTANT, result.get(name1).get(1).getMessageId());
    }

    @Test
    public synchronized void entranceStudentMessageWithoutCharacter() throws IllegalMoveException {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();

        Map<Integer, Map<PawnColor, Integer>> islandStudents = new HashMap<>();
        Map<PawnColor, Integer> tableStudents = new HashMap<>();
        PawnColor color1 = controller.getMatch().getPlayerFromName(name1).getSchool().getEntrance().get(0).getColor();
        PawnColor color2 = controller.getMatch().getPlayerFromName(name1).getSchool().getEntrance().get(1).getColor();
        PawnColor color3 = controller.getMatch().getPlayerFromName(name1).getSchool().getEntrance().get(2).getColor();

        islandStudents.put(3, new HashMap<>());
        islandStudents.get(3).put(color1, islandStudents.get(3).getOrDefault(color1, 0) + 1);
        islandStudents.put(10, new HashMap<>());
        islandStudents.get(10).put(color2, islandStudents.get(10).getOrDefault(color2, 0) + 1);
        tableStudents.put(color3, tableStudents.getOrDefault(color3, 0) + 1);

        Map<String, List<Message>> result = controller.handleMessage(name1, new SetEntranceStudentMessage(islandStudents, tableStudents));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name1).get(0).getMessageId());
        Assertions.assertEquals(4, controller.getMatch().getPlayerFromName(name1).getSchool().getEntrance().size());
        Assertions.assertEquals(color1, controller.getMatch().getIslands().get(3).getStudents().get(controller.getMatch().getIslands().get(3).getStudents().size() - 1).getColor());
        Assertions.assertEquals(color2, controller.getMatch().getIslands().get(10).getStudents().get(controller.getMatch().getIslands().get(10).getStudents().size() - 1).getColor());
        Assertions.assertEquals(1, controller.getMatch().getPlayerFromName(name1).getSchool().getTableCount(color3));
    }

    /*@Test
    public synchronized void entranceStudentMessageWithCharacter() throws IllegalMoveException {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        String name2 = controller.getMatch().getPlayersOrder().get(1).getName();

        controller.getMatch().getPlayerFromName(name1).addCoin();
        controller.getMatch().getCharacters().set(0, new Character2());

        Map<String, List<Message>> result = controller.handleMessage(name1, new UseCharacterMessage(1));
        Assertions.assertEquals(2, result.get(name1).size());

        Map<Integer, Map<PawnColor, Integer>> islandStudents = new HashMap<>();
        Map<PawnColor, Integer> tableStudents = new HashMap<>();
        PawnColor color1 = controller.getMatch().getPlayerFromName(name1).getSchool().getEntrance().get(0).getColor();
        PawnColor color2 = controller.getMatch().getPlayerFromName(name1).getSchool().getEntrance().get(1).getColor();
        PawnColor color3 = controller.getMatch().getPlayerFromName(name1).getSchool().getEntrance().get(2).getColor();

        islandStudents.put(3, new HashMap<>());
        islandStudents.get(3).put(color1, islandStudents.get(3).getOrDefault(color1, 0) + 1);
        islandStudents.put(10, new HashMap<>());
        islandStudents.get(10).put(color2, islandStudents.get(10).getOrDefault(color2, 0) + 1);
        tableStudents.put(color3, tableStudents.getOrDefault(color3, 0) + 1);

        result = controller.handleMessage(name1, new SetEntranceStudentMessage(islandStudents, tableStudents));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_MOTHER_NATURE, result.get(name1).get(0).getMessageId());
        Assertions.assertEquals(4, controller.getMatch().getPlayerFromName(name1).getSchool().getEntrance().size());
        Assertions.assertEquals(color1, controller.getMatch().getIslands().get(3).getStudents().get(controller.getMatch().getIslands().get(3).getStudents().size() - 1).getColor());
        Assertions.assertEquals(color2, controller.getMatch().getIslands().get(10).getStudents().get(controller.getMatch().getIslands().get(10).getStudents().size() - 1).getColor());
        Assertions.assertEquals(1, controller.getMatch().getPlayerFromName(name1).getSchool().getTableCount(color3));
    }*/

    @Test
    public synchronized void motherNatureMessageWithoutCharacter() throws IllegalMoveException {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        Player player1 = controller.getMatch().getPlayerFromName(name1);
        player1.setCurrentAssistant(player1.getAssistantFromValue(3));
        int pos = controller.getMatch().getPosMotherNature();

        Map<String, List<Message>> result = controller.handleMessage(name1, new SetMotherNatureMessage(15));
        Assertions.assertEquals(2, result.get(name1).size());
        Assertions.assertEquals("Mother nature moves must be between 1 and " + (player1.getCurrentAssistant().getMoves() + player1.getAdditionalMoves()), ((ErrorMessage)result.get(name1).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_MOTHER_NATURE, result.get(name1).get(1).getMessageId());

        result = controller.handleMessage(name1, new SetMotherNatureMessage(2));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name1).get(0).getMessageId());
        Assertions.assertEquals((pos + player1.getCurrentAssistant().getMoves()) % 12, controller.getMatch().getPosMotherNature());
    }

    @Test
    public synchronized void cloudMessageWithoutCharacter() {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        String name2 = controller.getMatch().getPlayersOrder().get(1).getName();

        Map<String, List<Message>> result = controller.handleMessage(name1, new SetCloudMessage(3));
        Assertions.assertEquals(2, result.get(name1).size());
        Assertions.assertEquals("Island number must be between 1 and " + controller.getMatch().getClouds().size(), ((ErrorMessage)result.get(name1).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_CLOUD, result.get(name1).get(1).getMessageId());

        result = controller.handleMessage(name1, new SetCloudMessage(1));
        Assertions.assertEquals(1, result.get(name1).size());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name1).get(0).getMessageId());
        Assertions.assertEquals(0, controller.getMatch().getClouds().get(1).getStudents().size());

        result = controller.handleMessage(name2, new SetCloudMessage(1));
        Assertions.assertEquals(2, result.get(name2).size());
        Assertions.assertEquals("Cloud already chosen by another player this turn", ((ErrorMessage)result.get(name2).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_CLOUD, result.get(name2).get(1).getMessageId());
    }

    /*@Test
    public synchronized void characterColorIslandMessage() throws IllegalMoveException {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();

        controller.getMatch().getCharacters().set(0, new Character1());
        Character1 character1 = (Character1)controller.getMatch().getCharacters().get(0);
        character1.setup(controller.getMatch());

        controller.getMatch().getPlayerFromName(name1).removeCoins(1);
        Map<String, List<Message>> result = controller.handleMessage(name1, new UseCharacterColorIslandMessage(character1.getStudents().get(0).getColor(), 1));
        Assertions.assertEquals(2, result.get(name1).size());
        Assertions.assertEquals("Insufficient coins", ((ErrorMessage) result.get(name1).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name1).get(1).getMessageId());

        controller.getMatch().getPlayerFromName(name1).addCoin();
        result = controller.handleMessage(name1, new UseCharacterColorIslandMessage(character1.getStudents().get(0).getColor(), 15));
        Assertions.assertEquals(2, result.get(name1).size());
        Assertions.assertEquals("Island number must be between 1 and " + controller.getMatch().getIslands().size(), ((ErrorMessage) result.get(name1).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name1).get(1).getMessageId());

        PawnColor color = null;
        for (PawnColor c : PawnColor.values())
            if(character1.getStudentsColorCount(c) == 0)
                color = c;
        result = controller.handleMessage(name1, new UseCharacterColorIslandMessage(color, 1));
        Assertions.assertEquals(2, result.get(name1).size());
        Assertions.assertEquals("There are no students with color " + color.name() + " on this character", ((ErrorMessage) result.get(name1).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_CHARACTER, result.get(name1).get(1).getMessageId());

        PawnColor color1 = character1.getStudents().get(0).getColor();
        result = controller.handleMessage(name1, new UseCharacterColorIslandMessage(color1, 1));
        Assertions.assertEquals(1, result.get(name1).size());

        Assertions.assertEquals(4, character1.getStudents().size());
        Assertions.assertEquals(color1, controller.getMatch().getIslands().get(1).getStudents().get(controller.getMatch().getIslands().get(1).getStudents().size() - 1).getColor());
    }*/

    @AfterAll
    public static void closeServer(){
        server.close();
    }
}
