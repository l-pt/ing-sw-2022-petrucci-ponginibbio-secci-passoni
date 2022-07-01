package it.polimi.ingsw.client;

import com.google.gson.JsonSyntaxException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.model.character.impl.Character1;
import it.polimi.ingsw.model.character.impl.Character11;
import it.polimi.ingsw.model.character.impl.Character7;
import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;
import it.polimi.ingsw.server.protocol.message.*;
import it.polimi.ingsw.server.protocol.message.character.*;
import it.polimi.ingsw.view.ViewCLI;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.function.IntPredicate;

public class ClientCLI extends Client{
    private final Scanner stdin;
    private ViewCLI view;

    public ClientCLI(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;
        stdin = new Scanner(System.in);
        socket = new Socket(ip, port);
        //2 min timeout
        socket.setSoTimeout(120000);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    /**
     * Main client loop that processes all messages received
     * @throws IOException If there are failed or interrupted I/O operations
     */
    @Override
    public void run() throws IOException {
        try {
            lobby();
            game();
        } catch (IOException e) {
            System.out.println("Disconnected from the server");
            closeProgram();
        }
    }

    /**
     * Loop executed when the client is inside the lobby
     * Client waits for server questions about username, max player number and expert mode
     * @throws IOException If there are failed or interrupted I/O operations
     */
    public void lobby() throws IOException {
        Message msg;
        while (true) {
            //Wait for a server message
            try {
                msg = readMessage();
                if (handleLobbyMessage(msg)) {
                    return;
                }
            } catch (JsonSyntaxException e) {
                System.out.println("Server sent invalid message");
                closeProgram();
                return;
            }
        }
    }

    /**
     * Processes the lobby message received
     * @param msg The lobby message received
     * @return True if the match has started, false otherwise
     * @throws IOException If there are failed or interrupted I/O operations
     */
    public boolean handleLobbyMessage(Message msg) throws IOException {
        //We have received a server message, check its Type to answer appropriately
        switch (msg.getMessageId()) {
            case ASK_USERNAME -> {
                System.out.println("Insert username: ");
                String username = stdin.nextLine();
                sendMessage(new SetUsernameMessage(username));
                name = username;
            }
            case ASK_PLAYER_NUMBER -> {
                int playerNumber = readInt("Insert player number (2 - 4): ");
                sendMessage(new SetPlayerNumberMessage(playerNumber));
            }
            case ASK_EXPERT -> {
                String expert = null;
                while (expert == null) {
                    System.out.println("Activate expert mode? (yes/no): ");
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
                view.printTitle();
                view.printDescription();
                view.handleUpdateView((UpdateViewMessage) msg);
                return true;
            }
            case WAITING -> System.out.println(((WaitingMessage) msg).getMessage());
            case ERROR -> System.out.println(((ErrorMessage) msg).getError());
        }
        return false;
    }

    /**
     * Loop executed when the match has started
     * Client waits for server questions about game moves
     * @throws IOException If there are failed or interrupted I/O operations
     */
    public void game() throws IOException {
        boolean running = true;
        while (running) {
            Message msg;
            //Wait for a server message
            try {
                msg = readMessage();
                handleGameMessage(msg);
                if(msg.getMessageId().equals(MessageId.END_GAME))
                    running = false;
            } catch (JsonSyntaxException e) {
                System.out.println("Server sent invalid message");
                closeProgram();
                return;
            }
        }
        closeProgram();
    }

    /**
     * Processes the game message received
     * @param msg The game message received
     * @throws IOException If there are failed or interrupted I/O operations
     */
    public void handleGameMessage(Message msg) throws IOException {
        switch (msg.getMessageId()) {
            case ASK_ASSISTANT -> {
                int assistant = readInt("What assistant do you want to play?");
                sendMessage(new SetAssistantMessage(assistant));
            }
            case ASK_ENTRANCE_STUDENT -> {
                int remaining = 3;
                Map<Integer, Map<PawnColor, Integer>> islandsStudents = new HashMap<>();
                Map<PawnColor, Integer> tableStudents = new HashMap<>();
                List<Student> entranceStudents = new ArrayList<>(view.getPlayerFromName(name).getSchool().getEntrance());
                while(remaining > 0) {
                    Iterator<Student> it = entranceStudents.iterator();
                    while (it.hasNext() && remaining > 0) {
                        Student student = it.next();
                        boolean ok = false;
                        while (!ok) {
                            System.out.println("Where do you want to move the " + student.getColor().name() + " student? (1 - " + view.getIslands().size() + " for islands, write \"t\" for table, write \"e\" to leave the student in your entrance) - " + remaining + " remaining");
                            String in = stdin.nextLine();
                            if (in.equals("t")) {
                                if ((view.getPlayerFromName(name).getSchool().getTableCount(student.getColor()) + tableStudents.getOrDefault(student.getColor(), 0)) < 10) {
                                    tableStudents.put(student.getColor(), tableStudents.getOrDefault(student.getColor(), 0) + 1);
                                    it.remove();
                                    --remaining;
                                    ok = true;
                                }else {
                                    System.out.println("You can't move more " + student.getColor().name() + " students because the table contains 10 students");
                                }
                            } else if (in.equals("e")) {
                                ok = true;
                            } else {
                                try {
                                    int island = Integer.parseInt(in);
                                    if (island >= 1 && island <= view.getIslands().size()) {
                                        if (!islandsStudents.containsKey(island - 1)) {
                                            islandsStudents.put(island - 1, new HashMap<>());
                                        }
                                        islandsStudents.get(island - 1).put(student.getColor(), islandsStudents.get(island - 1).getOrDefault(student.getColor(), 0) + 1);
                                        it.remove();
                                        --remaining;
                                        ok = true;
                                    } else {
                                        System.out.println("Island index must be between 1 and " + view.getIslands().size());
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid answer format");
                                }
                            }
                        }
                    }
                }
                sendMessage(new SetEntranceStudentMessage(islandsStudents, tableStudents));
            }
            case ASK_MOTHER_NATURE -> {
                Player p = view.getPlayerFromName(name);
                int motherNatureMoves = readInt("Insert mother nature moves: (1 - " + (p.getCurrentAssistant().getMoves() + p.getAdditionalMoves()) + ")");
                sendMessage(new SetMotherNatureMessage(motherNatureMoves));
            }
            case ASK_CLOUD -> {
                int cloud = readInt("Choose a cloud: (1 - " + view.getClouds().size() + ")");
                sendMessage(new SetCloudMessage(cloud - 1));
            }
            case ASK_CHARACTER -> {
                AskCharacterMessage askCharacterMessage = (AskCharacterMessage) msg;
                if (askCharacterMessage.getCharacterId() == -1) {
                    handleCharacter();
                } else {
                    askCharacterParameters(askCharacterMessage.getCharacterId());
                }
            }
            case END_GAME -> {
                Team winner = ((EndGameMessage) msg).getWinner();
                if (winner == null) {
                    System.out.println("Game over (draw).\n");
                } else if (view.getPlayersOrder().size() == 4) {
                    System.out.println("Game over. Winners: " + String.join(", ", winner.getPlayers().stream().map(Player -> getName().toUpperCase()).toList()) + "\n");
                } else {
                    System.out.println("Game over. Winner: " + winner.getPlayers().get(0).getName().toUpperCase() + "\n");
                }
            }
            case UPDATE_VIEW -> view.handleUpdateView((UpdateViewMessage) msg);
            case ERROR -> System.out.println(((ErrorMessage)msg).getError());
        }
    }

    /**
     * Checks if the conditions for using the characters apply
     * @param index The character index in the character list
     * @return True if the conditions are verified
     */
    public boolean checkCharacters(int index){
        if (view.getCharacters().get(index).getId() == 9) {
            return view.getPlayerFromName(name).getSchool().getTables().values().stream().mapToInt(List::size).sum() != 0;
        }else if(view.getCharacters().get(index).getId() == 10){
            Character11 character11 = (Character11) view.getCharacters().get(index);
            for (PawnColor color : PawnColor.values()) {
                if (character11.getStudentsColorCount(color) == character11.getStudents().size() && view.getPlayerFromName(name).getSchool().getTableCount(color) == 10) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Asks the client whether to activate the effect of some character
     * @throws IOException If there are failed or interrupted I/O operations
     */
    public void handleCharacter() throws IOException {
        if(view.isExpert() && ((view.getPlayerFromName(this.name).getCoins() >= view.getCharacters().get(0).getCost() && checkCharacters(0)) ||
                (view.getPlayerFromName(this.name).getCoins() >= view.getCharacters().get(1).getCost() && checkCharacters(1)) ||
                view.getPlayerFromName(this.name).getCoins() >= view.getCharacters().get(2).getCost() && checkCharacters(2))){
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
                int characterIndex;
                int tableStudentsCount = view.getPlayerFromName(name).getSchool().getTables().values().stream().mapToInt(List::size).sum();
                do {
                    characterIndex = readInt("Which character do you want to play? (1 - 3)", n -> n > 0 && n <= 3,
                            "Character number must be between 1 and 3");
                    if(view.getCharacters().get(characterIndex - 1).getCost() > view.getPlayerFromName(name).getCoins()){
                        System.out.println("You don't have enough coins to activate this character ability");
                        characterIndex = -1;
                    }else if (view.getCharacters().get(characterIndex - 1).getId() == 9 && tableStudentsCount == 0){
                        System.out.println("You can't use this character because you don't have any students on your tables");
                        characterIndex = -1;
                    }else if (view.getCharacters().get(characterIndex - 1).getId() == 10){
                        boolean check = false;
                        PawnColor c = null;
                        Character11 character11 = (Character11) view.getCharacters().get(characterIndex - 1);
                        for (PawnColor color : PawnColor.values()){
                            if (character11.getStudentsColorCount(color) == character11.getStudents().size() && view.getPlayerFromName(name).getSchool().getTableCount(color) == 10){
                                check = true;
                                c = color;
                            }
                        }
                        if (check) {
                            System.out.println("You can't use this character because your " + c.name() + " table contains 10 students and on this character there are only " + c.name() + " students");
                            characterIndex = -1;
                        }
                    }
                }while (characterIndex == -1);
                Character c = view.getCharacters().get(characterIndex - 1);
                askCharacterParameters(c.getId());
            } else {
                sendMessage(new UseNoCharacterMessage());
            }
        } else {
            sendMessage(new UseNoCharacterMessage());
        }
    }

    /**
     * Asks the client how to use the character effect
     * @param characterId The id of the character to use
     * @throws IOException If there are failed or interrupted I/O operations
     */
    public void askCharacterParameters(int characterId) throws IOException {
        Character c = null;
        for (Character character : view.getCharacters()) {
            if (character.getId() == characterId) {
                c = character;
                break;
            }
        }
        switch (characterId) {
            case 0 -> {
                Character1 c1 = (Character1) c;
                PawnColor color = null;
                while (color == null) {
                    System.out.println("Choose a student color on this character");
                    try {
                        color = PawnColor.valueOf(stdin.nextLine().toUpperCase());
                        if (c1.getStudentsColorCount(color) == 0) {
                            System.out.println("There are no student with color " + color + " on this character");
                            color = null;
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid color");
                    }
                }
                int island = readInt("Choose an island (1 - " + (view.getIslands().size()) + ")", n -> n > 0 && n <= view.getIslands().size(),
                        "Island number must be between 1 and " + view.getIslands().size());
                sendMessage(new UseCharacterColorIslandMessage(color, island - 1));
            }
            case 1,3,5,7 -> sendMessage(new UseCharacterMessage(characterId));
            case 2,4 -> {
                int island = readInt("Choose an island (1 - " + (view.getIslands().size()) + ")", n -> n > 0 && n <= view.getIslands().size(),
                        "Island number must be between 1 and " + view.getIslands().size());
                sendMessage(new UseCharacterIslandMessage(characterId, island - 1));
            }
            case 6 -> {
                Character7 c7 = (Character7) c;
                int students = readInt("How many students do you want to exchange? (1 - 3)", n -> n >= 1 && n <= 3,
                        "Student number must be between 1, 2 or 3");
                int remaining = students;
                Map<PawnColor, Integer> cardToEntrance = new HashMap<>();
                Map<PawnColor, Integer> entranceToCard = new HashMap<>();
                List<Student> characterStudents = new ArrayList<>(c7.getStudents());
                while (remaining > 0) {
                    Iterator<Student> it = characterStudents.iterator();
                    while (it.hasNext() && remaining > 0) {
                        Student student = it.next();
                        boolean ok = false;
                        while (!ok) {
                            System.out.println("Do you want to move the " + student.getColor().name() + " student from this character to your entrance? (yes/no) - " + remaining + " remaining");
                            String in = stdin.nextLine();
                            if (in.equals("yes")) {
                                cardToEntrance.put(student.getColor(), cardToEntrance.getOrDefault(student.getColor(), 0) + 1);
                                it.remove();
                                --remaining;
                                ok = true;
                            } else if (in.equals("no")) {
                                ok = true;
                            } else System.out.println("Answer must be yes or no");
                        }
                    }
                }
                remaining = students;
                List<Student> entranceStudents = new ArrayList<>(view.getPlayerFromName(name).getSchool().getEntrance());
                while (remaining > 0) {
                    Iterator<Student> it = entranceStudents.iterator();
                    while (it.hasNext() && remaining > 0) {
                        Student student = it.next();
                        boolean ok = false;
                        while (!ok) {
                            System.out.println("Do you want to move the " + student.getColor().name() + " student from your entrance to this character card? (yes/no) - " + remaining + " remaining");
                            String in = stdin.nextLine();
                            if (in.equals("yes")) {
                                entranceToCard.put(student.getColor(), entranceToCard.getOrDefault(student.getColor(), 0) + 1);
                                it.remove();
                                --remaining;
                                ok = true;
                            } else if (in.equals("no")) {
                                ok = true;
                            } else System.out.println("Answer must be yes or no");
                        }
                    }
                }
                sendMessage(new UseCharacterStudentMapMessage(characterId, entranceToCard ,cardToEntrance));
            }
            case 8,11 -> {
                PawnColor color = null;
                while (color == null) {
                    System.out.println("Choose a student color");
                    try {
                        color = PawnColor.valueOf(stdin.nextLine().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid color");
                    }
                }
                sendMessage(new UseCharacterColorMessage(characterId, color));
            }
            case 9 -> {
                int tableStudentsCount = view.getPlayerFromName(name).getSchool().getTables().values().stream().mapToInt(List::size).sum();
                int students;
                if (tableStudentsCount > 1) {
                    students = readInt("How many students do you want to exchange? (1 - 2)", n -> n >= 1 && n <= 2,
                            "Student number must be 1 or 2");
                }else students = 1;
                int remaining = students;
                Map<PawnColor, Integer> entranceToTable = new HashMap<>();
                Map<PawnColor, Integer> tableToEntrance = new HashMap<>();
                Map<PawnColor, List<Student>> tableStudents = new HashMap<>(view.getPlayerFromName(name).getSchool().getTables());
                while (remaining > 0) {
                    for (PawnColor color : PawnColor.values()) {
                        if (view.getPlayerFromName(name).getSchool().getTableCount(color) > 0) {
                            Iterator<Student> it = tableStudents.get(color).iterator();
                            while (it.hasNext() && remaining > 0) {
                                Student student = it.next();
                                boolean ok = false;
                                String in = null;
                                while (!ok) {
                                    System.out.println("Do you want to move the " + student.getColor().name() + " student from your table to your entrance? (yes/no) - " + remaining + " remaining");
                                    in = stdin.nextLine();
                                    if (in.equals("yes")) {
                                        tableToEntrance.put(student.getColor(), tableToEntrance.getOrDefault(student.getColor(), 0) + 1);
                                        it.remove();
                                        --remaining;
                                        ok = true;
                                    } else if (in.equals("no")) {
                                        ok = true;
                                    } else System.out.println("Answer must be yes or no");
                                }
                                if (in.equals("no")){
                                    break;
                                }
                            }
                        }
                    }
                }
                remaining = students;
                List<Student> entranceStudents = new ArrayList<>(view.getPlayerFromName(name).getSchool().getEntrance());
                while (remaining > 0) {
                    Iterator<Student> it = entranceStudents.iterator();
                    while (it.hasNext() && remaining > 0) {
                        Student student = it.next();
                        boolean ok = false;
                        if ((view.getPlayerFromName(name).getSchool().getTableCount(student.getColor()) +
                                entranceToTable.getOrDefault(student.getColor(), 0) -
                                tableToEntrance.getOrDefault(student.getColor(), 0)) < 10) {
                            while (!ok) {
                                System.out.println("Do you want to move the " + student.getColor().name() + " student from your entrance to your table? (yes/no) - " + remaining + " remaining");
                                String in = stdin.nextLine();
                                if (in.equals("yes")) {
                                    entranceToTable.put(student.getColor(), entranceToTable.getOrDefault(student.getColor(), 0) + 1);
                                    it.remove();
                                    --remaining;
                                    ok = true;
                                } else if (in.equals("no")) {
                                    ok = true;
                                } else System.out.println("Answer must be yes or no");
                            }
                        }
                    }
                }
                sendMessage(new UseCharacterStudentMapMessage(characterId, entranceToTable, tableToEntrance));
            }
            case 10 -> {
                Character11 c11 = (Character11) c;
                PawnColor color = null;
                while (color == null) {
                    System.out.println("Choose a student color on this character card");
                    try {
                        color = PawnColor.valueOf(stdin.nextLine().toUpperCase());
                        if (c11.getStudentsColorCount(color) == 0) {
                            System.out.println("There are no students with color " + color.name() + " on the character card");
                            color = null;
                        }else if(view.getPlayerFromName(name).getSchool().getTableCount(color) == 10){
                            System.out.println("You can't move more " + color + " students because the table contains 10 students");
                            color = null;
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid color");
                    }
                }
                sendMessage(new UseCharacterColorMessage(characterId, color));
            }
        }
    }

    /**
     * Closes scanner and calls super.closeProgram()
     */
    public void closeProgram() {
        stdin.close();
        super.closeProgram();
    }

    /**
     * Reads an int from stdin
     * @param prompt The string to print on screen
     * @return The integer value represented by the argument received through the scanner in decimal
     */
    private int readInt(String prompt) {
        return readInt(prompt, n -> true, "");
    }

    /**
     * Reads an int from stdin until it satisfies predicate
     * @param prompt The string to print on screen
     * @param predicate The predicate to satisfy
     * @param error The error string
     * @return The integer value represented by the argument received through the scanner in decimal
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
}
