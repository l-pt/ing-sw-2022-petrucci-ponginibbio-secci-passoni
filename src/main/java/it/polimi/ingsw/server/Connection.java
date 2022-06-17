package it.polimi.ingsw.server;

import com.google.gson.*;
import it.polimi.ingsw.model.IllegalMoveException;
import it.polimi.ingsw.observer.Observer;
import it.polimi.ingsw.protocol.*;
import it.polimi.ingsw.protocol.message.*;

import java.io.*;
import java.net.Socket;

public class Connection implements Runnable, Observer<UpdateViewMessage> {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Server server;
    private String name;
    private boolean isActive;
    private boolean setPlayersAndExpert;

    public Connection(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        isActive = true;
        setPlayersAndExpert = false;
    }

    /**
     *
     * @return String the player's name, or null if the name is yet to be entered
     */
    public synchronized String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    private synchronized boolean isActive(){
        return this.isActive;
    }

    /**
     *
     * @param msg
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
     *
     * @return
     * @throws JsonSyntaxException
     * @throws IOException
     */
    private Message readMessage() throws JsonSyntaxException, IOException {
        String json = in.readUTF();
        return GsonSingleton.get().fromJson(json, Message.class);
    }

    /**
     *
     * @param messageClass
     * @param <T>
     * @return
     * @throws JsonSyntaxException
     * @throws IOException
     */
    private <T extends Message> T readMessage(Class<T> messageClass) throws JsonSyntaxException, IOException {
        return messageClass.cast(readMessage());
    }

    /**
     *
     */
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

    /**
     *
     */
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
                                sendMessage(new ErrorMessage("The players number must be 2, 3 or 4"));
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
            if(server.getWaitingConnections().size() >= server.getMatchParameters().getPlayerNumber()) {
                server.checkWaitingConnections();
            }else {
                sendMessage(new WaitingMessage("\nWaiting for other players to connect...\n"));
            }

            while(this.isActive()){
                //Wait for messages from the client
                Message read = readMessage();
                //When a new message arrives, put it in the server's messageQueue
                server.notifyMessage(this, read);
            }
        } catch (IllegalMoveException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println("Connection lost");
        } finally {
            //Notify the server with a null message to signal that the connection is broken
            server.notifyMessage(this, null);
        }
    }

    @Override
    public void notifyObserver(UpdateViewMessage msg) {
        try {
            sendMessage(msg);
        } catch (IOException e) {
            System.err.println("Connection lost");
            server.notifyMessage(this, null);
        }
    }
}
