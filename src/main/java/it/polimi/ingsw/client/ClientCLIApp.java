package it.polimi.ingsw.client;

import it.polimi.ingsw.client.ClientCLI;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientCLIApp {
    /**
     *
     * @param args
     */
    public static void main(String[] args){
        Scanner stdin = new Scanner(System.in);
        String ip, port;
        ClientCLI client;
        try{
            System.out.println("Insert server ip:");
            ip = stdin.nextLine();
            while (checkIp(ip)){
                System.out.println("Wrong address format. Insert server ip:");
                ip = stdin.nextLine();
            }
            System.out.println("Insert server port (press enter for default port 61863):");
            port = stdin.nextLine();
            while (checkInteger(port) && !port.equals("")){
                System.out.println("Wrong format. Insert server port (press enter for default port 61863):");
                port = stdin.nextLine();
            }
            if (port.equals("")) {
                client = new ClientCLI(ip, 61863);
            }else {
                client = new ClientCLI(ip, Integer.parseInt(port));
            }
            client.run();
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    /**
     *
     * @param ip
     * @return
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
     *
     * @param port
     * @return
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