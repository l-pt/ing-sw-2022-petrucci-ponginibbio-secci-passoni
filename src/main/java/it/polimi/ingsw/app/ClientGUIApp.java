package it.polimi.ingsw.app;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.gui.ClientGUI;

import java.io.IOException;

public class ClientGUIApp {
    public static void main(String[] args){
        Client client = new ClientGUI();
        try{
            client.run();
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
}
