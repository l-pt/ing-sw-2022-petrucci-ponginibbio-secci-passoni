package it.polimi.ingsw.server;

import java.io.IOException;

public class ServerApp {
    /**
     * Main Method of ServerApp
     * @param args Terminal argument
     */
    public static void main(String[] args) {
        try {
            //Sets 61863 if the server port isn't specified in args
            new Server(args.length == 0 ? 61863 : Integer.parseInt(args[0])).run();
        } catch(IOException e){
            System.err.println("Impossible to start the server!\n" + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Concurrency system error");
        }
    }
}
