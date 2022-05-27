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
        super(ip, port);
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
                return true;
            }
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
            case UPDATE_VIEW -> {
                view.handleUpdateView((UpdateViewMessage) msg);
            }
            case ASK_ASSISTANT -> {
                int assistant = readInt("What assistant do you want to play?");
                sendMessage(new SetAssistantMessage(assistant));
            }
            case ASK_ENTRANCE_STUDENT -> {
                int remaining = 3;
                Map<Integer, Map<PawnColor, Integer>> islandsStudents = new HashMap<>();
                Map<PawnColor, Integer> tableStudents = new HashMap<>();

                List<Student> entranceStudents = view.getPlayerFromName(name).getSchool().getEntrance();
                while(remaining > 0) {
                    Iterator<Student> it = entranceStudents.iterator();
                    while (it.hasNext() && remaining > 0) {
                        Student student = it.next();
                        boolean ok = false;
                        while (!ok) {
                            System.out.println("Where do you want to move the " + student.getColor().name() + " student? (1 - " + view.getIslands().size() + " for islands, write \"t\" for table, press enter to leave the student in the entrance) - " + remaining + " remaining");

                            String in = stdin.nextLine();
                            if (in.equals("t")) {
                                tableStudents.put(student.getColor(), tableStudents.getOrDefault(student.getColor(), 0) + 1);
                                it.remove();
                                --remaining;
                                ok = true;
                            } else if (in.equals("")) {
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
            case END_GAME -> {
                Team winner = ((EndGameMessage) msg).getWinner();
                if (view.getPlayersOrder().size() == 4) {
                    System.out.println("Game over. Winners: " + String.join(", ", winner.getPlayers().stream().map(p -> p.getName()).toList()));
                } else {
                    System.out.println("Game over. Winner: " + winner.getPlayers().get(0));
                }
            }
            case ERROR -> {
                System.out.println(((ErrorMessage)msg).getError());
            }
            case ASK_CHARACTER -> {
                AskCharacterMessage askCharacterMessage = (AskCharacterMessage) msg;
                if (askCharacterMessage.getCharacterId() == -1) {
                    handleCharacter();
                } else {
                    askCharacterParameters(askCharacterMessage.getCharacterId());
                }
            }
        }
    }

    public void handleCharacter() throws IOException {
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
                int characterIndex;
                do {
                    characterIndex = readInt("Which character do you want to play? (1 - 3)", n -> n > 0 && n <= 3,
                            "Character number must be between 1 and 3");
                    if(view.getCharacters().get(characterIndex - 1).getCost() > view.getPlayerFromName(name).getCoins()){
                        System.out.println("You don't have enough coins to activate this character ability");
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
                    System.out.println("Choose a student color");
                    try {
                        color = PawnColor.valueOf(stdin.nextLine());
                        if (c1.getStudentsColorCount(color) == 0) {
                            System.out.println("There are no student with color " + color + " on the character card");
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
            case 1,3,7 -> {
                sendMessage(new UseCharacterMessage(characterId));
            }
            case 2,4,5 -> {
                int island = readInt("Choose an island (1 - " + (view.getIslands().size()) + ")", n -> n > 0 && n <= view.getIslands().size(),
                        "Island number must be between 1 and " + view.getIslands().size());
                sendMessage(new UseCharacterIslandMessage(characterId, island - 1));
            }
            case 6 -> {
                Character7 c7 = (Character7) c;
                int students = readInt("How many students do you want to exchange? (1 - 3)", n -> n >= 1 && n <= 3,
                        "You can exchange up to 3 students");
                Map<PawnColor, Integer> cardToEntrance = new HashMap<>();
                Map<PawnColor, Integer> entranceToCard = new HashMap<>();
                while (students > 0) {
                    for (PawnColor color : PawnColor.values()) {
                        int cardStudents = c7.getStudentsColorCount(color);
                        if (cardStudents > 0) {
                            int finalStudents = students;
                            int sel = readInt("How many " + color.name() + " students? (0 - " + Math.min(cardStudents, students) + ")",
                                    n -> n >= 0 && n <= Math.min(cardStudents, finalStudents), "Student number must be between 0 and " + Math.min(cardStudents, students));
                            students -= sel;
                            cardToEntrance.put(color, sel);
                        }
                    }
                }
                students = cardToEntrance.size();
                while (students > 0) {
                    for (PawnColor color : PawnColor.values()) {
                        int entranceStudent = view.getPlayerFromName(name).getSchool().getEntranceCount(color);
                        if (entranceStudent > 0) {
                            int finalStudents = students;
                            int sel = readInt("How many " + color.name() + " students? (0 - " + Math.min(entranceStudent, students) + ")",
                                    n -> n >= 0 && n <= Math.min(entranceStudent, finalStudents), "Student number must be between 0 and " + Math.min(entranceStudent, students));
                            students -= sel;
                            entranceToCard.put(color, sel);
                        }
                    }
                }
                sendMessage(new UseCharacterStudentMapMessage(characterId, cardToEntrance, entranceToCard));
            }
            case 8,11 -> {
                PawnColor color = null;
                while (color == null) {
                    System.out.println("Choose a student color");
                    try {
                        color = PawnColor.valueOf(stdin.nextLine());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid color");
                    }
                }
                sendMessage(new UseCharacterColorMessage(characterId, color));
            }
            case 9 -> {
                int students = readInt("How many students do you want to exchange? (1 - 2)", n -> n >= 1 && n <= 2,
                        "You can exchange up to 2 students");
                Map<PawnColor, Integer> entranceToTable = new HashMap<>();
                Map<PawnColor, Integer> tableToEntrance = new HashMap<>();
                while (students > 0) {
                    for (PawnColor color : PawnColor.values()) {
                        int entranceStudents = view.getPlayerFromName(name).getSchool().getEntranceCount(color);
                        if (entranceStudents > 0) {
                            int finalStudents = students;
                            int sel = readInt("How many " + color.name() + " students? (0 - " + Math.min(entranceStudents, students) + ")",
                                    n -> n >= 0 && n <= Math.min(entranceStudents, finalStudents), "Student number must be between 0 and " + Math.min(entranceStudents, students));
                            students -= sel;
                            entranceToTable.put(color, sel);
                        }
                    }
                }
                students = entranceToTable.size();
                while (students > 0) {
                    for (PawnColor color : PawnColor.values()) {
                        int tableStudents = view.getPlayerFromName(name).getSchool().getTableCount(color);
                        if (tableStudents > 0) {
                            int finalStudents = students;
                            int sel = readInt("How many " + color.name() + " students? (0 - " + Math.min(tableStudents, students) + ")",
                                    n -> n >= 0 && n <= Math.min(tableStudents, finalStudents), "Student number must be between 0 and " + Math.min(tableStudents, students));
                            students -= sel;
                            tableToEntrance.put(color, sel);
                        }
                    }
                }
                sendMessage(new UseCharacterStudentMapMessage(characterId, entranceToTable, tableToEntrance));
            }
            case 10 -> {
                Character11 c11 = (Character11) c;
                PawnColor color = null;
                while (color == null) {
                    System.out.println("Choose a student color");
                    try {
                        color = PawnColor.valueOf(stdin.nextLine());
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
