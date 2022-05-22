package it.polimi.ingsw.app;

import it.polimi.ingsw.server.Server;

import java.io.IOException;

public class ServerApp {
    private static Server server;

    public static void main( String[] args ) {
        try {
            server = new Server();
            server.run();
        } catch(IOException e){
            System.err.println("Impossible to start the server!\n" + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Concurrency system error");
        }
    }

    public static Server getServer() {
        return server;
    }
}
