package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Team;
import it.polimi.ingsw.model.Tower;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TwoPlayersViewGUI extends ViewGUI {
    private JPanel playerPanel;
    private JPanel opponentPanel;

    protected TwoPlayersViewGUI(ClientGUI client) {
        super(client);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        islandsPanel = new JPanel();
        islandsPanel.setPreferredSize(new Dimension(4 * 1000, 1));
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
        rightPanel.setPreferredSize(new Dimension(3 * 1000, 1));
        expertPanel = new JPanel();
        expertPanel.setPreferredSize(new Dimension(1, 3 * 1000));
        cpPanel = new JPanel();
        cpPanel.setPreferredSize(new Dimension(1, 3 * 1000));
        assistantsPanel = new JPanel();
        assistantsPanel.setPreferredSize(new Dimension(1, 3 * 1000));
        rightPanel.add(expertPanel);
        rightPanel.add(cpPanel);
        rightPanel.add(assistantsPanel);
        mainPanel.add(rightPanel);
    }

    @Override
    protected void draw() {
        super.draw();
        Player me = getPlayerFromName(client.getName());
        Team myTeam = teams.stream().filter(t -> t.isTeamMember(me)).findAny().get();

        Player other = playersOrder.stream().filter(p -> !p.getName().equals(me.getName())).findAny().get();
        Team otherTeam = teams.stream().filter(t -> t.isTeamMember(other)).findAny().get();

        drawPlayer(playerPanel, me, myTeam.getTowers());
        drawPlayer(opponentPanel, other, otherTeam.getTowers());
        client.getFrame().revalidate();
        client.getFrame().repaint();
    }
}
