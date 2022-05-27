package it.polimi.ingsw.app;

import it.polimi.ingsw.server.Server;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ClientCLIAppTest extends TestCase {
    /*@Test
    public synchronized void ClientCLI() throws IOException {
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

        System.setIn(new ByteArrayInputStream("127.0\n127.0.0.1".getBytes()));
        Thread t1 = new Thread(() -> {
            ClientCLIApp.main(new String[0]);
        });
        t1.start();

        server.close();
    }*/

}
