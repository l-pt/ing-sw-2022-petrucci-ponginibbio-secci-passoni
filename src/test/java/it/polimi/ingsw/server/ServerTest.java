package it.polimi.ingsw.server;

import it.polimi.ingsw.server.Server.MatchParameters;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ServerTest extends TestCase {
    @Test
    public void setupMatchParameters() throws IOException {
        Server server = new Server();

        assertNull(server.getMatchParameters());

        server.setMatchParameters(3, true);
        assertEquals(3, server.getMatchParameters().getPlayerNumber());
        assertTrue(server.getMatchParameters().isExpert());

        server.setMatchParameters(new MatchParameters(2, false));
        assertEquals(2, server.getMatchParameters().getPlayerNumber());
        assertFalse(server.getMatchParameters().isExpert());
    }
    @Test
    public void connectionsTest() throws IOException {
        Server server = new Server();

        assertEquals(0, server.getWaitingConnections().size());
        assertNull(server.getFirstConnection());
    }

}
