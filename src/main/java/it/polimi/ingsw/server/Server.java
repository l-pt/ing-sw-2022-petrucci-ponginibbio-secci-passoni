package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;
import it.polimi.ingsw.server.protocol.message.AskAssistantMessage;
import it.polimi.ingsw.server.protocol.message.UpdateViewMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server Class
 */
public class Server {
    /**
     * ExecutorService for Thread Management:
     * define executor for Controller commands onto Model
     */
    private final ExecutorService executor = Executors.newFixedThreadPool(64);

    /**
     * ServerSocketThread for Port Listening:
     * listens on TCP for new connections
     */
    private final ServerSocketThread serverSocketThread;

    /**
     * Client Connections Management:
     * list of all active connections to the server
     */
    private final List<Connection> connections = new ArrayList<>();
    /**
     * Client Connections Management:
     * list of all connections waiting in the lobby
     */
    private final List<Connection> waitingConnections = new ArrayList<>();
    /**
     * Admin:
     * firstConnection to Server
     */
    private Connection firstConnection;

    /**
     * Message Reception Management:
     * creates queue for incoming "to be handled" messages
     */
    private final Queue<MessageQueueEntry> messageQueue = new LinkedList<>();

    /**
     * Match Management:
     * parameters implemented locally in this Server.java class; contains (numberOfPlayers, isExpertMode)
     */
    private MatchParameters matchParameters;
    /**
     * Controller Management:
     * maps live connections to its associated controller
     */
    private final Map<Connection, Controller> connectionControllerMap = new HashMap<>();

    /**
     * Eryantis Server
     */
    public Server(int port) throws IOException{
        serverSocketThread = new ServerSocketThread(port);
        executor.submit(serverSocketThread);
        firstConnection = null;
        matchParameters = null;
    }

    /**
     * getWaitingConnections()
     * @return List of connections waiting in the Eryantis lobby.
     */
    public synchronized List<Connection> getWaitingConnections(){return waitingConnections;}

    /**
     * getMatchParameters()
     * @return matchParameters consisting of (number of players, isExpert).
     */
    public synchronized MatchParameters getMatchParameters() {
        return matchParameters;
    }

    /**
     * getFirstConnection() Note: client of firstConnection decides matchParameters.
     * @return firstConnection
     */
    public Connection getFirstConnection() {
        return firstConnection;
    }

    /**
     * Sets number of players, and if game is expert. Note: client of firstConnection decides matchParameters.
     * @param waitingConnectionMax game lobby max size
     * @param expert is expert mode?
     */
    public synchronized void setMatchParameters(int waitingConnectionMax, boolean expert) {
        matchParameters = new MatchParameters(waitingConnectionMax, expert);
    }

    /**
     * Sets only number of players if game is not expert. Note: client of firstConnection decides matchParameters.
     * @param parameters type MatchParameters which includes variables for (int waitingConnectionMax, boolean isExpert)
     */
    public synchronized void setMatchParameters(MatchParameters parameters) {
        matchParameters = parameters;
    }

    /**
     * Gets Connection object from connections list
     * @param name player name
     * @return Connection of client with given name
     * @throws IllegalMoveException when there is no client with given name
     */
    public Connection getConnectionFromName(String name) throws IllegalMoveException {
        for(Connection c : connections){
            if (c.getName().equals(name))
                return c;
        }
        throw new IllegalMoveException("There is no player with that name.");
    }

    /**
     * Gets list of clients associated to given controller. Note: association is by connectionControllerMap.
     * @param controller game controller
     * @return List of clients with given controller.
     */
    public List<Connection> getConnectionsFromController(Controller controller) {
        return connectionControllerMap.entrySet().stream().filter(e -> e.getValue() == controller).map(Map.Entry::getKey).toList();
    }


    /**
     * Receives new messages from client's connection, puts message into the messageQueue, and notifies the server.
     * @param connection client's Connection object
     * @param message client's message to server
     */
    public void notifyMessage(Connection connection, Message message) {
        synchronized (messageQueue) {
            messageQueue.add(new MessageQueueEntry(connection, message));
            messageQueue.notifyAll();
        }
    }

