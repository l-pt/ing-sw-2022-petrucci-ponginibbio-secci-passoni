package it.polimi.ingsw.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

//implement Observable extension !!! later (TO DO)
public class Connection implements Runnable{

    private Socket socket;
    private Scanner in;
    private PrintWriter out;

    private Server server;
    private String name;
    private boolean isActive = true;
    private boolean isFirst;

    public Connection(Socket socket, Server server, boolean isFirst){
        this.socket = socket;
        this.server = server;
        this.isFirst = isFirst;
    }

    public String getName() {
        return name;
    }

    private synchronized boolean isActive(){
        return this.isActive;
    }

    public void send(String message){
        out.println(message);
        out.flush();
    }

    public void asyncSend(final String message){
        server.getExecutor().submit(() -> send(message));
    }

    public synchronized void closeConnection(){
        send("Connection closed by the server.");

        //attempt to close connection via socket object method
        try{
            this.socket.close();
        }
        catch(IOException e){
            System.err.println(e.getMessage());
        }

        //update active status
        this.isActive = false;
    }

    private void close(){
        //close the connection
        closeConnection();

        //deregister the client
        System.out.println("Deregistering client...");
        server.deregisterConnection(this);
        System.out.println("Client deregistered.");
    }

    @Override
    public void run(){
        try{
            //link socket input/output streams to local variables
            in = new Scanner(socket.getInputStream()).useDelimiter("\n");
            out = new PrintWriter(socket.getOutputStream());

            //get name of connected user
            String n;
            send("What is your name? ");
            n = in.nextLine();
            while (server.nameUsed(n)) {
                send("Name already used. What is your name? ");
                n = in.nextLine();
            }
            name = n;

            if (isFirst) {
                send("Choose game size: ");
                Integer gameSize = null;
                do {
                    try {
                        gameSize = in.nextInt();
                    } catch (NoSuchElementException e) {
                        //TODO
                        in.skip("");
                        send("Invalid number. Choose game size: ");
                    }
                    if (gameSize != null && (gameSize < 2 || gameSize > 4)) {
                        send("Game size must be between 2 and 4. Choose game size: ");
                    }
                } while (gameSize == null || gameSize < 2 || gameSize > 4);
                server.setWaitingConnectionMax(gameSize);

                send("Activate expert mode? yes/no");
                String expert = null;
                do {
                    try {
                        expert = in.nextLine();
                    } catch (NoSuchElementException e) {
                        send("Answer yes/no. Activate expert mode? ");
                    }
                    if (expert != null && !expert.equals("yes") && !expert.equals("no")) {
                        send("Answer yes/no. Activate expert mode? ");
                    }
                } while (expert == null || (!expert.equals("yes") && !expert.equals("no")));
                server.setExpert(expert.equals("yes"));
            }

            //add user to lobby
            server.lobby(this, name);

            while(this.isActive()){
                String read = in.nextLine();
                //notify(read); //WHY DOESNT THIS WORK?? TO DO
            }
        }
        catch(IOException e){
            System.err.println(e.getMessage());
        }
        finally{
            close();
        }
    }
}
