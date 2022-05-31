package it.polimi.ingsw.app;

import it.polimi.ingsw.client.ClientCLI;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientCLIApp {
    public static void main(String[] args){
        Scanner stdin = new Scanner(System.in);
        String ip;
        try{
            System.out.println("Insert server ip:");
            ip = stdin.nextLine();
            while (checkIp(ip)){
                System.out.println("Wrong address format. Insert server ip:");
                ip = stdin.nextLine();
            }
            ClientCLI client = new ClientCLI(ip, 61863);
            client.run();
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    public static boolean checkIp(String ip){
        try {
            InetAddress.getByName(ip);
            return false;
        } catch(UnknownHostException e){
            return true;
        }
    }
}