package it.polimi.ingsw.client;

import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.message.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
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

        try {
            SwingUtilities.invokeAndWait(() -> {
                frame = new JFrame("Eriantys");

                JPanel contentPane = new JPanel();
                contentPane.setOpaque(true);
                contentPane.setBackground(Color.WHITE);
                contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                frame.setContentPane(contentPane);

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().setLayout(new BorderLayout(10, 10));
                frame.setVisible(true);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            closeProgram();
        }
        Thread t = new Thread(new ClientSocketWorker(this));
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void processMessage(Message msg) {
        //We have received a server message, check its Type to answer appropriately
        switch (msg.getMessageId()) {
            case ERROR -> {
                System.out.println("Server error: " + ((ErrorMessage) msg).getError());
            }
            case ASK_USERNAME -> {
                frame.getContentPane().removeAll();
                frame.getContentPane().add(new JLabel("Insert username"), BorderLayout.PAGE_START);
                JTextField usernameTextField = new JTextField("");
                usernameTextField.setPreferredSize(new Dimension());
                usernameTextField.addActionListener(actionEvent -> {
                    try {
                        frame.getContentPane().removeAll();
                        frame.getContentPane().add(new JLabel("Waiting for other players..."), BorderLayout.CENTER);
                        frame.revalidate();
                        frame.repaint();
                        name = usernameTextField.getName();
                        sendMessage(new SetUsernameMessage(usernameTextField.getText()));
                    } catch (IOException e) {
                        closeProgram();
                    }
                });
                frame.getContentPane().add(usernameTextField, BorderLayout.CENTER);
                frame.revalidate();
                frame.repaint();
            }
            case ASK_PLAYER_NUMBER -> {
                frame.getContentPane().removeAll();
                frame.getContentPane().add(new JLabel("Choose player number"), BorderLayout.PAGE_START);
                JComboBox<Integer> comboBox = new JComboBox<>(new Integer[]{2, 3, 4});
                frame.getContentPane().add(comboBox, BorderLayout.CENTER);
                JButton confirmPlayers = new JButton("Confirm");
                confirmPlayers.addActionListener(actionEvent -> {
                    try {
                        frame.getContentPane().removeAll();
                        frame.revalidate();
                        frame.repaint();
                        sendMessage(new SetPlayerNumberMessage((Integer) comboBox.getSelectedItem()));
                    } catch (IOException e) {
                        closeProgram();
                    }
                });
                frame.getContentPane().add(confirmPlayers, BorderLayout.PAGE_END);
                frame.revalidate();
                frame.repaint();
            }
            case ASK_EXPERT -> {
                frame.getContentPane().removeAll();
                frame.getContentPane().add(new JLabel("Activate expert mode?"), BorderLayout.PAGE_START);
                JComboBox<Boolean> comboBox = new JComboBox<>(new Boolean[]{true, false});
                frame.getContentPane().add(comboBox, BorderLayout.CENTER);
                JButton confirmExpert = new JButton("Confirm");
                confirmExpert.addActionListener(actionEvent -> {
                    try {
                        frame.getContentPane().removeAll();
                        frame.getContentPane().add(new JLabel("Waiting for other players..."), BorderLayout.CENTER);
                        frame.revalidate();
                        frame.repaint();
                        sendMessage(new SetExpertMessage((Boolean) comboBox.getSelectedItem()));
                    } catch (IOException e) {
                        closeProgram();
                    }
                });
                frame.getContentPane().add(confirmExpert, BorderLayout.PAGE_END);
                frame.revalidate();
                frame.repaint();
            }
            case UPDATE_VIEW -> {
                //TODO
            }
        }
    }
}
