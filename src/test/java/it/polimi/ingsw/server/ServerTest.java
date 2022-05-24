package it.polimi.ingsw.server;

import it.polimi.ingsw.client.ClientCLI;
import it.polimi.ingsw.model.IllegalMoveException;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ServerTest extends TestCase {
    @Test
    public void setupMatchParameters() throws IOException, IllegalMoveException {
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

        System.setIn(new ByteArrayInputStream("test\nt\n1\n2\ny\nyes\n2\n".getBytes()));
        ClientCLI client1 = new ClientCLI("127.0.0.1", 61863);
        Thread t1 = new Thread(() -> {
            try {
                client1.run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t1.start();

        while (server.getMatchParameters() == null);
        t1.interrupt();

        assertEquals("test", client1.getName());
        assertEquals(2, server.getMatchParameters().getPlayerNumber());
        assertEquals(true, server.getMatchParameters().isExpert());
        assertEquals(1, server.getWaitingConnections().size());

        System.setIn(new ByteArrayInputStream("test\ntest1\n2\n4\n".getBytes()));
        ClientCLI client2 = new ClientCLI("127.0.0.1", 61863);
        Thread t2 = new Thread(() -> {
            try {
                client2.run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t2.start();

        while (server.getControllers().size() == 0);
        t1.interrupt();
        assertEquals("test1", client2.getName());
        assertEquals(0, server.getWaitingConnections().size());
        assertEquals(1, server.getControllers().size());

        while (server.getControllers().get(0).getMatch().getPlayersOrder().get(0).getCurrentAssistant() == null ||
                server.getControllers().get(0).getMatch().getPlayersOrder().get(1).getCurrentAssistant() == null);
        assertEquals(2, server.getControllers().get(0).getMatch().getPlayerFromName("test").getCurrentAssistant().getValue());
        assertEquals(4, server.getControllers().get(0).getMatch().getPlayerFromName("test1").getCurrentAssistant().getValue());
        assertEquals("test", server.getControllers().get(0).getMatch().getPlayersOrder().get(0).getName());
        assertEquals("test1", server.getControllers().get(0).getMatch().getPlayersOrder().get(1).getName());

        server.close();
    }
}
