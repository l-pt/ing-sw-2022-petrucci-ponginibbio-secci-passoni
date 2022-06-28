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

    public String getName() {
        return name;
    }

    /**
     * Send a message to the server
     */
    public void sendMessage(Message msg) throws IOException {
        String json = GsonSingleton.get().toJson(msg);
        synchronized (out) {
            out.writeUTF(json);
            out.flush();
        }
    }

    /**
     * Receive a message from the server
     */
    public Message readMessage() throws JsonSyntaxException, IOException {
        String json = in.readUTF();
        return GsonSingleton.get().fromJson(json, Message.class);
    }

    /**
     * Receive a message from the server
     */
    public <T extends Message> T readMessage(Class<T> messageClass) throws JsonSyntaxException, IOException {
        return messageClass.cast(readMessage());
    }

    public abstract void run() throws IOException;

    /**
     * Free all resources and call System.exit() to close the program
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
