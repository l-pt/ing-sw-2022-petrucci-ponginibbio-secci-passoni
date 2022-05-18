package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.model.Player;

import javax.swing.*;
import java.awt.*;

public class TwoPlayersViewGUI extends ViewGUI {
    private JPanel playerPanel;
    private JPanel opponentPanel;

    protected TwoPlayersViewGUI(ClientGUI client) {
        super(client);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        islandsPanel = new JPanel();
        islandsPanel.setPreferredSize(new Dimension(5 * 1000, 1));
        mainPanel.add(islandsPanel);

        JPanel boardsPanel = new JPanel();
        boardsPanel.setLayout(new BoxLayout(boardsPanel, BoxLayout.Y_AXIS));
        boardsPanel.setPreferredSize(new Dimension(5 * 1000, 1));
        playerPanel = new JPanel();
        opponentPanel = new JPanel();
        boardsPanel.add(opponentPanel);
        boardsPanel.add(playerPanel);
        mainPanel.add(boardsPanel);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(4 * 1000, 1));
        expertPanel = new JPanel();
        cpPanel = new JPanel();
        assistantsPanel = new JPanel();
        rightPanel.add(expertPanel);
        rightPanel.add(cpPanel);
        rightPanel.add(assistantsPanel);
        mainPanel.add(rightPanel);
    }

    @Override
    protected void draw() {
        super.draw();
        Player clientPlayer = getPlayerFromName(client.getName());
        Player opponent = null;
        for (Player player : originalPlayersOrder) {
            if (player != clientPlayer) {
                opponent = player;
                break;
            }
        }
        drawPlayer(playerPanel, clientPlayer);
        drawPlayer(opponentPanel, opponent);
        client.getFrame().revalidate();
        client.getFrame().repaint();
    }
}
