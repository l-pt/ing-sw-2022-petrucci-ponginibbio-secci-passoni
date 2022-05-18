package it.polimi.ingsw.client.gui;

import com.google.gson.JsonSyntaxException;
import it.polimi.ingsw.protocol.Message;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

public class ClientSocketWorker extends SwingWorker<Void, Message> {
    ClientGUI client;

    public ClientSocketWorker(ClientGUI client) {
        this.client = client;
    }

    @Override
    protected Void doInBackground() throws IOException {
        while (true) {
            Message msg;
            //Wait for a server message
            try {
                msg = client.readMessage();
            } catch (JsonSyntaxException e) {
                System.out.println("Server sent invalid message");
                return null;
            }
            publish(msg);
        }
    }

    @Override
    protected void process(List<Message> chunks) {
        for (Message msg : chunks) {
            client.processMessage(msg);
        }
    }
}
