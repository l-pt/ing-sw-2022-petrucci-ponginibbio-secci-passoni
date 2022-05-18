package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.message.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

public class ClientGUI extends Client {
    private JFrame frame;
    private JPanel questionsPanel;
    private ViewGUI view;

    public ClientGUI(String ip, int port){
        super(ip, port);
    }

    public JFrame getFrame() {
        return frame;
    }

    public String getName() {
        return name;
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
                contentPane.setAlignmentX(Component.CENTER_ALIGNMENT);
                contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

                questionsPanel = new JPanel();
                questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));
                questionsPanel.setMaximumSize(new Dimension(320, 90));
                questionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                contentPane.add(questionsPanel);

                frame.setContentPane(contentPane);

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
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
                questionsPanel.removeAll();
                questionsPanel.add(new JLabel("Insert username"));
                JTextField usernameTextField = new JTextField("");
                usernameTextField.setPreferredSize(new Dimension(320, 40));
                usernameTextField.setAlignmentX(Component.LEFT_ALIGNMENT);
                usernameTextField.addActionListener(actionEvent -> {
                    if (usernameTextField.getText().isEmpty()) {
                        return;
                    }
                    try {
                        questionsPanel.removeAll();
                        questionsPanel.add(new JLabel("Waiting for other players..."));
                        frame.revalidate();
                        frame.repaint();
                        name = usernameTextField.getText();
                        sendMessage(new SetUsernameMessage(usernameTextField.getText()));
                    } catch (IOException e) {
                        closeProgram();
                    }
                });
                questionsPanel.add(usernameTextField);
                frame.revalidate();
                frame.repaint();
            }
            case ASK_PLAYER_NUMBER -> {
                questionsPanel.removeAll();
                questionsPanel.add(new JLabel("Choose player number"));
                JComboBox<Integer> comboBox = new JComboBox<>(new Integer[]{2, 3, 4});
                comboBox.setMaximumSize(new Dimension(320, 40));
                comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
                questionsPanel.add(comboBox);
                JButton confirmPlayers = new JButton("Confirm");
                confirmPlayers.setMaximumSize(new Dimension(320, 40));
                confirmPlayers.setAlignmentX(Component.LEFT_ALIGNMENT);
                confirmPlayers.addActionListener(actionEvent -> {
                    try {
                        questionsPanel.removeAll();
                        frame.revalidate();
                        frame.repaint();
                        sendMessage(new SetPlayerNumberMessage((Integer) comboBox.getSelectedItem()));
                    } catch (IOException e) {
                        closeProgram();
                    }
                });
                questionsPanel.add(confirmPlayers);
                frame.revalidate();
                frame.repaint();
            }
            case ASK_EXPERT -> {
                questionsPanel.removeAll();
                questionsPanel.add(new JLabel("Activate expert mode?"));
                JComboBox<Boolean> comboBox = new JComboBox<>(new Boolean[]{true, false});
                comboBox.setMaximumSize(new Dimension(320, 40));
                comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
                questionsPanel.add(comboBox);
                JButton confirmExpert = new JButton("Confirm");
                confirmExpert.setMaximumSize(new Dimension(320, 40));
                confirmExpert.setAlignmentX(Component.LEFT_ALIGNMENT);
                confirmExpert.addActionListener(actionEvent -> {
                    try {
                        questionsPanel.removeAll();
                        questionsPanel.add(new JLabel("Waiting for other players..."));
                        frame.revalidate();
                        frame.repaint();
                        sendMessage(new SetExpertMessage((Boolean) comboBox.getSelectedItem()));
                    } catch (IOException e) {
                        closeProgram();
                    }
                });
                questionsPanel.add(confirmExpert, BorderLayout.PAGE_END);
                frame.revalidate();
                frame.repaint();
            }
            case UPDATE_VIEW -> {
                questionsPanel = null;
                if (view == null) {
                    view = ViewGUI.create(this, ((UpdateViewMessage) msg).getPlayersOrder().size());
                }
                view.handleUpdateView((UpdateViewMessage) msg);
            }
        }
    }
}
