package it.polimi.ingsw.client;

import com.google.gson.JsonSyntaxException;
import it.polimi.ingsw.model.PawnColor;
import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.message.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientCLI extends Client{
    private String name;
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
                case ASK_ASSISTANT -> {
                    Integer assistant = null;
                    while (assistant == null) {
                        System.out.println("What assistant do you want to play?");
                        String assistantString = stdin.nextLine();
                        try {
                            assistant = Integer.parseInt(assistantString);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number formatting");
                        }
                    }
                    sendMessage(new SetAssistantMessage(assistant));
                }
                case ASK_ENTRANCE_STUDENT -> {
                    handleCharacter();
                    int remaining = 3;
                    for (PawnColor color : PawnColor.values()) {
                        if (remaining != 0) {
                            Integer count = null;
                            while (count == null) {
                                System.out.println("How many " + color.name() + " students do you want to move from entrance to an island? (" + remaining + "remaining)");
                                String countString = stdin.nextLine();
                                try {
                                    count = Integer.parseInt(countString);
                                    if (count > remaining) {
                                        System.out.println("You can move up to " + remaining + " " + color.name() + " students");
                                        count = null;
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid number formatting");
                                }
                            }
                            if (count > 0) {
                                while (count > 0) {
                                    Integer island = null;
                                    while (island == null) {
                                        System.out.println("Choose an island index: (0 - " + (view.getIslands().size() - 1) + ")");
                                        String countString = stdin.nextLine();
                                        try {
                                            island = Integer.parseInt(countString);
                                            if (island < 0 || island > view.getIslands().size() - 1) {
                                                System.out.println("The island index must be between 0 and " + (view.getIslands().size() - 1));
                                                island = null;
                                            }
                                        } catch (NumberFormatException e) {
                                            System.out.println("Invalid number formatting");
                                        }
                                    }
                                    Integer student = null;
                                    while (student == null) {
                                        System.out.println("How many " + color.name() + " student in island n°" + island + "?");
                                        String countString = stdin.nextLine();
                                        try {
                                            student = Integer.parseInt(countString);
                                            if (student > count) {
                                                System.out.println("You can move up to " + count + " " + color.name() + " students");
                                                student = null;
                                            }
                                            } catch (NumberFormatException e) {
                                            System.out.println("Invalid number formatting");
                                        }
                                    }

                                    count -= student;
                                    remaining -= student;
                                }
                            }

                        }

                    }
                }
                case ERROR -> {
                    System.out.println(((ErrorMessage)msg).getError());
                }
            }
        }
        //closeProgram();
    }

    public void handleCharacter(){
        if(view.getPlayerFromName(this.name).getCoins() >= view.getCharacters().get(0).getCost() ||
                view.getPlayerFromName(this.name).getCoins() >= view.getCharacters().get(1).getCost() ||
                view.getPlayerFromName(this.name).getCoins() >= view.getCharacters().get(2).getCost()){
            String character = null;
            while (character == null) {
                System.out.println("Do you want to play a character card? (yes/no)");
                character = stdin.nextLine();
                if (!character.equals("yes") && !character.equals("no")) {
                    System.out.println("Answer must be yes or no");
                    character = null;
                }
            }
            if(character.equals("yes")){
                //TODO
            }
        }
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
                    name = username;
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
