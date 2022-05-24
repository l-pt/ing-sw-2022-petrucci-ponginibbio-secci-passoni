package it.polimi.ingsw.server;

import com.google.gson.*;
import it.polimi.ingsw.observer.Observer;
import it.polimi.ingsw.protocol.*;
import it.polimi.ingsw.protocol.message.*;

import java.io.*;
import java.net.Socket;

public class Connection implements Runnable, Observer<UpdateViewMessage> {
    private final Socket socket;
    private final InputStreamReader in;
    private final OutputStreamWriter out;
    private final Server server;
    private String name;
    private boolean isActive;
    private boolean setPlayersAndExpert;

    public Connection(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        in = new InputStreamReader(socket.getInputStream());
        out = new OutputStreamWriter(socket.getOutputStream());
        isActive = true;
        setPlayersAndExpert = false;
    }

    /**
     * @return String the player's name, or null if the name is yet to be entered
     */
    public synchronized String getName() {
        return name;
    }

    private synchronized boolean isActive(){
        return this.isActive;
    }

    /**
     * Send a message to the client
     */
    public void sendMessage(Message msg) throws IOException {
        String json = GsonSingleton.get().toJson(msg);
        synchronized (out) {
            out.write(json.length());
            out.write(json, 0, json.length());
            out.flush();
        }
    }

    /**
     * Receive a message from the client
     */
    private Message readMessage() throws JsonSyntaxException, IOException {
        int jsonLen = in.read();
        if (jsonLen == -1) {
            throw new IOException("Connection closed by the client");
        }
        StringBuilder jsonBuilder = new StringBuilder();
        for (int i = 0; i < jsonLen; ++i) {
            jsonBuilder.appendCodePoint(in.read());
        }
        return GsonSingleton.get().fromJson(jsonBuilder.toString(), Message.class);
    }

    /**
     * Receive a message from the client
     */
    private <T extends Message> T readMessage(Class<T> messageClass) throws JsonSyntaxException, IOException {
        return messageClass.cast(readMessage());
    }

    private void closeConnection(){
        //attempt to close connection via socket object method
        try{
            this.socket.close();
        }
        catch(IOException e){
            System.err.println(e.getMessage());
        }

        //update active status
        this.isActive = false;
    }

    public synchronized void close(){
        if (setPlayersAndExpert) {
            server.setMatchParameters(null);
        }
        //close the connection
        closeConnection();
    }

    @Override
    public void run(){
        try{
            //get name of connected user
            SetUsernameMessage usernameMessage = null;
            while (usernameMessage == null) {
                sendMessage(new AskUsernameMessage());
                try {
                    usernameMessage = readMessage(SetUsernameMessage.class);
                    if (server.nameUsed(usernameMessage.getUsername())) {
                        sendMessage(new ErrorMessage("Username already taken"));
                        usernameMessage = null;
                    }
                } catch (JsonSyntaxException e) {
                    sendMessage(new ErrorMessage("Error reading username"));
                }
            }
            name = usernameMessage.getUsername();

            synchronized (server) {
                if (server.getFirstConnection() == this) {
                    //Ask number of players in the match
                    SetPlayerNumberMessage playerNumberMessage = null;
                    while (playerNumberMessage == null) {
                        sendMessage(new AskPlayerNumberMessage());
                        try {
                            playerNumberMessage = readMessage(SetPlayerNumberMessage.class);
                            if (playerNumberMessage.getPlayersNumber() < 2 || playerNumberMessage.getPlayersNumber() > 4) {
                                sendMessage(new ErrorMessage("Game size must be between 2 and 4. Choose game size: "));
                                playerNumberMessage = null;
                            }
                        } catch (JsonSyntaxException e) {
                            sendMessage(new ErrorMessage("Error reading player number"));
                        }
                    }

                    //Ask whether to activate expert mode
                    SetExpertMessage expertMessage = null;
                    while (expertMessage == null) {
                        sendMessage(new AskExpertMessage());
                        try {
                            expertMessage = readMessage(SetExpertMessage.class);
                        } catch (JsonSyntaxException e) {
                            sendMessage(new ErrorMessage("Error reading expert message"));
                        }
                    }

                    server.setMatchParameters(playerNumberMessage.getPlayersNumber(), expertMessage.getExpert());
                    setPlayersAndExpert = true;
                }
            }
            server.checkWaitingConnections();

            while(this.isActive()){
                //Wait for messages from the client
                Message read = readMessage();
                //When a new message arrives, put it in the server's messageQueue
                server.notifyMessage(this, read);
            }
        }
        catch(IOException e){
            System.err.println(e.getMessage());
        }
        finally{
            //Notify the server with a null message to signal that the connection is broken
            server.notifyMessage(this, null);
        }
    }

    @Override
    public void notifyObserver(UpdateViewMessage msg) {
        try {
            sendMessage(msg);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            server.notifyMessage(this, null);
        }
    }
}
