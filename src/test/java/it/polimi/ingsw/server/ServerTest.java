package it.polimi.ingsw.server;

import it.polimi.ingsw.app.ServerApp;
import it.polimi.ingsw.client.ClientCLI;
import it.polimi.ingsw.model.IllegalMoveException;
import it.polimi.ingsw.protocol.message.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.io.*;

public class ServerTest extends TestCase {
    @Test
    public void connectionCLITest() throws IOException {
        Server server = new Server();
        Thread t = new Thread(() -> {
            try {
                server.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();

        System.setIn(new ByteArrayInputStream("test\nt\n1\n2\ny\nyes\n5\n".getBytes()));
        ClientCLI client1 = new ClientCLI("127.0.0.1", 61863);


        client1.handleLobbyMessage(new AskUsernameMessage());
        assertEquals("test", client1.getName());
        client1.handleLobbyMessage(new AskPlayerNumberMessage());

        client1.handleLobbyMessage(new AskExpertMessage());

        assertEquals(1, server.getWaitingConnections().size());

        System.setIn(new ByteArrayInputStream("test1\n4\n".getBytes()));
        ClientCLI client2 = new ClientCLI("127.0.0.1", 61863);
        client2.handleLobbyMessage(new AskUsernameMessage());

        assertEquals("test1", client2.getName());
        assertEquals(2, server.getWaitingConnections().size());


        client1.handleGameMessage(new AskAssistantMessage());

        client2.handleGameMessage(new AskAssistantMessage());


        assertEquals(2, server.getWaitingConnections().size());

        //server.getControllers().get(0).getMatch().updateView(server.getConnectionsFromController(server.getControllers().get(0)));

        /*System.setIn(new ByteArrayInputStream("3\n".getBytes()));
        client2.handleGameMessage(new AskAssistantMessage());*/

        server.close();
    }

}
