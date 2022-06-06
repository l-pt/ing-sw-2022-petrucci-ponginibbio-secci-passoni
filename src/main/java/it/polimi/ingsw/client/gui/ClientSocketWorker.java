package it.polimi.ingsw.client.gui;

import com.google.gson.JsonSyntaxException;
import it.polimi.ingsw.protocol.Message;

import javax.swing.*;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

public class ClientSocketWorker extends SwingWorker<Void, Message> {
    private ClientGUI client;
    private boolean running;

    public ClientSocketWorker(ClientGUI client) {
        this.client = client;
        running = true;
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    protected Void doInBackground() {
        while (isRunning()) {
            Message msg;
            //Wait for a server message
            try {
                msg = client.readMessage();
            } catch (SocketTimeoutException e) {
                continue;
            } catch (JsonSyntaxException | IOException e) {
                msg = null;
            }
            publish(msg);
        }
        return null;
    }

    @Override
    protected void process(List<Message> chunks) {
        for (Message msg : chunks) {
            client.processMessage(msg);
        }
    }
}
