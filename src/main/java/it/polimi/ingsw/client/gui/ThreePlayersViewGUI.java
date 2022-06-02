package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Team;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ThreePlayersViewGUI extends ViewGUI {
    private JPanel playerPanel;
    private JPanel opponent1Panel;
    private JPanel opponent2Panel;

    protected ThreePlayersViewGUI(ClientGUI client) {
        super(client);
        mainPanel.setLayout(new GridBagLayout());

        islandsPanel = new JPanel(new GridLayout(5, 3));
        islandsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.BLACK, Color.BLACK), "Islands", TitledBorder.CENTER, TitledBorder.CENTER));
        islandsPanel.setPreferredSize(new Dimension(35, 1));
        mainPanel.add(islandsPanel, new GridBagConstraints(
                0, 0,
                1, 1,
                0.35D, 1D,
                GridBagConstraints.PAGE_START, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        JPanel boardsPanel = new JPanel(new GridBagLayout());
        boardsPanel.setPreferredSize(new Dimension(40, 1));
        playerPanel = new JPanel(new BorderLayout());
        playerPanel.setPreferredSize(new Dimension(1, 33));
        boardsPanel.add(playerPanel, new GridBagConstraints(
                0, 2,
                1, 1,
                1D, 0.33D,
                GridBagConstraints.PAGE_START, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));
        opponent1Panel = new JPanel(new BorderLayout());
        opponent1Panel.setPreferredSize(new Dimension(1, 33));
        boardsPanel.add(opponent1Panel, new GridBagConstraints(
                0, 0,
                1, 1,
                1D, 0.33D,
                GridBagConstraints.PAGE_END, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));
        opponent2Panel = new JPanel(new BorderLayout());
        opponent2Panel.setPreferredSize(new Dimension(1, 33));
        boardsPanel.add(opponent2Panel, new GridBagConstraints(
                0, 1,
                1, 1,
                1D, 0.33D,
                GridBagConstraints.PAGE_END, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));
        mainPanel.add(boardsPanel, new GridBagConstraints(
                1, 0,
                1, 1,
                0.40D, 1D,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setPreferredSize(new Dimension(25, 1));
        mainPanel.add(rightPanel, new GridBagConstraints(
                2, 0,
                1, 1,
                0.25D, 1D,
                GridBagConstraints.LINE_END, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        expertPanel = new JPanel(null);
        expertPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.BLACK, Color.BLACK), "Expert mode", TitledBorder.CENTER, TitledBorder.CENTER));
        expertPanel.setPreferredSize(new Dimension(1, 33));
        rightPanel.add(expertPanel, new GridBagConstraints(
                0, 0,
                1, 1,
                1D, 0.33D,
                GridBagConstraints.PAGE_START, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        cpPanel = new JPanel(new GridBagLayout());
        cpPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.BLACK, Color.BLACK), "Clouds & Professors", TitledBorder.CENTER, TitledBorder.CENTER));
        cpPanel.setPreferredSize(new Dimension(1, 33));
        rightPanel.add(cpPanel, new GridBagConstraints(
                0, 1,
                1, 1,
                1D, 0.33D,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0)
        );

        assistantsPanel = new JPanel(new GridLayout(2, 5, 5, 5));
        assistantsPanel.setPreferredSize(new Dimension(1, 33));
        assistantsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.BLACK, Color.BLACK), "Your assistants", TitledBorder.CENTER, TitledBorder.CENTER));
        rightPanel.add(assistantsPanel, new GridBagConstraints(
                0, 2,
                1, 1,
                1D, 0.33D,
                GridBagConstraints.PAGE_END, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));
    }

    @Override
    public void print() {
        super.print();
        Player me = getPlayerFromName(client.getName());
        Team myTeam = teams.stream().filter(t -> t.isTeamMember(me)).findAny().get();

        List<String> opponents = new ArrayList<>(originalPlayersOrder);
        opponents.remove(me.getName());

        Player opponent1 = getPlayerFromName(opponents.get(0));
        Team opponent1Team = teams.stream().filter(t -> t.isTeamMember(opponent1)).findAny().get();

        Player opponent2 = getPlayerFromName(opponents.get(1));
        Team opponent2Team = teams.stream().filter(t -> t.isTeamMember(opponent2)).findAny().get();

        drawPlayer(playerPanel, me, myTeam.getTowers());
        drawPlayer(opponent1Panel, opponent1, opponent1Team.getTowers());
        drawPlayer(opponent2Panel, opponent2, opponent2Team.getTowers());
        client.getFrame().revalidate();
        client.getFrame().repaint();
    }
}
