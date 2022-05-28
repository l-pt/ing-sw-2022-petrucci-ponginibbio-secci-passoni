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
import java.util.ArrayList;
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
        Assertions.assertEquals("Cannot play this assistant", ((ErrorMessage)result.get(name2).get(0)).getError());
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
    public synchronized void entranceStudentMessage() {
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        String name1 = controller.getMatch().getPlayersOrder().get(0).getName();
        String name2 = controller.getMatch().getPlayersOrder().get(1).getName();

        Map<Integer, Map<PawnColor, Integer>> islandStudents = new HashMap<>();
        Map<PawnColor, Integer> tableStudents = new HashMap<>();

        Map<String, List<Message>> result = controller.handleMessage(name1, new SetEntranceStudentMessage(islandStudents, tableStudents));
        Assertions.assertEquals(2, result.get(name1).size());
        Assertions.assertEquals("You have to move exactly three students from the entrance", ((ErrorMessage)result.get(name1).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_ENTRANCE_STUDENT, result.get(name1).get(1).getMessageId());

        islandStudents.put(13, new HashMap<>());
        islandStudents.get(13).put(PawnColor.RED, islandStudents.get(13).getOrDefault(PawnColor.RED, 0) + 1);
        islandStudents.put(10, new HashMap<>());
        islandStudents.get(10).put(PawnColor.RED, islandStudents.get(13).getOrDefault(PawnColor.PINK, 0) + 1);
        tableStudents.put(PawnColor.BLUE, tableStudents.getOrDefault(PawnColor.PINK, 0) + 1);

        result = controller.handleMessage(name1, new SetEntranceStudentMessage(islandStudents, tableStudents));
        Assertions.assertEquals(2, result.get(name1).size());
        Assertions.assertEquals("Island 13 does not exist", ((ErrorMessage)result.get(name1).get(0)).getError());
        Assertions.assertEquals(MessageId.ASK_ENTRANCE_STUDENT, result.get(name1).get(1).getMessageId());



    }

    @AfterAll
    public static void closeServer(){
        server.close();
    }
}
