package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.protocol.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    //vars
    private ServerSocketThread serverSocketThread;

    /** All connections */
    private List<Connection> connections = new ArrayList<>();
    /** Connections that are not part of any match */
    private List<Connection> waitingConnections = new ArrayList<>();

    private final ConcurrentLinkedQueue<MessageQueueEntry> messageQueue = new ConcurrentLinkedQueue<>();

    private MatchParameters matchParameters;

    private ExecutorService executor = Executors.newFixedThreadPool(64);
    private List<Controller> controllers = new ArrayList<>();
    private Map<Connection, Controller> connectionControllerMap = new HashMap<>();

    private int nextMatchId;
    private int nextPlayerId;
    private int nextTeamId;

    //constr
    public Server() throws IOException{
        serverSocketThread = new ServerSocketThread();
        executor.submit(serverSocketThread);
        matchParameters = null;
        nextMatchId = 0;
        nextPlayerId = 0;
        nextTeamId = 0;
    }

    //methods
    public synchronized MatchParameters getMatchParameters() {
        return matchParameters;
    }

    public synchronized void setMatchParameters(int waitingConnectionMax, boolean expert) {
        matchParameters = new MatchParameters(waitingConnectionMax, expert);
    }

    public synchronized void setMatchParameters(MatchParameters parameters) {
        matchParameters = parameters;
    }

    /**
     * The notifyMessage method is called from Connection classes (other threads)
     * to put new messages from clients into the messageQueue and notify the server
     */
    public void notifyMessage(Connection connection, Message message) {
        synchronized (messageQueue) {
            messageQueue.add(new MessageQueueEntry(connection, message));
            messageQueue.notifyAll();
        }
    }

    //Register Connection
    public synchronized void registerConnection(Connection c){
        connections.add(c);
        waitingConnections.add(c);
    }

    public synchronized void deregisterConnection(Connection c){
        c.close();
        connections.remove(c);
        waitingConnections.remove(c);
        //Find the controller of the player's match
        Controller controller = connectionControllerMap.get(c);
        if (controller != null) {
            connectionControllerMap.remove(c);
            //Player is in a match, disconnect all players in the same match
            Iterator<Map.Entry<Connection, Controller>> it = connectionControllerMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Connection, Controller> entry = it.next();
                if (entry.getValue() == controller) {
                    entry.getKey().close();
                    connections.remove(entry.getKey());
                    it.remove();
                }
            }
        }
    }

    /**
     * @return bool true if there is a connected player with the same name,
     * false otherwise
     */
    public synchronized boolean nameUsed(String name) {
        for (Connection connection : connections) {
            if (name.equals(connection.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * This function starts a new match, if there are enough players in the lobby.
     * It is called from Connection.run (another thread) when a client has provided all
     * the required information (name, (max players, expert mode))
     */
    public synchronized void checkWaitingConnections(){
        if (matchParameters != null) {
            List<Connection> readyConnections = waitingConnections.stream().filter(c -> c.getName() != null).limit(matchParameters.getWaitingConnectionMax()).toList();
            if(readyConnections.size() >= matchParameters.getWaitingConnectionMax()){
                System.out.println("Starting match now...");

                List<Player> players = new ArrayList<>(matchParameters.getWaitingConnectionMax());
                for (Connection connection : readyConnections) {
                    //TODO fix tower colors
                    players.add(new Player(nextPlayerId, connection.getName(), TowerColor.values()[nextPlayerId], Wizard.values()[nextPlayerId]));
                    ++nextPlayerId;
                }
                List<Team> teams = new ArrayList<>(players.size() == 4 ? 2 : players.size());
                if (players.size() == 4) {
                    Random randomGenerator = new Random();
                    List<Player> playersRandom = new ArrayList<>(players);
                    teams.add(new Team(nextTeamId,
                            List.of(playersRandom.remove(randomGenerator.nextInt(playersRandom.size())),
                                    playersRandom.remove(randomGenerator.nextInt(playersRandom.size()))
                            ),
                            TowerColor.WHITE
                    ));
                    ++nextTeamId;
                    teams.add(new Team(nextTeamId,
                            List.of(playersRandom.remove(randomGenerator.nextInt(playersRandom.size())),
                                    playersRandom.remove(randomGenerator.nextInt(playersRandom.size()))
                            ),
                            TowerColor.BLACK
                    ));
                    ++nextTeamId;
                } else {
                    for (Player player : players) {
                        teams.add(new Team(nextTeamId, List.of(player), player.getTowerColor()));
                        ++nextTeamId;
                    }
                }

                Controller controller = new Controller(nextMatchId, teams, players, matchParameters.isExpert());
                controllers.add(controller);
                for (Connection c : readyConnections) {
                    connectionControllerMap.put(c, controller);
                }
                ++nextMatchId;

                matchParameters = null;
                waitingConnections.removeAll(readyConnections);
                for (Connection c : waitingConnections) {
                    c.close();
                }
                waitingConnections.clear();
            }
        }
    }

    /**
     * Main server loop: process all messages in the messageQueue
     */
    public void run() throws InterruptedException {
        while (true) {
            MessageQueueEntry entry;
            synchronized (messageQueue) {
                while (messageQueue.size() == 0) {
                    messageQueue.wait();
                }
                entry = messageQueue.poll();
            }
            Connection connection = entry.getConnection();
            Message message = entry.getMessage();

            if (message == null) {
                deregisterConnection(connection);
                return;
            }

            //TODO handle game messages
        }

        //TODO call this when the match finishes
        //serverSocketThread.close();
    }

    /**
     * The ServerSocketThread class starts the ServerSocket to listen for new TCP connections.
     * It adds new connections to the following Lists: connections, waitingConnections
     */
    private class ServerSocketThread implements Runnable {
        private ServerSocket serverSocket;
        private boolean active;

        public ServerSocketThread() throws IOException {
            serverSocket = new ServerSocket(61863);
            active = true;
        }

        @Override
        public void run() {
            System.out.println("ServerSocket Thread started");
            System.out.println("Server is listening on PORT " + 61863);
            System.out.println("Number of Connections: " + connections.size());

            while(isActive()){
                try{
                    Socket socket = serverSocket.accept();
                    Connection connection = new Connection(socket, Server.this);

                    registerConnection(connection);
                    executor.submit(connection);
                    System.out.println("Number of Connections: " + connections.size());
                }
                catch(IOException e){
                    System.err.println("Connection Error.");
                }
            }

            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("Unable to close server socket");
            }
        }

        public synchronized boolean isActive() {
            return active;
        }

        public synchronized void close() {
            active = false;
        }
    }

    private static class MessageQueueEntry {
        private Connection connection;
        private Message message;

        public MessageQueueEntry(Connection connection, Message message) {
            this.connection = connection;
            this.message = message;
        }

        public Connection getConnection() {
            return connection;
        }

        public Message getMessage() {
            return message;
        }
    }

    /**
     * The MatchParameters class contains the two parameters needed to start a match.
     * They are requested to the first player that connects to the lobby
     */
    private static class MatchParameters {
        private int waitingConnectionMax;
        private boolean expert;

        public MatchParameters(int waitingConnectionMax, boolean expert) {
            this.waitingConnectionMax = waitingConnectionMax;
            this.expert = expert;
        }

        public int getWaitingConnectionMax() {
            return waitingConnectionMax;
        }

        public boolean isExpert() {
            return expert;
        }
    }
}
