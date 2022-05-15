package it.polimi.ingsw.client;

import com.google.gson.JsonSyntaxException;
import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.message.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientGUI extends Client{
    JFrame frame;

    public ClientGUI(String ip, int port){
        super(ip, port);
    }

    @Override
    public void run() throws IOException {
        socket = new Socket(ip, port);
        in = new InputStreamReader(socket.getInputStream());
        out = new OutputStreamWriter(socket.getOutputStream());

        frame = new JFrame("Eriantys");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        Thread t = new Thread(() -> {
            try {
                lobbyLoop();
            } catch (IOException e) {
                closeProgram();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void lobbyLoop() throws IOException {
        Message msg;
        System.out.println("Test test lobbyLoop");

        while (true) {
            //Wait for a server message
            try {
                msg = readMessage();
                System.out.println("MSG");
            } catch (JsonSyntaxException e) {
                System.out.println("Server sent invalid message");
                closeProgram();
                return;
            }
            //We have received a server message, check its Type to answer appropriately
            switch (msg.getMessageId()) {
                case ERROR -> {
                    System.out.println("Server error: " + ((ErrorMessage) msg).getError());
                }
                case ASK_USERNAME -> {
                    System.out.println("Ask usernsme");
                    frame.getContentPane().removeAll();
                    JTextField usernameTextField = new JTextField("Insert username");
                    usernameTextField.addActionListener(actionEvent -> {
                        try {
                            sendMessage(new SetUsernameMessage(usernameTextField.getText()));
                            name = usernameTextField.getName();
                            frame.getContentPane().removeAll();
                            frame.getContentPane().add(new JLabel("Waiting for other players..."));
                        } catch (IOException e) {
                            closeProgram();
                        }
                    });
                    frame.getContentPane().add(usernameTextField);
                    frame.pack();
                }
                case ASK_PLAYER_NUMBER -> {
                    JComboBox<Integer> comboBox = new JComboBox<>(new Integer[]{2, 3, 4});
                    frame.getContentPane().removeAll();
                    frame.getContentPane().setLayout(new FlowLayout());
                    frame.getContentPane().add(comboBox);
                    JButton confirmPlayers = new JButton("Confirm");
                    frame.getContentPane().add(confirmPlayers);
                    confirmPlayers.addActionListener(actionEvent -> {
                        try {
                            sendMessage(new SetPlayerNumberMessage((Integer) comboBox.getSelectedItem()));
                            frame.getContentPane().removeAll();
                        } catch (IOException e) {
                            closeProgram();
                        }
                    });
                }
                case ASK_EXPERT -> {
                    JComboBox<Boolean> comboBox = new JComboBox<>(new Boolean[]{true, false});
                    frame.getContentPane().setLayout(new FlowLayout());
                    frame.getContentPane().add(comboBox);
                    JButton confirmExpert = new JButton("Confirm");
                    frame.getContentPane().add(confirmExpert);
                    confirmExpert.addActionListener(actionEvent -> {
                        try {
                            sendMessage(new SetExpertMessage((Boolean) comboBox.getSelectedItem()));
                            frame.getContentPane().removeAll();
                            frame.getContentPane().add(new JLabel("Waiting for other players..."));
                        } catch (IOException e) {
                            closeProgram();
                        }
                    });
                }
                case UPDATE_VIEW -> {
                    return;
                }
            }
        }
    }
}
