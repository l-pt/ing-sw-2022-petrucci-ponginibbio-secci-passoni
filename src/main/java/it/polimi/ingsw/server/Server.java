package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.message.AskAssistantMessage;
import it.polimi.ingsw.protocol.message.UpdateViewMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    //vars
    private ServerSocketThread serverSocketThread;

    /** All connections */
    private List<Connection> connections = new ArrayList<>();
    /** Connections that are not part of any match */
    private List<Connection> waitingConnections = new ArrayList<>();
    private Connection firstConnection;

    private final Queue<MessageQueueEntry> messageQueue = new LinkedList<>();

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
        firstConnection = null;
        matchParameters = null;
        nextMatchId = 0;
        nextPlayerId = 0;
        nextTeamId = 0;
    }

    //methods
    public List<Controller> getControllers() {
        return controllers;
    }

    public synchronized List<Connection> getWaitingConnections(){return waitingConnections;}

    public synchronized MatchParameters getMatchParameters() {
        return matchParameters;
    }

    public Connection getFirstConnection() {
        return firstConnection;
    }

    public synchronized void setMatchParameters(int waitingConnectionMax, boolean expert) {
        matchParameters = new MatchParameters(waitingConnectionMax, expert);
    }

    public synchronized void setMatchParameters(MatchParameters parameters) {
        matchParameters = parameters;
    }

    public Connection getConnectionFromName(String name) throws IllegalMoveException {
        for(Connection c : connections){
            if (c.getName().equals(name))
                return c;
        }
        throw new IllegalMoveException("There is no player with that name");
    }

    public List<Connection> getConnectionsFromController(Controller controller) {
        return connectionControllerMap.entrySet().stream().filter(e -> e.getValue() == controller).map(e -> e.getKey()).toList();
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
        if (firstConnection == null) {
            firstConnection = c;
        }
    }

    public synchronized void deregisterConnection(Connection connection){
        connection.close();
        connections.remove(connection);
        waitingConnections.remove(connection);
        if (firstConnection == connection) {
            firstConnection = null;
        }
        //Find the controller of the player's match
        Controller controller = connectionControllerMap.get(connection);
        if (controller != null) {
            connectionControllerMap.remove(connection);
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
    public synchronized void checkWaitingConnections() throws IOException {
        if (matchParameters != null) {
            List<Connection> readyConnections = waitingConnections.stream().filter(c -> c.getName() != null).limit(matchParameters.getPlayerNumber()).toList();
            if(readyConnections.size() >= matchParameters.getPlayerNumber()){
                System.out.println("Starting match now...");
                Controller controller = new Controller(this, readyConnections);
                controllers.add(controller);
                for (Connection c : readyConnections) {
                    connectionControllerMap.put(c, controller);
                }

                matchParameters = null;
                firstConnection = null;
                for (Connection c : waitingConnections) {
                    c.close();
                }
                waitingConnections.removeAll(readyConnections);
                waitingConnections.clear();
            }
        }
    }

    public void close(){
        serverSocketThread.close();
    }

    /**
     * Main server loop: process all messages in the messageQueue
     */
    public void run() throws InterruptedException, IOException {
        while (true) {
            MessageQueueEntry entry;
            synchronized (messageQueue) {
                while (messageQueue.isEmpty()) {
                    messageQueue.wait();
                }
                entry = messageQueue.remove();
            }
            Connection connection = entry.getConnection();
            Message message = entry.getMessage();

            if (message == null) {
                deregisterConnection(connection);
            } else {
                Controller controller = connectionControllerMap.get(connection);
                assert controller != null;
                controller.handleClientMessage(connection, message);
            }
        }
        //TODO call this when the match finishes
        //serverSocketThread.close();
    }

    public void close() {
        serverSocketThread.close();
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

    static class MessageQueueEntry {
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
    public static class MatchParameters {
        private int playerNumber;
        private boolean expert;

        public MatchParameters(int playerNumber, boolean expert) {
            this.playerNumber = playerNumber;
            this.expert = expert;
        }

        public int getPlayerNumber() {
            return playerNumber;
        }

        public boolean isExpert() {
            return expert;
        }
    }
}
