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
     * @return the username of the client
     */
    public String getName() {
        return name;
    }

    /**
     * Send a message to the server
     * @param msg is the message to send
     * @throws IOException
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
     * @return the message sent by the server
     * @throws JsonSyntaxException
     * @throws IOException
     */
    public Message readMessage() throws JsonSyntaxException, IOException {
        String json = in.readUTF();
        return GsonSingleton.get().fromJson(json, Message.class);
    }

    /**
     * Receive a message from the server
     * @param messageClass
     * @param <T>
     * @return
     * @throws JsonSyntaxException
     * @throws IOException
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
