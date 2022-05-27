package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.message.SetAssistantMessage;
import it.polimi.ingsw.server.Server;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ControllerTest extends TestCase {
    @Test
    public synchronized void setup2PlayersController() throws IOException {
        Server server = new Server();
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);
        server.close();
    }

    @Test
    public synchronized void setup3PlayersController() throws IOException {
        Server server = new Server();
        server.setMatchParameters(3, true);
        List<String> names = List.of("test1", "test2", "test3");
        Controller controller = new Controller(server, names);
        server.close();
    }

    @Test
    public synchronized void setup4PlayersController() throws IOException {
        Server server = new Server();
        server.setMatchParameters(4, true);
        List<String> names = List.of("test1", "test2", "test3", "test4");
        Controller controller = new Controller(server, names);
        server.close();
    }

    /*@Test
    public synchronized void setAssistantMessage() throws IOException {
        Server server = new Server();
        server.setMatchParameters(2, true);
        List<String> names = List.of("test1", "test2");
        Controller controller = new Controller(server, names);

        Exception e = assertThrows(IllegalMoveException.class, () -> controller.handleMessage(controller.getMatch().getPlayersOrder().get(0).getName(), new SetAssistantMessage(15)));
        Assertions.assertEquals("The value must be between 1 and 10", e.getMessage());

        server.close();
    }*/
}
