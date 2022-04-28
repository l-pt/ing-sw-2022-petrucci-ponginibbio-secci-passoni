package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    //vars
    private ServerSocket serverSocket;
    private List<Connection> connections = new ArrayList<>();
    private int waitingConnectionMax;
    private boolean expert;
    private List<Connection> waitingConnections = new ArrayList<>();
    private ExecutorService executor = Executors.newFixedThreadPool(64);
    private List<Controller> controllers = new ArrayList<>();
    private int nextMatchId;
    private int nextPlayerId;
    private int nextTeamId;

    //constr
    public Server() throws IOException{
        this.serverSocket = new ServerSocket(61863);
        waitingConnectionMax = -1;
        expert = false;
        nextMatchId = 0;
        nextPlayerId = 0;
        nextTeamId = 0;
    }

    public List<Connection> getConnections(){
        return connections;
    }

    public synchronized List<Connection> getWaitingConnections(){
        return waitingConnections;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    //methods
    //Register Connection
    public synchronized void registerConnection(Connection c){
        this.connections.add(c);
    }

    //Deregister Connection
    public synchronized void deregisterConnection(Connection c){
        this.connections.remove(c);

        //TO DO
        //find other users that are connected to this user and manage appropriately
    }

    public synchronized void setWaitingConnectionMax(int waitingConnectionMax) {
        this.waitingConnectionMax = waitingConnectionMax;
    }

    public synchronized void setExpert(boolean expert) {
        this.expert = expert;
    }

    public synchronized boolean nameUsed(String name) {
        for (Connection connection : connections) {
            if (name.equals(connection.getName())) {
                return true;
            }
        }
        return false;
    }

    public synchronized void addToLobby(Connection c) {
        this.waitingConnections.add(c);
    }

    public synchronized void checkWatingConnections(){
        if(waitingConnectionMax != -1 && waitingConnections.size() >= waitingConnectionMax){
            //Disconnect players in excess
            int toRemove = waitingConnections.size() - waitingConnectionMax;
            Connection curr;
            while (toRemove > 0) {
                curr = waitingConnections.remove(waitingConnections.size()-1);
                curr.closeConnection();
                deregisterConnection(curr);
                --toRemove;
            }

            System.out.println("Starting match now...");

            List<Player> players = new ArrayList<>(waitingConnectionMax);
            for (Connection connection : waitingConnections) {
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

            controllers.add(new Controller(nextMatchId, teams, players, expert));
            ++nextMatchId;

            waitingConnectionMax = -1;
            waitingConnections.clear();
        }
    }

    public void run(){
        System.out.println("Server is listening on PORT " + 61863);
        System.out.println("Number of Connections: " + connections.size());

        while(true){
            try{
                Socket socket = serverSocket.accept();
                Connection connection;
                synchronized (this) {
                    connection = new Connection(socket, this, waitingConnections.size() == 0);
                }

                registerConnection(connection);
                executor.submit(connection);
                System.out.println("Number of Connections: " + connections.size());
            }
            catch(IOException e){
                System.err.println("Connection Error.");
            }
        }
    }
}
