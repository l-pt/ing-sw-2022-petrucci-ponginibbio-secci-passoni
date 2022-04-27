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

public class Client {
    private String ip;
    private int port;
    private Socket socket;
    private InputStreamReader in;
    private OutputStreamWriter out;

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

    public void run() throws IOException {
        socket = new Socket(ip, port);
        in = new InputStreamReader(socket.getInputStream());
        out = new OutputStreamWriter(socket.getOutputStream());

        try {
            lobbyLoop();
            //TODO Match loop
        } catch (IOException e) {
            System.out.println("Disconnected : " + e.getMessage());
            closeProgram();
        }
    }

    /**
     * Loop executed when the client is inside the lobby
     * Client waits for server questions about username, max player number and expert mode
     */
    private void lobbyLoop() throws IOException {
        Scanner stdin = new Scanner(System.in);
        Message msg;

        while (true) {
            //Wait for a server message
            try {
                msg = readMessage();
            } catch (JsonSyntaxException e) {
                System.out.println("Server sent invalid message");
                stdin.close();
                closeProgram();
                return;
            }
            //We have received a server message, check its Type to answer appropriately
            switch (msg.getMessageId()) {
                case ERROR -> {
                    System.out.println("Server error: " + ((ErrorMessage) msg).getError());
                }
                case ASK_USERNAME -> {
                    System.out.println("Insert username: ");
                    String username = stdin.nextLine();
                    sendMessage(new SetUsernameMessage(username));
                }
                case ASK_PLAYER_NUMBER -> {
                    Integer playerNumber = null;
                    while (playerNumber == null) {
                        System.out.println("Insert player number: ");
                        String playerNumberString = stdin.nextLine();
                        try {
                            playerNumber = Integer.parseInt(playerNumberString);
                            if (playerNumber < 2 || playerNumber > 4) {
                                System.out.println("Player number must be between 2 and 4");
                                playerNumber = null;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number formatting");
                        }
                    }
                    sendMessage(new SetPlayerNumberMessage(playerNumber));
                }
                case ASK_EXPERT -> {
                    String expert = null;
                    while (expert == null) {
                        System.out.println("Activate expert mode? yes/no: ");
                        expert = stdin.nextLine();
                        if (!expert.equals("yes") && !expert.equals("no")) {
                            System.out.println("Answer must be yes or no");
                            expert = null;
                        }
                    }
                    sendMessage(new SetExpertMessage(expert.equals("yes")));
                }
            }
        }
    }

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
