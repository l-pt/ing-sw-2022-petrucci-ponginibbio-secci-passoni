package it.polimi.ingsw.server;

import com.google.gson.*;
import it.polimi.ingsw.protocol.*;
import it.polimi.ingsw.protocol.message.*;

import java.io.*;
import java.net.Socket;

//implement Observable extension !!! later (TO DO)
public class Connection implements Runnable{
    private Socket socket;
    private InputStreamReader in;
    private OutputStreamWriter out;
    private Server server;
    private String name;
    private boolean isActive = true;
    private boolean isFirst;

    public Connection(Socket socket, Server server, boolean isFirst){
        this.socket = socket;
        this.server = server;
        this.isFirst = isFirst;
    }

    public String getName() {
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
        out.write(json.length());
        out.write(json, 0, json.length());
        out.flush();
    }

    /**
     * Receive a message from the client
     */
    public Message readMessage() throws JsonSyntaxException, IOException {
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
    public <T extends Message> T readMessage(Class<T> messageClass) throws JsonSyntaxException, IOException {
        return messageClass.cast(readMessage());
    }

    public synchronized void closeConnection(){
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

    private void close(){
        //close the connection
        closeConnection();

        //deregister the client
        System.out.println("Deregistering client...");
        server.deregisterConnection(this);
        System.out.println("Client deregistered.");
    }

    @Override
    public void run(){
        try{
            //link socket input/output streams to local variables
            in = new InputStreamReader(socket.getInputStream());
            out = new OutputStreamWriter(socket.getOutputStream());

            //get name of connected user
            SetUsernameMessage usernameMessage = null;
            while(usernameMessage == null) {
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

            if (isFirst) {
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
                server.setWaitingConnectionMax(playerNumberMessage.getPlayersNumber());

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
                server.setExpert(expertMessage.getExpert());
            }

            //add user to lobby
            server.lobby(this, name);

            while(this.isActive()){
                Message read = readMessage();
                //TODO implement notify
                //notify(read); //WHY DOESNT THIS WORK?? TO DO
            }
        }
        catch(IOException e){
            System.err.println(e.getMessage());
        }
        finally{
            close();
        }
    }
}
