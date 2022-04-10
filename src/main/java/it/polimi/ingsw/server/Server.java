package it.polimi.ingsw.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    //vars
    private static final int PORT = 61863;
    private ServerSocket serverSocket;
    private List<Connection> connections = new ArrayList<>();
    private int waitingConnectionMax;
    private Map<String, Connection> waitingConnection = new HashMap<>();
    private ExecutorService executor = Executors.newFixedThreadPool(64);

    //constr
    public Server() throws IOException{
        this.serverSocket = new ServerSocket(PORT);
        waitingConnectionMax = -1;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    //methods
    //Register Connection
    private synchronized void registerConnection(Connection c){
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

    public synchronized void lobby(Connection c, String name){
        this.waitingConnection.put(name, c);

        if(waitingConnectionMax != -1 && waitingConnection.size() >= waitingConnectionMax){
            //Disconnect players in excess
            int toRemove = waitingConnection.size() - waitingConnectionMax;
            Iterator<Connection> iterator = waitingConnection.values().iterator();
            Connection curr = iterator.next();
            while (toRemove > 0) {
                curr.closeConnection();
                deregisterConnection(curr);
                iterator.remove();
                --toRemove;
                curr = iterator.next();
            }

            //TODO create match and controller objects here, once view is done

            waitingConnectionMax = -1;
            waitingConnection.clear();
        }
    }

    public void run(){
        int connectionCount = 0; //at the beginning there are no connections
        System.out.println("Server is listening on PORT " + PORT);

        while(true){
            try{
                Socket socket = serverSocket.accept();
                Connection connection;
                synchronized (this) {
                    connection = new Connection(socket, this, waitingConnection.size() == 0);
                }

                System.out.println("Number of Connections: " + connectionCount);
                connectionCount++;

                registerConnection(connection);
                executor.submit(connection);
            }
            catch(IOException e){
                System.err.println("Connection Error.");
            }
        }
    }
}
