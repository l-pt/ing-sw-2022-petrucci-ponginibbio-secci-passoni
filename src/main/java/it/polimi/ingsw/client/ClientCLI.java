package it.polimi.ingsw.client;

import com.google.gson.JsonSyntaxException;
import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.message.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientCLI extends Client{
    private Scanner stdin;
    private ViewCLI view;

    public ClientCLI(String ip, int port){
        super(ip, port);
        stdin = new Scanner(System.in);
    }

    @Override
    public void run() throws IOException {
        socket = new Socket(ip, port);
        in = new InputStreamReader(socket.getInputStream());
        out = new OutputStreamWriter(socket.getOutputStream());

        try {
            lobbyLoop();
            gameLoop();
        } catch (IOException e) {
            System.out.println("Disconnected : " + e.getMessage());
            closeProgram();
        }
    }

    private void gameLoop() throws IOException {
        System.out.println("Starting game loop");
        while (true) {
            Message msg;
            //Wait for a server message
            try {
                msg = readMessage();
            } catch (JsonSyntaxException e) {
                System.out.println("Server sent invalid message");
                closeProgram();
                return;
            }
            switch (msg.getMessageId()) {
                case UPDATE_VIEW -> {
                    view.handleUpdateView((UpdateViewMessage) msg);
                }
                //TODO other messages
            }
        }
        //closeProgram();
    }

    /**
     * Loop executed when the client is inside the lobby
     * Client waits for server questions about username, max player number and expert mode
     */
    private void lobbyLoop() throws IOException {
        Message msg;

        while (true) {
            //Wait for a server message
            try {
                msg = readMessage();
            } catch (JsonSyntaxException e) {
                System.out.println("Server sent invalid message");
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
                case UPDATE_VIEW -> {
                    view = new ViewCLI(this);
                    view.handleUpdateView((UpdateViewMessage) msg);
                    return;
                }
            }
        }
    }

    public void closeProgram() {
        stdin.close();
        super.closeProgram();
    }
}
