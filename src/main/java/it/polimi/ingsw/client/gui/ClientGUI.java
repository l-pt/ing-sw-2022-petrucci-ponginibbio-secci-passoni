package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.gui.component.EntranceStudentSelectorPanel;
import it.polimi.ingsw.model.PawnColor;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.message.*;
import it.polimi.ingsw.protocol.message.character.UseNoCharacterMessage;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

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
            case ASK_ASSISTANT -> {
                view.getBottomPanel().removeAll();
                view.getBottomPanel().add(new JLabel("Choose an assistant"));
                JComboBox<Integer> comboBox = new JComboBox<>(view.getAssistants().stream().map(a -> a.getValue()).toArray(Integer[]::new));
                view.getBottomPanel().add(comboBox);
                JButton confirm = new JButton("Confirm");
                confirm.addActionListener(actionEvent -> {
                    try {
                        sendMessage(new SetAssistantMessage((Integer) comboBox.getSelectedItem()));
                        view.getBottomPanel().removeAll();
                        frame.revalidate();
                        frame.repaint();
                    } catch (IOException e) {
                        closeProgram();
                    }
                });
                view.getBottomPanel().add(confirm);
                frame.revalidate();
                frame.repaint();
            }
            case ASK_ENTRANCE_STUDENT -> {
                Player player = view.getPlayerFromName(name);

                view.getBottomPanel().removeAll();
                view.getBottomPanel().setLayout(new BoxLayout(view.getBottomPanel(), BoxLayout.Y_AXIS));
                view.getBottomPanel().add(new JLabel("Move 3 entrance students"));

                int i = 0;
                JPanel selectorsPanel = new JPanel();
                EntranceStudentSelectorPanel[] studentSelectors = new EntranceStudentSelectorPanel[player.getSchool().getEntrance().size()];
                for (Student student : player.getSchool().getEntrance()) {
                    selectorsPanel.add(studentSelectors[i++] = new EntranceStudentSelectorPanel(student, view.getIslands()));
                }
                view.getBottomPanel().add(selectorsPanel);

                JLabel errorLbl = new JLabel("You must select exactly three students");
                JButton confirm = new JButton("Confirm");
                confirm.addActionListener(actionEvent -> {
                    Map<Integer, Map<PawnColor, Integer>> islandsStudents = new HashMap<>();
                    Map<PawnColor, Integer> tableStudents = new HashMap<>();
                    int totalStudents = 0;
                    for (EntranceStudentSelectorPanel pan : studentSelectors) {
                        if (pan.getSelection() == EntranceStudentSelectorPanel.SELECTION_TABLE) {
                            ++totalStudents;
                            tableStudents.put(pan.getStudent().getColor(), tableStudents.getOrDefault(pan.getStudent().getColor(), 0) + 1);
                        } else if (pan.getSelection() >= 0) {
                            ++totalStudents;
                            if (!islandsStudents.containsKey(pan.getSelection())) {
                                islandsStudents.put(pan.getSelection(), new HashMap<>());
                            }
                            islandsStudents.get(pan.getSelection()).put(pan.getStudent().getColor(), islandsStudents.get(pan.getSelection()).getOrDefault(pan.getStudent().getColor(), 0) + 1);
                        }
                    }
                    if (totalStudents == 3) {
                        try {
                            sendMessage(new SetEntranceStudentMessage(islandsStudents, tableStudents));
                            view.getBottomPanel().removeAll();
                            frame.revalidate();
                            frame.repaint();
                        } catch (IOException e) {
                            closeProgram();
                        }
                    } else {
                        view.getBottomPanel().remove(confirm);
                        view.getBottomPanel().remove(errorLbl);
                        view.getBottomPanel().add(errorLbl);
                        view.getBottomPanel().add(confirm);
                        frame.revalidate();
                        frame.repaint();
                    }
                });

                view.getBottomPanel().add(confirm);
                frame.revalidate();
                frame.repaint();
            }
            case ASK_CHARACTER -> {
                try {
                    //TODO IMPLEMENT CHARACTERS
                    sendMessage(new UseNoCharacterMessage());
                } catch (IOException e) {
                    closeProgram();
                }
            }
        }
    }
}
