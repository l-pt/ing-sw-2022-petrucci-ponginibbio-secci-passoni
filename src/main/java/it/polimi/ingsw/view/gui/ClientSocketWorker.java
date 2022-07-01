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
 * until it is stopped with {@link ClientSocketWorker#stop}.
 * When a new message is available, it calls {@link it.polimi.ingsw.client.ClientGUI#processMessage}
 */
public class ClientSocketWorker extends SwingWorker<Void, Message> {
    private ClientGUI client;
    private boolean running;
    /**
     * Number of seconds since last message from the server
     */
    private int delay;

    /**
     * Constructor
     * @param client The client to send new messages to
     */
    public ClientSocketWorker(ClientGUI client) {
        this.client = client;
        running = true;
        delay = 0;
    }

    /**
     * isRunning()
     * @return True if the worker has not been stopped yet, false otherwise
     */
    public synchronized boolean isRunning() {
        return running;
    }

    /**
     * Stop the worker
     */
    public synchronized void stop() {
        this.running = false;
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
                //2 min timeout
                if (delay >= 120) {
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
