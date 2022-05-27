package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.character.*;
import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.model.character.impl.*;
import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;
import it.polimi.ingsw.protocol.message.*;
import it.polimi.ingsw.protocol.message.character.*;
import it.polimi.ingsw.server.Connection;
import it.polimi.ingsw.server.Server;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Controller {
    private Match match;
    private Server server;
    private boolean usedCharacter = false;
    private boolean lastMessage = false;

    public Controller(Server server, List<Team> teams, List<Player> players, boolean expert){
        this.match = new Match(teams, players, expert);
        this.server = server;
    }

    public Controller(Server server, List<Connection> readyConnections) throws IOException {
        List<Player> players = new ArrayList<>(server.getMatchParameters().getPlayerNumber());
        List<Team> teams = new ArrayList<>(server.getMatchParameters().getPlayerNumber() == 4 ? 2 : server.getMatchParameters().getPlayerNumber());
        switch (server.getMatchParameters().getPlayerNumber()) {
            case 4 -> {
                List<Player> white = List.of(
                        new Player(readyConnections.get(0).getName(), TowerColor.WHITE, Wizard.values()[0]),
                        new Player(readyConnections.get(1).getName(), TowerColor.WHITE, Wizard.values()[1])
                );
                List<Player> black = List.of(
                        new Player(readyConnections.get(2).getName(), TowerColor.BLACK, Wizard.values()[2]),
                        new Player(readyConnections.get(3).getName(), TowerColor.BLACK, Wizard.values()[3])
                );
                players.addAll(white);
                players.addAll(black);
                teams.add(new Team(white, TowerColor.WHITE));
                teams.add(new Team(black, TowerColor.BLACK));
            }
            case 3, 2 -> {
                for (int i = 0; i < server.getMatchParameters().getPlayerNumber(); ++i) {
                    players.add(new Player(readyConnections.get(i).getName(), TowerColor.values()[i], Wizard.values()[i]));
                }
                //2/3 player matches have teams made of just one player
                for (Player player : players) {
                    teams.add(new Team(List.of(player), player.getTowerColor()));
                }
            }
            default -> throw new AssertionError();
        }
        this.match = new Match(teams, players, server.getMatchParameters().isExpert());
        this.server = server;

        for (int i = 0; i < readyConnections.size(); ++i){
            readyConnections.get(i).sendMessage(new UpdateViewMessage(
                    getMatch().getTeams(),
                    getMatch().getIslands(),
                    getMatch().getPlayersOrder(),
                    getMatch().getPosMotherNature(),
                    getMatch().getClouds(),
                    getMatch().getProfessors(),
                    getMatch().getCoins(),
                    getMatch().getCharacters(),
                    getMatch().isExpert(),
                    getMatch().getPlayersOrder().get(0).getName()
            ));
            match.addObserver(readyConnections.get(i));
        }
        readyConnections.get(0).sendMessage(new AskAssistantMessage());
    }

    public Match getMatch() {
        return match;
    }

    public Map<String, List<Message>> handleMessage(String name, Message message) {
        switch (message.getMessageId()) {
            case SET_ASSISTANT -> {
                try {
                    int pos = match.getPosFromName(name);
                    if (pos != match.getPlayersOrder().size() - 1) {
                        match.setCurrentPlayer(match.getPlayersOrder().get(pos + 1).getName());
                    } else {
                        match.setCurrentPlayer(match.getPlayersOrder().get(0).getName());
                    }
                    useAssistant(name, ((SetAssistantMessage) message).getAssisant());
                    if (pos != match.getPlayersOrder().size() - 1) {
                        return Map.of(match.getPlayersOrder().get(pos + 1).getName(), List.of(new AskAssistantMessage()));
                    } else {
                        Map<String, List<Message>> map = new HashMap<>();
                        map.put(match.getPlayersOrder().get(0).getName(), new ArrayList<>());
                        if (!usedCharacter) {
                            map.get(match.getPlayersOrder().get(0).getName()).add(new AskCharacterMessage());
                        }
                        map.get(match.getPlayersOrder().get(0).getName()).add(new AskEntranceStudentMessage());
                        return map;
                    }
                } catch (IllegalMoveException e) {
                    return Map.of(name, List.of(new ErrorMessage(e.getMessage()), new AskAssistantMessage()));
                }
            }
            case SET_ENTRANCE_STUDENT -> {
                SetEntranceStudentMessage entranceStudentMessage = (SetEntranceStudentMessage) message;
                try {
                    moveStudentsToIslandsAndTable(name, entranceStudentMessage.getIslandStudents(), entranceStudentMessage.getTableStudents());
                    Map<String, List<Message>> map = new HashMap<>();
                    map.put(name, new ArrayList<>());
                    if (!usedCharacter) {
                        map.get(name).add(new AskCharacterMessage());
                    }
                    map.get(name).add(new AskMotherNatureMessage());
                    return map;
                } catch (IllegalMoveException e) {
                    return Map.of(name, List.of(new ErrorMessage(e.getMessage()), new AskEntranceStudentMessage()));

                }
            }
            case SET_MOTHER_NATURE -> {
                try {
                    moveMotherNature(((SetMotherNatureMessage) message).getMotherNatureMoves(), name);
                    if (match.isGameFinished()) {
                        return match.getPlayersOrder().stream().collect(Collectors.toMap(p -> p.getName(), p -> List.of(new EndGameMessage(match.getWinningTeam()))));
                    } else if (match.isLastTurn()) {
                        int pos = match.getPosFromName(name);
                        Map<String, List<Message>> map = new HashMap<>();
                        map.put(name, new ArrayList<>());
                        if (!usedCharacter) {
                            map.get(name).add(new AskCharacterMessage());
                        }
                        match.resetAbility();
                        if (pos != match.getPlayersOrder().size() - 1) {
                            map.get(name).add(new AskEntranceStudentMessage());
                            return map;
                        } else
                            return match.getPlayersOrder().stream().collect(Collectors.toMap(p -> p.getName(), p -> List.of(new EndGameMessage(match.getWinningTeam()))));
                    } else {
                        Map<String, List<Message>> map = new HashMap<>();
                        map.put(name, new ArrayList<>());
                        if (!usedCharacter) {
                            map.get(name).add(new AskCharacterMessage());
                        }
                        map.get(name).add(new AskCloudMessage());
                        return map;
                    }
                } catch (IllegalMoveException e) {
                    return Map.of(name, List.of(new ErrorMessage(e.getMessage()), new AskMotherNatureMessage()));
                }
            }
            case SET_CLOUD -> {
                try {
                    moveStudentsFromCloud(((SetCloudMessage) message).getCloud(), name);
                    if (!usedCharacter) {
                        lastMessage = true;
                        return Map.of(name, List.of(new AskCharacterMessage()));
                    } else {
                        return endTurn(name);
                    }
                } catch (IllegalMoveException e) {
                    return Map.of(name, List.of(new ErrorMessage(e.getMessage()), new AskCloudMessage()));
                }
            }
            case USE_CHARACTER_COLOR_ISLAND -> {
                UseCharacterColorIslandMessage colorIslandMessage = (UseCharacterColorIslandMessage) message;
                try {
                    Character1 c1 = (Character1) match.getCharacterFromType(Character1.class);
                    c1.use(match, name, colorIslandMessage.getColor(), colorIslandMessage.getIsland());
                    usedCharacter = true;
                    if (lastMessage) {
                        lastMessage = false;
                        return endTurn(name);
                    }
                } catch (IllegalMoveException e) {
                    return Map.of(name, List.of(new ErrorMessage(e.getMessage()), new AskCharacterMessage(0)));
                }
            }
            case USE_CHARACTER_COLOR -> {
                UseCharacterColorMessage colorMessage = (UseCharacterColorMessage) message;
                try {
                    ((ColorCharacter) match.getCharacterFromType(Character.getClassFromId(colorMessage.getCharacterId()))).use(match, name, colorMessage.getColor());
                    usedCharacter = true;
                    if (lastMessage) {
                        lastMessage = false;
                        return endTurn(name);
                    }
                } catch (IllegalMoveException e) {
                    return Map.of(name, List.of(new ErrorMessage(e.getMessage()), new AskCharacterMessage(colorMessage.getCharacterId())));
                }
            }
            case USE_CHARACTER -> {
                UseCharacterMessage useCharacterMessage = (UseCharacterMessage) message;
                try {
                    ((NoParametersCharacter) match.getCharacterFromType(Character.getClassFromId(useCharacterMessage.getCharacterId()))).use(match, name);
                    usedCharacter = true;
                    if (lastMessage) {
                        lastMessage = false;
                        return endTurn(name);
                    }
                } catch (IllegalMoveException e) {
                    return Map.of(name, List.of(new ErrorMessage(e.getMessage()), new AskCharacterMessage(useCharacterMessage.getCharacterId())));
                }
            }
            case USE_CHARACTER_ISLAND -> {
                UseCharacterIslandMessage islandMessage = (UseCharacterIslandMessage) message;
                try {
                    ((IslandCharacter) match.getCharacterFromType(Character.getClassFromId(islandMessage.getCharacterId()))).use(match, name, islandMessage.getIsland());
                    usedCharacter = true;
                    if (match.isGameFinished()) {
                        return match.getPlayersOrder().stream().collect(Collectors.toMap(p -> p.getName(), p -> List.of(new EndGameMessage(match.getWinningTeam()))));
                    }
                    if (lastMessage) {
                        lastMessage = false;
                        return endTurn(name);
                    }
                } catch (IllegalMoveException e) {
                    return Map.of(name, List.of(new ErrorMessage(e.getMessage()), new AskCharacterMessage(islandMessage.getCharacterId())));
                }
            }
            case USE_CHARACTER_STUDENT_MAP -> {
                UseCharacterStudentMapMessage mapMessage = (UseCharacterStudentMapMessage) message;
                try {
                    ((StudentMapCharacter) match.getCharacterFromType(Character.getClassFromId(mapMessage.getCharacterId()))).use(match, name, mapMessage.getInMap(), mapMessage.getOutMap());
                    usedCharacter = true;
                    if (lastMessage) {
                        lastMessage = false;
                        return endTurn(name);
                    }
                } catch (IllegalMoveException e) {
                    return Map.of(name, List.of(new ErrorMessage(e.getMessage()), new AskCharacterMessage(mapMessage.getCharacterId())));
                }
            }
            case USE_NO_CHARACTER -> {
                if (lastMessage) {
                    lastMessage = false;
                    try {
                        return endTurn(name);
                    } catch (IllegalMoveException e) {
                        return Map.of(name, List.of(new ErrorMessage(e.getMessage())));
                    }
                }
            }
        }
        return Collections.emptyMap();
    }

    public Map<String, List<Message>> endTurn(String name) throws IllegalMoveException {
        int pos = match.getPosFromName(name);
        match.resetAbility();
        if (pos != match.getPlayersOrder().size() - 1) {
            match.setCurrentPlayer(match.getPlayersOrder().get(pos + 1).getName());
            match.updateView();
            return Map.of(match.getPlayersOrder().get(pos + 1).getName(), List.of(new AskEntranceStudentMessage()));
        } else {
            if (match.isLastTurn()) {
                return match.getPlayersOrder().stream().collect(Collectors.toMap(p -> p.getName(), p -> List.of(new EndGameMessage(match.getWinningTeam()))));
            } else {
                match.setCurrentPlayer(match.getPlayersOrder().get(0).getName());
                match.populateClouds();
                return Map.of(match.getPlayersOrder().get(0).getName(), List.of(new AskAssistantMessage()));
            }
        }
    }

    public void handleClientMessage(Connection connection, Message message) throws IOException {
        boolean gameFinished = false;
        for (Map.Entry<String, List<Message>> entry : handleMessage(connection.getName(), message).entrySet()) {
            Connection c;
            try {
                c = server.getConnectionFromName(entry.getKey());
            } catch (IllegalMoveException e) {
                throw new AssertionError();
            }
            for (Message m : entry.getValue()) {
                if (m.getMessageId() == MessageId.END_GAME) {
                    gameFinished = true;
                }
                c.sendMessage(m);
            }
        }
        if (gameFinished) {
            for (Connection c : server.getConnectionsFromController(this)) {
                server.deregisterConnection(c);
            }
        }
    }

    /**
     * Move a student from entrance to table or to an island
     */
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
        match.updateView();
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
