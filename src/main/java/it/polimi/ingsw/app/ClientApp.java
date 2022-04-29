package it.polimi.ingsw.app;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.ClientCLI;
import it.polimi.ingsw.client.ClientGUI;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientApp
{
    public static void main(String[] args){
        Scanner stdin = new Scanner(System.in);
        String ip, mod;
        try{
            System.out.println("Insert server ip:");
            ip = stdin.nextLine();
            while (checkIp(ip)){
                System.out.println("Wrong address format. Insert server ip:");
                ip = stdin.nextLine();
            }

            System.out.println("cli/gui?");
            mod = stdin.nextLine();
            while (!(mod.equals("cli") || mod.equals("gui"))){
                System.out.println("Answer must be cli or gui. cli/gui?");
                mod = stdin.nextLine();
            }
            if (mod.equals("cli")) {
                ClientCLI client = new ClientCLI(ip, 61863);
                client.run();
            } else {
                ClientGUI client = new ClientGUI(ip, 61863);
                client.run();
            }
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