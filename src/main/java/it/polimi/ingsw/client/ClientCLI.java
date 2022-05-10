package it.polimi.ingsw.client;

import com.google.gson.JsonSyntaxException;
import it.polimi.ingsw.model.PawnColor;
import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.message.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.IntPredicate;

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
                case ASK_ASSISTANT -> {
                    int assistant = readInt("What assistant do you want to play?");
                    sendMessage(new SetAssistantMessage(assistant));
                    System.out.println("Waiting for other players");
                }
                case ASK_ENTRANCE_STUDENT -> {
                    usedCharacter = handleCharacter();
                    int remaining = 3;
                    Map<Integer, Map<PawnColor, Integer>> islandsStudents = new HashMap<>();
                    for (PawnColor color : PawnColor.values()) {
                        if (remaining != 0) {
                            int finalRemaining = remaining;
                            int count = readInt("How many " + color.name() + " students do you want to move from entrance to an island? (" + remaining + "remaining)",
                                    n -> n <= finalRemaining, "You can move up to " + remaining + " " + color.name() + " students");
                            while (count > 0) {
                                int island = readInt("Choose an island index: (0 - " + (view.getIslands().size() - 1) + ")",
                                        n -> n >= 0 && n < view.getIslands().size(), "Island index must be between 0 and " + (view.getIslands().size() - 1));
                                int finalCount = count;
                                int student;
                                if (count > 1) {
                                    student = readInt("How many " + color.name() + " student in island n°" + island + "?",
                                            n -> n <= finalCount, "You can move up to " + count + " " + color.name() + " students");
                                } else {
                                    student = 1;
                                }

                                if (!islandsStudents.containsKey(island)) {
                                    islandsStudents.put(island, new HashMap<>());
                                }
                                islandsStudents.get(island).put(color, islandsStudents.get(island).getOrDefault(color, 0) + count);

                                count -= student;
                                remaining -= student;
                            }
                        }
                    }
                    Map<PawnColor, Integer> tableStudents = new HashMap<>();
                    while (remaining != 0) {
                        for (PawnColor color : PawnColor.values()) {
                            if (remaining != 0) {
                                int finalRemaining = remaining;
                                int count = readInt("How many " + color.name() + " students do you want to move from entrance to table? (" + remaining + " remaining)",
                                        n -> n <= finalRemaining, "You can move up to " + remaining + " " + color.name() + " students");
                                tableStudents.put(color, tableStudents.getOrDefault(color, 0) + count);
                                remaining -= count;
                            }
                        }
                    }
                    sendMessage(new SetEntranceStudentMessage(islandsStudents, tableStudents));
                }
                case ASK_MOTHER_NATURE -> {
                    if (!usedCharacter) {
                        usedCharacter = handleCharacter();
                    }
                    int motherNatureMoves = readInt("Insert mother nature moves: (1-" + view.getPlayerFromName(name).getCurrentAssistant().getMoves()+")");
                    sendMessage(new SetMotherNatureMessage(motherNatureMoves));
                }
                case ASK_CLOUD -> {
                    if (!usedCharacter) {
                        usedCharacter = handleCharacter();
                    }
                    int cloud = readInt("Choose a cloud: (0-" + (view.getClouds().size() - 1));
                    sendMessage(new SetCloudMessage(cloud));
                    if (!usedCharacter) {
                        handleCharacter();
                    }
                    usedCharacter = false;
                    sendMessage(new EndTurnMessage());
                    System.out.println("Waiting for other players");
                }
                case ERROR -> {
                    System.out.println(((ErrorMessage)msg).getError());
                }
            }
        }
        //closeProgram();
    }

    public boolean handleCharacter(){
        if(view.isExpert() && (view.getPlayerFromName(this.name).getCoins() >= view.getCharacters().get(0).getCost() ||
                view.getPlayerFromName(this.name).getCoins() >= view.getCharacters().get(1).getCost() ||
                view.getPlayerFromName(this.name).getCoins() >= view.getCharacters().get(2).getCost())){
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
                return true;
            }
        }
        return false;
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
                    int playerNumber = readInt("Insert player number: ", n -> n >= 2 && n <= 4, "Player number must be between 2 and 4");
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

    /**
     * Read an int from stdin
     */
    private int readInt(String prompt) {
        return readInt(prompt, n -> true, "");
    }

    /**
     * Read an int from stdin until it satisfies predicate
     */
    private int readInt(String prompt, IntPredicate predicate, String error) {
        while (true) {
            System.out.println(prompt);
            String playerNumberString = stdin.nextLine();
            try {
                int res = Integer.parseInt(playerNumberString);
                if (predicate.test(res)) {
                    return res;
                }
                System.out.println(error);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number formatting");
            }
        }
    }
    
    public static void clrscr(){
        try{
            String operatingSystem = System.getProperty("os.name"); //Check the current operating system

            if(operatingSystem.contains("Windows")){
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "cls");
                Process startProcess = pb.inheritIO().start();
                startProcess.waitFor();
            } else {
                ProcessBuilder pb = new ProcessBuilder("clear");
                Process startProcess = pb.inheritIO().start();

                startProcess.waitFor();
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }
}
