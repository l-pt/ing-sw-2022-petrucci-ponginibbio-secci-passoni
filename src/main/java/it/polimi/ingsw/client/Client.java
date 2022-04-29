package it.polimi.ingsw.client;

import com.google.gson.JsonSyntaxException;
import it.polimi.ingsw.protocol.*;
import it.polimi.ingsw.protocol.message.ErrorMessage;
import it.polimi.ingsw.protocol.message.SetExpertMessage;
import it.polimi.ingsw.protocol.message.SetPlayerNumberMessage;
import it.polimi.ingsw.protocol.message.SetUsernameMessage;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public abstract class Client {
    protected String ip;
    protected int port;
    protected Socket socket;
    protected InputStreamReader in;
    protected OutputStreamWriter out;

    public Client(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    /**
     * Send a message to the server
     */
    public void sendMessage(Message msg) throws IOException {
        String json = GsonSingleton.get().toJson(msg);
        out.write(json.length());
        out.write(json, 0, json.length());
        out.flush();
    }

    /**
     * Receive a message from the server
     */
    public Message readMessage() throws JsonSyntaxException, IOException {
        int jsonLen = in.read();
        if (jsonLen == -1) {
            throw new IOException("Connection closed by the server");
        }
        StringBuilder jsonBuilder = new StringBuilder();
        for (int i = 0; i < jsonLen; ++i) {
            jsonBuilder.appendCodePoint(in.read());
        }
        return GsonSingleton.get().fromJson(jsonBuilder.toString(), Message.class);
    }

    /**
     * Receive a message from the server
     */
    public <T extends Message> T readMessage(Class<T> messageClass) throws JsonSyntaxException, IOException {
        return messageClass.cast(readMessage());
    }

    public abstract void run() throws IOException;

    /**
     * Free all resources and call System.exit to close the program
     */
    public void closeProgram() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

}
