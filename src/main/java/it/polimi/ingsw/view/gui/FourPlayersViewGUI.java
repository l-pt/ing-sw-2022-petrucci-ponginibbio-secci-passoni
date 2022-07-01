package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.client.ClientGUI;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Team;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FourPlayersViewGUI extends ViewGUI {
    /**
     * Panel with player information (board, coins, number of assistants, last played assistant)
     */
    private final JPanel playerPanel;
    /**
     * Panel with teammate information (board, coins, number of assistants, last played assistant)
     */
    private final JPanel teamMatePanel;
    /**
     * Panel with opponent information (board, coins, number of assistants, last played assistant)
     */
    private final JPanel opponent1Panel;
    /**
     * Panel with opponent information (board, coins, number of assistants, last played assistant)
     */
    private final JPanel opponent2Panel;

    /**
     * @see ViewGUI#ViewGUI
     */
    protected FourPlayersViewGUI(ClientGUI client) {
        super(client);
        mainPanel.setLayout(new GridBagLayout());

        islandsPanel = new JPanel(new GridLayout(5, 3));
        islandsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.BLACK, Color.BLACK), "Islands", TitledBorder.CENTER, TitledBorder.CENTER));
        islandsPanel.setPreferredSize(new Dimension(35, 1));
        mainPanel.add(islandsPanel, new GridBagConstraints(
                0, 0,
                1, 1,
                0.35D, 1D,
                GridBagConstraints.LINE_START, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        JPanel rightPanel = new JPanel(new GridBagLayout());
        mainPanel.add(rightPanel, new GridBagConstraints(
                1, 0,
                1, 1,
                0.65D, 1D,
                GridBagConstraints.LINE_END, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        JPanel boardsPanel = new JPanel(new GridBagLayout());
        boardsPanel.setPreferredSize(new Dimension(1, 75));
        playerPanel = new JPanel(new BorderLayout());
        boardsPanel.add(playerPanel, new GridBagConstraints(
                0, 1,
                1, 1,
                0.5D, 0.5D,
                GridBagConstraints.PAGE_START, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));
        teamMatePanel = new JPanel(new BorderLayout());
        boardsPanel.add(teamMatePanel, new GridBagConstraints(
                1, 0,
                1, 1,
                0.5D, 0.5D,
                GridBagConstraints.PAGE_START, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));
        opponent1Panel = new JPanel(new BorderLayout());
        boardsPanel.add(opponent1Panel, new GridBagConstraints(
                0, 0,
                1, 1,
                0.5D, 0.5D,
                GridBagConstraints.PAGE_END, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));
        opponent2Panel = new JPanel(new BorderLayout());
        boardsPanel.add(opponent2Panel, new GridBagConstraints(
                1, 1,
                1, 1,
                0.5D, 0.5D,
                GridBagConstraints.PAGE_END, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));
        rightPanel.add(boardsPanel, new GridBagConstraints(
                0, 1,
                1, 1,
                1D, 0.75D,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setPreferredSize(new Dimension(25, 1));
        rightPanel.add(topPanel, new GridBagConstraints(
                0, 0,
                1, 1,
                1D, 0.25D,
                GridBagConstraints.PAGE_START, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        expertPanel = new JPanel(null);
        expertPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.BLACK, Color.BLACK), "Expert mode", TitledBorder.CENTER, TitledBorder.CENTER));
        expertPanel.setPreferredSize(new Dimension(33, 1));
        topPanel.add(expertPanel, new GridBagConstraints(
                0, 0,
                1, 1,
                0.33D, 1D,
                GridBagConstraints.LINE_START, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        cpPanel = new JPanel(new GridBagLayout());
        cpPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.BLACK, Color.BLACK), "Clouds & Professors", TitledBorder.CENTER, TitledBorder.CENTER));
        cpPanel.setPreferredSize(new Dimension(33, 1));
        topPanel.add(cpPanel, new GridBagConstraints(
                1, 0,
                1, 1,
                0.33D, 1D,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0)
        );

        assistantsPanel = new JPanel(new GridLayout(2, 5, 5, 5));
        assistantsPanel.setPreferredSize(new Dimension(33, 1));
        assistantsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.BLACK, Color.BLACK), "Your assistants", TitledBorder.CENTER, TitledBorder.CENTER));
        topPanel.add(assistantsPanel, new GridBagConstraints(
                2, 0,
                1, 1,
                0.33D, 1D,
                GridBagConstraints.LINE_END, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));
    }

    /**
     * Update the players panels and call {@link ViewGUI#print}
     */
    @Override
    public void print() {
        super.print();
        Player me = getPlayerFromName(client.getName());
        Team myTeam = teams.stream().filter(t -> t.isTeamMember(me)).findAny().get();

        Player teamMate = myTeam.getPlayers().stream().filter(p -> !p.getName().equals(client.getName())).findAny().get();

        List<String> opponents = new ArrayList<>(originalPlayersOrder);
        opponents.remove(me.getName());
        opponents.remove(teamMate.getName());

        Player opponent1 = getPlayerFromName(opponents.get(0));
        Team opponentTeam = teams.stream().filter(t -> t.isTeamMember(opponent1)).findAny().get();

        Player opponent2 = getPlayerFromName(opponents.get(1));

        drawPlayer(playerPanel, me, myTeam.getTowers(), "You");
        drawPlayer(teamMatePanel, teamMate, Collections.emptyList(), "Team Mate");
        drawPlayer(opponent1Panel, opponent1, opponentTeam.getTowers(), "Opponent Team");
        drawPlayer(opponent2Panel, opponent2, Collections.emptyList(), "Opponent Team");
        client.getFrame().revalidate();
        client.getFrame().repaint();
    }
}
