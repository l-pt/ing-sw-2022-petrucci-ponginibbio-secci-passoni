package it.polimi.ingsw.client;

import com.google.gson.JsonSyntaxException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.model.character.impl.Character1;
import it.polimi.ingsw.model.character.impl.Character11;
import it.polimi.ingsw.model.character.impl.Character7;
import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;
import it.polimi.ingsw.protocol.message.*;
import it.polimi.ingsw.protocol.message.character.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.*;
import java.util.function.IntPredicate;

public class ClientCLI extends Client{
    private Scanner stdin;
    private ViewCLI view;

    public ClientCLI(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;
        stdin = new Scanner(System.in);
        socket = new Socket(ip, port);
        in = new InputStreamReader(socket.getInputStream());
        out = new OutputStreamWriter(socket.getOutputStream());
    }

    @Override
    public void run() throws IOException {
        try {
            lobby();
            game();
        } catch (IOException e) {
            System.out.println("Disconnected : " + e.getMessage());
            closeProgram();
        }
    }

    /**
     * Loop executed when the client is inside the lobby
     * Client waits for server questions about username, max player number and expert mode
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
     * @return boolean true if the match has started, false otherwise
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
                view.handleUpdateView((UpdateViewMessage) msg);
                return true;
            }
            case WAITING -> System.out.println(((WaitingMessage) msg).getMessage());
            case ERROR -> System.out.println(((ErrorMessage) msg).getError());
        }
        return false;
    }

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
                                tableStudents.put(student.getColor(), tableStudents.getOrDefault(student.getColor(), 0) + 1);
                                it.remove();
                                --remaining;
                                ok = true;
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
                if (view.getPlayersOrder().size() == 4) {
                    System.out.println("Game over. Winners: " + String.join(", ", winner.getPlayers().stream().map(Player::getName).toList()));
                } else {
                    System.out.println("Game over. Winner: " + winner.getPlayers().get(0));
                }
            }
            case UPDATE_VIEW -> view.handleUpdateView((UpdateViewMessage) msg);
            case ERROR -> System.out.println(((ErrorMessage)msg).getError());
        }
    }

    public boolean checkCharacter10(){
        int tableStudentsCount = view.getPlayerFromName(name).getSchool().getTables().values().stream().mapToInt(List::size).sum();
        return (view.getCharacters().get(0).getId() != 9 || tableStudentsCount != 0) &&
                (view.getCharacters().get(1).getId() != 9 || tableStudentsCount != 0) &&
                (view.getCharacters().get(2).getId() != 9 || tableStudentsCount != 0);
    }

    public void handleCharacter() throws IOException {
        if(view.isExpert() && (view.getPlayerFromName(this.name).getCoins() >= view.getCharacters().get(0).getCost() ||
                view.getPlayerFromName(this.name).getCoins() >= view.getCharacters().get(1).getCost() ||
                view.getPlayerFromName(this.name).getCoins() >= view.getCharacters().get(2).getCost()) && checkCharacter10()){
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
                        System.out.println("You can't use this character because don't have any students on your tables");
                        characterIndex = -1;
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
                List<Student> entranceStudents = new ArrayList<>(view.getPlayerFromName(name).getSchool().getEntrance());
                while (remaining > 0) {
                    Iterator<Student> it = entranceStudents.iterator();
                    while (it.hasNext() && remaining > 0) {
                        Student student = it.next();
                        boolean ok = false;
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
                remaining = students;
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
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid color");
                    }
                }
                sendMessage(new UseCharacterColorMessage(characterId, color));
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
}
