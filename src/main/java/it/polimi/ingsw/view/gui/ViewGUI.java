package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.client.ClientGUI;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.view.gui.component.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public abstract class ViewGUI extends View<ClientGUI> {
    private ImageProvider imageProvider;

    protected JPanel mainPanel;
    protected JPanel islandsPanel;
    protected JPanel cpPanel;
    protected JPanel expertPanel;
    protected JPanel assistantsPanel;

    protected JPanel bottomPanel;

    public static ViewGUI create(ClientGUI client, int playerNumber) {
        switch (playerNumber) {
            case 2 -> {
                return new TwoPlayersViewGUI(client);
            }
            case 3 -> {
                return new ThreePlayersViewGUI(client);
            }
            case 4 -> {
                return new FourPlayersViewGUI(client);
            }
            default -> {
                throw new IllegalArgumentException();
            }
        }
    }

    protected ViewGUI(ClientGUI client) {
        this.client = client;
        imageProvider = new ImageProvider();
        client.getFrame().getContentPane().removeAll();

        client.getFrame().getContentPane().setLayout(new GridBagLayout());
        mainPanel = new JPanel();
        mainPanel.setPreferredSize(new Dimension(1, 85));
        client.getFrame().getContentPane().add(mainPanel, new GridBagConstraints(
                0, 0,
                1, 1,
                1D, 0.85D,
                GridBagConstraints.PAGE_START, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setPreferredSize(new Dimension(1, 15));
        client.getFrame().getContentPane().add(bottomPanel, new GridBagConstraints(
                0, 1,
                1, 1,
                1D, 0.15D,
                GridBagConstraints.PAGE_END, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));
        client.getFrame().revalidate();
        client.getFrame().repaint();
    }

    @Override
    public void print() {
        drawIslands();
        drawCloudsAndProfessors();
        drawExpertMode();
        drawAssistants();
        client.getFrame().revalidate();
        client.getFrame().repaint();
    }

    public Player getPlayerFromName(String name){
        for (Player player : playersOrder) {
            if (player.getName().equals(name))
                return player;
        }
        throw new IllegalArgumentException();
    }

    private void drawIslands() {
        islandsPanel.removeAll();

        JPanel[] islandsGrid = new JPanel[5 * 3];
        int[] islandIndexes = new int[]{0, 1, 2, 5, 8, 11, 14, 13, 12, 9, 6, 3};
        int distance = islandIndexes.length / islands.size();

        int i = 0;
        int islandIndex = 0;
        for (Island island : islands) {
            islandsGrid[islandIndexes[i]] = new IslandPanel(island, posMotherNature == islandIndex, islandIndex++, imageProvider);
            i += distance;
        }

        for (i = 0; i < islandsGrid.length; ++i) {
            if (islandsGrid[i] == null) {
                islandsGrid[i] = new JPanel();
            }
        }

        for (JPanel panel : islandsGrid) {
            islandsPanel.add(panel);
        }
    }

    private void drawCloudsAndProfessors() {
        cpPanel.removeAll();

        JPanel cloudsPanel = new JPanel(new GridLayout(1, clouds.size()));
        cloudsPanel.setPreferredSize(new Dimension(1, 85));
        for (Cloud cloud : clouds) {
            cloudsPanel.add(new CloudPanel(cloud.getStudents(), imageProvider));
        }
        cpPanel.add(cloudsPanel, new GridBagConstraints(
                0, 0,
                1, 1,
                1D, 0.85D,
                GridBagConstraints.PAGE_START, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        JPanel professorsPanel = new JPanel(new GridLayout(1, PawnColor.values().length + 4, 10, 5));
        professorsPanel.setPreferredSize(new Dimension(1, 15));
        professorsPanel.add(new JLabel());
        professorsPanel.add(new JLabel());
        for (Professor professor : professors) {
            professorsPanel.add(new JLabel(" ", new DynamicIcon(imageProvider.getProfessor(professor.getColor())), SwingConstants.TRAILING));
        }
        for (int i = 0; i < PawnColor.values().length - professors.size(); ++i) {
            professorsPanel.add(new JLabel());
        }
        professorsPanel.add(new JLabel());
        professorsPanel.add(new JLabel());
        cpPanel.add(professorsPanel, new GridBagConstraints(
                0, 1,
                1, 1,
                1D, 0.15D,
                GridBagConstraints.PAGE_END, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));
    }

    private void drawExpertMode() {
        expertPanel.removeAll();
        if (!expert) {
            expertPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            expertPanel.add(new JLabel("Expert mode is not enabled for this match"));
        } else {
            expertPanel.setLayout(new BorderLayout(10, 10));
            JPanel coinsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JLabel lbl = new JLabel(" ", new DynamicIcon(imageProvider.getCoin()), SwingConstants.TRAILING);
            lbl.setPreferredSize(new Dimension(40, 40));
            coinsPanel.add(lbl);
            coinsPanel.add(new JLabel(Integer.toString(coinReserve)));
            expertPanel.add(coinsPanel, BorderLayout.PAGE_START);

            JPanel charactersPanel = new JPanel(new GridLayout(1, 3, 7, 0));
            for (Character character : characters) {
                CharacterPanel characterPanel = new CharacterPanel(character, imageProvider);
                characterPanel.setToolTipText("<html>Character " + (character.getId() + 1) + "<br><p width=\"350px\">" + character.getDescription() + "</p></html>");
                charactersPanel.add(characterPanel);
            }
            expertPanel.add(charactersPanel, BorderLayout.CENTER);
        }
    }

    protected void drawPlayer(JPanel playerPanel, Player player, List<Tower> towers) {
        playerPanel.removeAll();
        if (playerPanel.getBorder() == null) {
            playerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.BLACK, Color.BLACK), player.getName(), TitledBorder.CENTER, TitledBorder.CENTER));
        }

        JPanel playerInfoPanel = new JPanel(new FlowLayout());
        //Coins
        JLabel lbl = new JLabel(" ", new DynamicIcon(imageProvider.getCoin()), SwingConstants.TRAILING);
        lbl.setPreferredSize(new Dimension(40, 40));
        playerInfoPanel.add(lbl);
        playerInfoPanel.add(new JLabel(Integer.toString(player.getCoins())));

        //Number of assistants
        lbl = new JLabel(" ", new DynamicIcon(imageProvider.getWizard(player.getWizard())), SwingConstants.TRAILING);
        lbl.setPreferredSize(new Dimension(30, 40));
        playerInfoPanel.add(lbl);
        playerInfoPanel.add(new JLabel(Integer.toString(player.getAssistants().size())));

        if (player.getCurrentAssistant() != null) {
            lbl = new JLabel(" ", new DynamicIcon(imageProvider.getAssistant(player.getCurrentAssistant())), SwingConstants.TRAILING);
            lbl.setToolTipText("<html>Current Assistant<br>Value: " + player.getCurrentAssistant().getValue() + "<br>Moves: " + player.getCurrentAssistant().getMoves() + "</html>");
            lbl.setPreferredSize(new Dimension(30, 40));
            playerInfoPanel.add(lbl);
        }

        playerPanel.add(playerInfoPanel, BorderLayout.PAGE_START);
        //School
        JPanel schoolPanel = new SchoolPanel(player, towers, imageProvider);
        playerPanel.add(schoolPanel, BorderLayout.CENTER);
    }

    private void drawAssistants() {
        assistantsPanel.removeAll();
        for (Assistant assistant : assistants) {
            JLabel lbl = new JLabel(" ", new DynamicIcon(imageProvider.getAssistant(assistant)), SwingConstants.TRAILING);
            lbl.setToolTipText("<html>Value: " + assistant.getValue() + "<br>Moves: " + assistant.getMoves() + "</html>");
            assistantsPanel.add(lbl);
        }
    }

    public JPanel getBottomPanel() {
        return bottomPanel;
    }
}
