package it.polimi.ingsw.app;

import it.polimi.ingsw.server.Server;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ServerAppTest extends TestCase {
    /*@Test
    public synchronized void server(){
        Thread t = new Thread(() -> {
            ServerApp.main(new String[0]);
        });
        t.start();
        while (ServerApp.getServer() == null);
        ServerApp.getServer().close();
    }*/
}
