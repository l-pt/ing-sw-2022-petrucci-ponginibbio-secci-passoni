package it.polimi.ingsw.view.gui;

import com.google.gson.JsonSyntaxException;
import it.polimi.ingsw.client.ClientGUI;
import it.polimi.ingsw.server.protocol.Message;

import javax.swing.*;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

/**
 * The ClientSocketWorker runs in the background and fetches new messages from the server
 * until it is stopped with {@link ClientSocketWorker#setRunning}.
 * When a new message is available, it calls {@link it.polimi.ingsw.client.ClientGUI#processMessage}
 */
public class ClientSocketWorker extends SwingWorker<Void, Message> {
    private ClientGUI client;
    private boolean running;
    private int delay;

    public ClientSocketWorker(ClientGUI client) {
        this.client = client;
        running = true;
        delay = 0;
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * Wait for new server messages.
     *
     * This method is called automatically by {@link SwingWorker} and is executed on a separate thread.
     */
    @Override
    protected Void doInBackground() {
        while (isRunning()) {
            Message msg;
            //Wait for a server message
            try {
                msg = client.readMessage();
                delay = 0;
            } catch (SocketTimeoutException e) {
                delay += 2;
                //2 min 30 sec timeout
                if (delay >= 150) {
                    msg = null;
                } else {
                    continue;
                }
            } catch (JsonSyntaxException | IOException e) {
                msg = null;
            }
            publish(msg);
        }
        return null;
    }

    /**
     * Notify the client about new messages.
     *
     * This method is called automatically by {@link SwingWorker} and is executed on the Event Dispatcher Thread.
     * @param chunks A list of new messages
     */
    @Override
    protected void process(List<Message> chunks) {
        for (Message msg : chunks) {
            client.processMessage(msg);
        }
    }
}