    /**
     * Adds newConnection to the server's connections and waitingConnection lists.
     * @param newConnection The new connection
     */
    public synchronized void registerConnection(Connection newConnection){
        connections.add(newConnection);
        waitingConnections.add(newConnection);

        //set as firstConnection if newConnection is the first connection
        if (firstConnection == null) {
            firstConnection = newConnection;
        }
    }

    /**
     * Closes connection and removed it from the server's connections and waitingConnection lists.
     * @param connection The connection to deregister
     */
    public synchronized void deregisterConnection(Connection connection){
        connection.close();
        connections.remove(connection);
        waitingConnections.remove(connection);

        //reset firstConnection to null if it was firstConnection
        if (firstConnection == connection) {
            firstConnection = null;
        }

        //get the controller for the match associated to the connection
        Controller controller = connectionControllerMap.get(connection);

        if (controller != null) { // implies that Client is in a match

            //disconnect the player from match
            connectionControllerMap.remove(connection);

            //disconnect all other players in the same match
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
     * Checks if given name is connected
     * @param name of player
     * @return true if there is a connected player with the same name, false otherwise
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
     * It is called from Connection.run() (another thread) when a client has provided all
     * the required information (name, (max players, expert mode))
     * @throws IOException If there are failed or interrupted I/O operations
     * @throws IllegalMoveException If the number of players selected is not valid, so the controller cannot be instantiated
     */
    public synchronized void checkWaitingConnections() throws IOException, IllegalMoveException {
        //get list of players in lobby with matchParameters
        List<Connection> connectedPlayers = waitingConnections.stream().filter(c -> c.getName() != null).limit(matchParameters.getPlayerNumber()).toList();

        //get only the names of the players
        List<String> connectionsNames = new ArrayList<>();
        for (Connection player : connectedPlayers)
            connectionsNames.add(player.getName());

        //create a new game, controller resolves team allocation and tower color allocation based on input
        Controller controller = new Controller(this, connectionsNames);

        //send each connected player an updated view of the start board
        for (Connection player : connectedPlayers) {
            player.sendMessage(
                    new UpdateViewMessage(
                    controller.getMatch().getTeams(),
                    controller.getMatch().getIslands(),
                    controller.getMatch().getPlayersOrder(),
                    controller.getMatch().getPosMotherNature(),
                    controller.getMatch().getClouds(),
                    controller.getMatch().getProfessors(),
                    controller.getMatch().getCoins(),
                    controller.getMatch().getCharacters(),
                    controller.getMatch().isExpert(),
                    controller.getMatch().getPlayersOrder().get(0).getName()
            ));

            //add player connection to list of observables
            controller.getMatch().addObserver(player);
        }

        //ask first player for assistant selection
        connectedPlayers.get(0).sendMessage(new AskAssistantMessage());

        //send group of connected playerConnections to a match
        for (Connection c : connectedPlayers) {
            connectionControllerMap.put(c, controller);
        }

        //reset lobby variables to listen for next group of players
        matchParameters = null;
        firstConnection = null;
        waitingConnections.removeAll(connectedPlayers);
        for (Connection c : waitingConnections) {
            c.close();
        }
        waitingConnections.clear();
    }

    /**
     * Main server loop that processes all messages in the messageQueue
     * @throws InterruptedException If the thread is interrupted during the activity
     * @throws IOException If there are failed or interrupted I/O operations
     */
    public void run() throws InterruptedException, IOException {
        //loop message handler
        while (true) {

            //set entry to be first message in queue
            MessageQueueEntry entry;
            synchronized (messageQueue) {
                while (messageQueue.isEmpty()) {
                    messageQueue.wait();
                }
                entry = messageQueue.remove();
            }

            //handle entry message
            Connection connection = entry.getConnection();
            Message message = entry.getMessage();
            if (message == null) {
                deregisterConnection(connection);
            } else {
                handleClientMessage(connection, message);
            }
        }
    }

    /**
     * Forwards the given message to all connections in the match controller. Note: if message is END_GAME, then all Connections associated to match controller are deregistered.
     * @param connection client connection
     * @param message message from given client
     * @throws IOException If there are failed or interrupted I/O operations
     */
    public void handleClientMessage(Connection connection, Message message) throws IOException {

        //get match controller associated to given client connection
        Controller controller = connectionControllerMap.get(connection);

        //handle clientMessage
        boolean gameFinished = false;
        for (Map.Entry<String, List<Message>> entry : controller.handleMessage(connection.getName(), message).entrySet()) {
            Connection c;
            try {
                c = getConnectionFromName(entry.getKey());
            } catch (IllegalMoveException e) {
                throw new AssertionError();
            }
            for (Message m : entry.getValue()) {
                if (m.getMessageId() == MessageId.END_GAME) {
                    gameFinished = true;
                }
                c.sendMessage(m); //forward server handled Message m to all Connections c in the match controller
            }
        }

        //if gameFinished, deregister all connections associated to match controller
        if (gameFinished) {
            for (Connection c : getConnectionsFromController(controller)) {
                deregisterConnection(c);
            }
        }
    }

    /**
     * Closes the server. The serverSocketThread stops TCP listening.
     */
    public void close() {
        serverSocketThread.close();
    }

    /**
     * The ServerSocketThread class starts the ServerSocket to listen for new TCP connections.
     * It adds new connections to the following Lists: connections, waitingConnections
     */
    private class ServerSocketThread implements Runnable {

        private final ServerSocket serverSocket;
        private final int port;
        private boolean active;

        public ServerSocketThread(int port) throws IOException {
            this.port = port;
            serverSocket = new ServerSocket(port);
            active = true;
        }

        /**
         * Main loop that handles connection requests
         */
        @Override
        public void run() {

            //server status update to console
            System.out.println("Server is listening on port: " + port);
            System.out.println("Number of Connections: " + connections.size());

            //loop while server isActive()
            while(isActive()){
                try{

                    //add new client
                    Socket socket = serverSocket.accept();
                    Connection connection = new Connection(socket, Server.this);
                    registerConnection(connection);
                    executor.submit(connection);

                    //server status update to console
                    System.out.println("Number of Connections: " + connections.size());
                }
                catch(IOException e){

                    //unable to add new client
                    System.err.println("Connection Error.");
                }
            }

            try {

                //close when not isActive()
                serverSocket.close();

            } catch (IOException e) {

                //unable to close
                System.out.println("Unable to close server socket");
            }
        }

        /**
         * isActive()
         * @return True if the server is active
         */
        public synchronized boolean isActive() {
            return active;
        }

        /**
         * Sets the server as inactive
         */
        public synchronized void close() {
            active = false;
        }
    }

    /**
     * MessageQueueEntry class is the object in the MessageQueue list. MessageQueueEntry entry has a Message m from a Connection c.
     * Note: MessageQueueEntry has only getters, as messages should not be able to be changed.
     */
    static class MessageQueueEntry {

        private final Connection connection;
        private final Message message;

        public MessageQueueEntry(Connection connection, Message message) {
            this.connection = connection;
            this.message = message;
        }

        /**
         * getConnection()
         * @return The linked connection
         */
        public Connection getConnection() {
            return connection;
        }

        /**
         * getMessage()
         * @return The linked message
         */
        public Message getMessage() {
            return message;
        }
    }

    /**
     * The MatchParameters class contains the two parameters needed to start a match.
     * They are requested to the first player that connects to the lobby
     */
    public static class MatchParameters {
        private final int playerNumber;
        private final boolean expert;

        public MatchParameters(int playerNumber, boolean expert) {
            this.playerNumber = playerNumber;
            this.expert = expert;
        }

        /**
         * getPlayerNumber()
         * @return The number of players
         */
        public int getPlayerNumber() {
            return playerNumber;
        }

        /**
         * isExpert()
         * @return True if the expert mode is active
         */
        public boolean isExpert() {
            return expert;
        }
    }
}
