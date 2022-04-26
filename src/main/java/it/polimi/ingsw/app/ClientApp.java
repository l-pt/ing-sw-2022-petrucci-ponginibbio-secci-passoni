package it.polimi.ingsw.app;

import it.polimi.ingsw.client.Client;

import java.io.IOException;

public class ClientApp
{
    public static void main(String[] args){
        Client client;
        try{
            client = new Client("127.0.0.1", 61863);
            client.run();
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
}