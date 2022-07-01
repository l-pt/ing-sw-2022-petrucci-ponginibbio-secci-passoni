package it.polimi.ingsw.client;

import com.google.gson.JsonSyntaxException;
import it.polimi.ingsw.server.protocol.GsonSingleton;
import it.polimi.ingsw.server.protocol.Message;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Client {
    protected String name;
    protected String ip;
    protected int port;
    protected Socket socket;
    protected DataInputStream in;
    protected DataOutputStream out;
    protected ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * getName()
     * @return The username of the client
     */
    public String getName() {
        return name;
    }

    /**
     * Sends a message to the server
     * @param msg The message to send
     * @throws IOException If there are failed or interrupted I/O operations
     */
    public void sendMessage(Message msg) throws IOException {
        String json = GsonSingleton.get().toJson(msg);
        synchronized (out) {
            out.writeUTF(json);
            out.flush();
        }
    }

    /**
     * Receives a message from the server
     * @return The message sent by the server
     * @throws JsonSyntaxException If there are json syntax errors in the message
     * @throws IOException If there are failed or interrupted I/O operations
     */
    public Message readMessage() throws JsonSyntaxException, IOException {
        String json = in.readUTF();
        return GsonSingleton.get().fromJson(json, Message.class);
    }

    /**
     * Main client loop that processes all messages received
     * @throws IOException
     */
    public abstract void run() throws IOException;

    /**
     * Frees all resources and calls System.exit() to close the program
     */
    public void closeProgram() {
        try {
            executorService.shutdown();
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
