package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.message.*;
import it.polimi.ingsw.server.Connection;
import it.polimi.ingsw.server.Server;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Controller {
    private Match match;
    private Server server;

    public Controller(Server server, List<Team> teams, List<Player> players, boolean expert) {
        this.match = new Match(teams, players, expert);
        this.server = server;
    }

    public Match getMatch() {
        return match;
    }

    public void handleClientMessage(Connection connection, Message message) throws IOException {
        switch (message.getMessageId()){
            case SET_ASSISTANT -> {
                try {
                    int pos = match.getPosFromName(connection.getName());
                    useAssistant(connection.getName(), ((SetAssistantMessage)message).getAssisant());
                    if (pos != match.getPlayersOrder().size() - 1)
                        server.getConnectionFromName(match.getPlayersOrder().get(pos + 1).getName()).sendMessage(new AskAssistantMessage());
                    else
                        server.getConnectionFromName(match.getPlayersOrder().get(0).getName()).sendMessage(new AskEntranceStudentMessage());
                }catch (IllegalMoveException e){
                    connection.sendMessage(new ErrorMessage(e.getMessage()));
                    connection.sendMessage(new AskAssistantMessage());
                }
            }
            case SET_ENTRANCE_STUDENT -> {
                SetEntranceStudentMessage entranceStudentMessage = (SetEntranceStudentMessage) message;
                try {
                    int pos = match.getPosFromName(connection.getName());
                    moveStudentsToIslandsAndTable(connection.getName(), entranceStudentMessage.getIslandStudents(), entranceStudentMessage.getTableStudents());
                    if (pos != match.getPlayersOrder().size() - 1)
                        server.getConnectionFromName(match.getPlayersOrder().get(pos + 1).getName()).sendMessage(new AskEntranceStudentMessage());
                    //else
                        //TODO server.getConnectionFromName(match.getPlayersOrder().get(0).getName()).sendMessage(new AskEntranceStudentMessage());
                }catch (IllegalMoveException e){
                    connection.sendMessage(new ErrorMessage(e.getMessage()));
                    connection.sendMessage(new AskEntranceStudentMessage());
                }
            }
        }
    }

    /**
     * Move a student from entrance to table
     */
    public void moveStudentToTable(String playerName, PawnColor color) throws IllegalMoveException {
        Player player = match.getPlayerFromName(playerName);
        player.getSchool().addStudentToTable(color);
    }

    public void moveStudentsToIslandsAndTable(String playerName, Map<Integer, Map<PawnColor, Integer>> islandsStudents, Map<PawnColor, Integer> tableStudents) throws IllegalMoveException {
        //Check that all island indexes are valid
        for (int island : islandsStudents.keySet()) {
            if (island < 0 || island >= match.getIslands().size()) {
                throw new IllegalMoveException("Island " + island + " does not exist");
            }
        }
        Player player = match.getPlayerFromName(playerName);
        //Check if the number of students in the entrance is sufficient
        for (PawnColor color : PawnColor.values()) {
            int usedStudents = islandsStudents.values().stream().flatMap(m -> m.entrySet().stream()).filter(e -> e.getKey() == color).mapToInt(e -> e.getValue()).sum() +
                    tableStudents.getOrDefault(color, 0);
            if (player.getSchool().getEntranceCount(color) < usedStudents) {
                throw new IllegalMoveException("There aren't enough students with color " + color.name() + " in the entrance");
            }
        }
        //Move students from entrance to islands
        for (Map.Entry<Integer, Map<PawnColor, Integer>> entry : islandsStudents.entrySet()) {
            int island = entry.getKey();
            for (Map.Entry<PawnColor, Integer> islandEntry : entry.getValue().entrySet()) {
                List<Student> extractedStudents = player.getSchool().removeEntranceStudentsByColor(islandEntry.getKey(), islandEntry.getValue());
                match.getIslands().get(island).addStudents(extractedStudents);
            }
        }
        //Move students from entrance to table
        for (Map.Entry<PawnColor, Integer> entry : tableStudents.entrySet()) {
            match.playerMoveStudents(entry.getKey(), entry.getValue(), playerName);
        }
    }

    /**
     * Move a student from entrance to an island
     */
    public void moveStudentToIsland(String playerName, PawnColor color, int island) throws IllegalMoveException {
        if (island < 0 || island >= match.getIslands().size()) {
            throw new IllegalMoveException("Island " + island + " does not exist");
        }
        Player player = match.getPlayerFromName(playerName);
        if (player.getSchool().getEntranceCount(color) == 0) {
            throw new IllegalMoveException("There are no students with color " + color.name() + " in the entrance");
        }
        List<Student> students = player.getSchool().removeEntranceStudentsByColor(color, 1);
        match.getIslands().get(island).addStudents(students);
    }

    /**
     * Move all the students on a cloud to the player's entrance
     */
    public void moveStudentsFromCloud(int cloudIndex, String playerName) throws IllegalMoveException {
        match.moveStudentsFromCloud(cloudIndex, playerName);
    }

    /**
     * Move mother nature
     */
    public void moveMotherNature(int moves, String playerName) throws IllegalMoveException {
        match.moveMotherNature(moves, playerName);
    }

    /**
     * Use the assistant with the given value
     */
    public void useAssistant(String playerName, int value) throws IllegalMoveException {
        match.useAssistant(playerName, value);
    }

    /**
     * Get the character object with given index
     */
    public Character getCharacter(int characterIndex) throws IllegalMoveException {
        return match.getCharacter(characterIndex);
    }
}
