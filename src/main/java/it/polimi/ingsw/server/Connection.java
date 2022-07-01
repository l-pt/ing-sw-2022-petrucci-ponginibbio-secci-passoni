package it.polimi.ingsw.server;

import com.google.gson.*;
import it.polimi.ingsw.model.IllegalMoveException;
import it.polimi.ingsw.model.observer.Observer;
import it.polimi.ingsw.server.protocol.GsonSingleton;
import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.message.*;

import java.io.*;
import java.net.Socket;

/**
 * Connection Class
 * Implements: Runnable, Observer
 */
public class Connection implements Runnable, Observer<UpdateViewMessage> {

    /**
     * Allocate memory for Socket and Server.
     * Declare i/o stream variables.
     */
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Server server;

    /**
     * Personalize Connection: name, isActive, setParameters
     */
    private String name;
    private boolean isActive;
    private boolean setPlayersAndExpert;

    /**
     * Constructor requires socket and server as parameters for this Connection.
     * Initializes new i/o streams as channel for data exchange through this Connection.
     * Initializes isActive to true, setPlayersAndExpert to false.
     * @param socket stored as this.socket
     * @param server stored as this.server
     * @throws IOException If there are failed or interrupted I/O operations
     */
    public Connection(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        isActive = true;
        setPlayersAndExpert = false;
    }

    /**
     * getName()
     * @return player's name as String, or null if the name is yet to be entered
     */
    public synchronized String getName() {
        return name;
    }

    /**
     * isActive() returns the isActive status
     * @return this.isActive
     */
    private synchronized boolean isActive(){
        return this.isActive;
    }

    /**
     * Takes Message msg as input, converts msg to a JSON object, then outputs the msg_json to the DataOutputStream this.out
     * @param msg outgoing message
     * @throws IOException If there are failed or interrupted I/O operations
     */
    public void sendMessage(Message msg) throws IOException {
        String msg_json = GsonSingleton.get().toJson(msg);
        synchronized (out) {
            out.writeUTF(msg_json);
            out.flush();
        }
    }

    /**
     * Reads data from DataInputStream this.in which is expected in JSON format.
     * The method then converts the JSON into a java object, specifically a Message.class object.
     * The Message object is then returned
     * @return Message read from input stream
     * @throws JsonSyntaxException If the message received contains json syntax errors
     * @throws IOException If there are failed or interrupted I/O operations
     */
    private Message readMessage() throws JsonSyntaxException, IOException {
        String json = in.readUTF();
        return GsonSingleton.get().fromJson(json, Message.class);
    }

    /**
     * Reads message of Class object, and casts it into a Message object.
     * @param messageClass Used for casting
     * @param <T> Extends Message
     * @return The message after casting
     * @throws JsonSyntaxException If the message received contains json syntax errors
     * @throws IOException If there are failed or interrupted I/O operations
     */
    private <T extends Message> T readMessage(Class<T> messageClass) throws JsonSyntaxException, IOException {
        return messageClass.cast(readMessage());
    }

    /**
     * Closes connection by deactivating socket.
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
     * Close the connection if there are no match parameters.
     */
    public synchronized void close(){
        if (setPlayersAndExpert) {
            server.setMatchParameters(null);
        }
        //close the connection
        closeConnection();
    }

    /**
     * Run() for Connection Class
     */
    @Override
    public void run(){
        try{

            //ask player to enter username
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

            //establish server and match parameters
            synchronized (server) {

                //if player is the first connected, he is match admin
                if (server.getFirstConnection() == this) {

                    //match admin sets number of players in the match
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

                    //match admin decides to activate expert mode or not
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

            //check if match is full
            if(server.getWaitingConnections().size() >= server.getMatchParameters().getPlayerNumber()) {

                //check lobby
                server.checkWaitingConnections();

            }else {

                //continue waiting for new connections
                sendMessage(new WaitingMessage("\nWaiting for other players to connect...\n"));
            }

            //to do loop for this connection
            while(this.isActive()){

                //wait for messages from the client
                Message read = readMessage();

                //when message arrives, notify server and forward messages
                //server will queue incoming messages
                server.notifyMessage(this, read);
            }

        //exceptions
        } catch (IllegalMoveException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println("Connection lost");
        } finally {

            //notify the server with a null message to signal that the connection is broken
            server.notifyMessage(this, null);
        }
    }

    /**
     * When connection is lost, send a message to the player's interface
     * @param msg The update message to send
     */
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
