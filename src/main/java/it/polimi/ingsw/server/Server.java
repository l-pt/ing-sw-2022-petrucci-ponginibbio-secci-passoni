package it.polimi.ingsw.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    //vars
    private static final int PORT = 61863;
    private ServerSocket serverSocket;
    private List<Connection> connections = new ArrayList<Connection>();
    private Map<String, Connection> waitingConnection = new HashMap<>();
    private ExecutorService executor = Executors.newFixedThreadPool(64);

    //constr
    public Server() throws IOException{
        this.serverSocket = new ServerSocket(this.PORT);
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

    public synchronized void lobby(Connection c, String name){

        this.waitingConnection.put(name, c);

        if(waitingConnection.size() == 2){
            //TO DO
            //GAME OF 2
        }
        else if(waitingConnection.size() == 3){
            //TO DO
            //GAME OF 3
        }
        else{
            //TO DO
            //GAME OF 2 TEAMS
            //
            // FML
        }

    }

    public void run(){
        int connectionCount = 0; //at the beginning there are no connections
        System.out.println("Server is listening on PORT " + this.PORT);

        while(true){
            try{
                Socket socket = serverSocket.accept();
                Connection connection = new Connection(socket, this);

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