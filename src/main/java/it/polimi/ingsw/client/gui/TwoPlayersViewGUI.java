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
        mainPanel.setLayout(new GridBagLayout());

        islandsPanel = new JPanel();
        islandsPanel.setPreferredSize(new Dimension(35, 1));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gbc.gridy = 0;
        gbc.gridwidth = gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.35D;
        gbc.weighty = 1D;
        mainPanel.add(islandsPanel, gbc);

        JPanel boardsPanel = new JPanel();
        boardsPanel.setLayout(new BoxLayout(boardsPanel, BoxLayout.Y_AXIS));
        boardsPanel.setPreferredSize(new Dimension(40, 1));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.40D;
        gbc.weighty = 1D;
        playerPanel = new JPanel();
        opponentPanel = new JPanel();
        boardsPanel.add(opponentPanel);
        boardsPanel.add(playerPanel);
        mainPanel.add(boardsPanel, gbc);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.setPreferredSize(new Dimension(25, 1));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.weightx = 0.25D;
        gbc.weighty = 1D;
        mainPanel.add(rightPanel, gbc);

        expertPanel = new JPanel();
        expertPanel.setPreferredSize(new Dimension(1, 33));
        gbc = new GridBagConstraints();
        gbc.gridx = gbc.gridy = 0;
        gbc.gridwidth = gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.weightx = 1D;
        gbc.weighty = 0.33D;
        rightPanel.add(expertPanel, gbc);

        cpPanel = new JPanel();
        cpPanel.setPreferredSize(new Dimension(1, 33));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1D;
        gbc.weighty = 0.33D;
        rightPanel.add(cpPanel, gbc);

        assistantsPanel = new JPanel();
        assistantsPanel.setPreferredSize(new Dimension(1, 33));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.PAGE_END;
        gbc.weightx = 1D;
        gbc.weighty = 0.33D;
        rightPanel.add(assistantsPanel, gbc);
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
