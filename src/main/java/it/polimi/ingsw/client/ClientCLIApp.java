package it.polimi.ingsw.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientCLIApp {
    /**
     * Main Method of ClientCLIApp
     * @param args Terminal argument
     */
    public static void main(String[] args){
        Scanner stdin = new Scanner(System.in);
        String ip, port;
        try{
            //Asks for the server ip
            System.out.println("Insert server ip:");
            ip = stdin.nextLine();

            //Loops until the server ip has a valid format
            while (checkIp(ip)){
                System.out.println("Wrong address format. Insert server ip:");
                ip = stdin.nextLine();
            }

            //Asks for the server port
            System.out.println("Insert server port (press enter for default port 61863):");
            port = stdin.nextLine();

            //Loops until the server port has a valid format
            while (checkInteger(port) && !port.equals("")){
                System.out.println("Wrong format. Insert server port (press enter for default port 61863):");
                port = stdin.nextLine();
            }

            //Sets 61863 if the server port isn't specified in args
            new ClientCLI(ip, port.equals("") ? 61863 : Integer.parseInt(port)).run();
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    /**
     * Checks if a certain ip address has a valid format
     * @param ip Ip address
     * @return True if the given ip address has an invalid format, false otherwise
     */
    public static boolean checkIp(String ip){
        try {
            InetAddress.getByName(ip);
            return false;
        } catch(UnknownHostException e){
            return true;
        }
    }

    /**
     * Checks a certain server port
     * @param port Server port
     * @return True if the given server port isn't an integer or if is less than 1 or more than 65535, false otherwise
     */
    public static boolean checkInteger(String port){
        try {
            int p = Integer.parseInt(port);
            return p < 1 || p > 65535;
        } catch(NumberFormatException e){
            return true;
        }
    }
}