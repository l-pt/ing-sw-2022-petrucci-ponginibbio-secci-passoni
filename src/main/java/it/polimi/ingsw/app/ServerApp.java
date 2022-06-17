package it.polimi.ingsw.app;

import it.polimi.ingsw.server.Server;

import java.io.IOException;

public class ServerApp {
    /**
     *
     * @param args
     */
    public static void main( String[] args ) {
        Server server;
        try {
            if (args.length == 0) {
                server = new Server(61863);
            }else {
                server = new Server(Integer.parseInt(args[0]));
            }
            server.run();
        } catch(IOException e){
            System.err.println("Impossible to start the server!\n" + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Concurrency system error");
        }
    }
}
