package it.polimi.ingsw.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

//implement Observable extension !!! later (TO DO)
public class Connection implements Runnable{

    private Socket socket;
    private Scanner in;
    private PrintWriter out;

    private Server server;
    private String name;
    private boolean isActive = true;

    public Connection(Socket socket, Server server){
        this.socket = socket;
        this.server = server;
    }

    private synchronized boolean isActive(){
        return this.isActive;
    }

    public void send(String message){
        out.println(message);
        out.flush();
    }

    public void asyncSend(final String message){
        new Thread(new Runnable(){
            @Override
            public void run(){
                send(message);
            }
        }).start();
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
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream());

            //get name of connected user
            send("What is your name? ");
            name = in.nextLine();

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