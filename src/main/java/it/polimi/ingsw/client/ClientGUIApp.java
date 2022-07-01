package it.polimi.ingsw.client;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.ClientGUI;

import java.io.IOException;

public class ClientGUIApp {
    /**
     * The main process of the client from graphic user interface
     * @param args
     */
    public static void main(String[] args){
        Client client = new ClientGUI();
        try{
            client.run();
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
}
