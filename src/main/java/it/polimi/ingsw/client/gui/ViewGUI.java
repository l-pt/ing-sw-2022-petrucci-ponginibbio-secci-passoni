package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.component.CloudPanel;
import it.polimi.ingsw.client.gui.component.DynamicIcon;
import it.polimi.ingsw.client.gui.component.IslandPanel;
import it.polimi.ingsw.client.gui.component.SchoolPanel;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.protocol.message.UpdateViewMessage;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public abstract class ViewGUI {
    protected ClientGUI client;
    private List<Assistant> assistants;
    private List<Island> islands;
    protected List<Team> teams;
    private List<Player> originalPlayersOrder;
    protected List<Player> playersOrder;
    private int posMotherNature;
    private List<Cloud> clouds;
    private List<Professor> professors;
    private int coinReserve;
    private List<Character> characters;
    private boolean expert;

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
            case 3,4 -> {
                throw new UnsupportedOperationException("Not yet implemented");
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
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gbc.gridy = 0;
        gbc.gridwidth = gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.weightx = 1D;
        gbc.weighty = 0.85D;
        client.getFrame().getContentPane().add(mainPanel, gbc);

        bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(1, 15));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.PAGE_END;
        gbc.weightx = 1D;
        gbc.weighty = 0.15D;
        client.getFrame().getContentPane().add(bottomPanel, gbc);
        client.getFrame().revalidate();
        client.getFrame().repaint();
    }

    public void handleUpdateView(UpdateViewMessage message) {
        islands = message.getIslands();
        teams = message.getTeams();
        playersOrder = message.getPlayersOrder();
        assistants = getPlayerFromName(client.getName()).getAssistants();
        if (originalPlayersOrder == null) {
            originalPlayersOrder = Collections.unmodifiableList(playersOrder);
        }
        posMotherNature = message.getPosMotherNature();
        clouds = message.getClouds();
        professors = message.getProfessors();
        coinReserve = message.getCoinReserve();
        characters = message.getCharacters();
        expert = message.isExpert();

        draw();
    }

    protected void draw() {
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
        islandsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.BLACK, Color.BLACK), "Islands", TitledBorder.CENTER, TitledBorder.CENTER));
        islandsPanel.setLayout(new GridLayout(5, 3));

        JPanel[] islandsGrid = new JPanel[5 * 3];
        int[] islandIndexes = new int[]{0, 1, 2, 5, 8, 11, 14, 13, 12, 9, 6, 3};
        int distance = islandIndexes.length / islands.size();

        int i = 0;
        int islandIndex = 0;
        for (Island island : islands) {
            islandsGrid[islandIndexes[i]] = new IslandPanel(island, islandIndex++, imageProvider);
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
        cpPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.BLACK, Color.BLACK), "Clouds & Professors", TitledBorder.CENTER, TitledBorder.CENTER));
        cpPanel.setLayout(new BoxLayout(cpPanel, BoxLayout.Y_AXIS));

        JPanel cloudsPanel = new JPanel(new GridLayout(1, clouds.size()));
        cloudsPanel.setPreferredSize(new Dimension(1, 85 * 1000));
        for (Cloud cloud : clouds) {
            cloudsPanel.add(new CloudPanel(cloud.getStudents(), imageProvider));
        }
        cpPanel.add(cloudsPanel);

        JPanel professorsPanel = new JPanel(new GridLayout(1, professors.size() + 2, 10, 5));
        professorsPanel.setPreferredSize(new Dimension(1, 15 * 1000));
        professorsPanel.add(new JLabel());
        for (Professor professor : professors) {
            professorsPanel.add(new JLabel(" ", new DynamicIcon(imageProvider.getProfessor(professor.getColor())), SwingConstants.TRAILING));
        }
        professorsPanel.add(new JLabel());
        cpPanel.add(professorsPanel);
    }

    private void drawExpertMode() {
        expertPanel.removeAll();
        expertPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.BLACK, Color.BLACK), "Expert mode", TitledBorder.CENTER, TitledBorder.CENTER));
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
                lbl = new JLabel(" ", new DynamicIcon(imageProvider.getCharacter(character)), SwingConstants.TRAILING);
                lbl.setToolTipText("<html>Character " + (character.getId() + 1) + "<br>" + character.getDescription() + "</html>");
                charactersPanel.add(lbl);
                //TODO draw students and no entry cards on them
            }
            expertPanel.add(charactersPanel, BorderLayout.CENTER);
        }
    }

    protected void drawPlayer(JPanel playerPanel, Player player, List<Tower> towers) {
        playerPanel.removeAll();
        playerPanel.setLayout(new BorderLayout());
        playerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.BLACK, Color.BLACK), player.getName(), TitledBorder.CENTER, TitledBorder.CENTER));

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
        assistantsPanel.setLayout(new GridLayout(2, 5, 5, 5));
        assistantsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.BLACK, Color.BLACK), "Your assistants", TitledBorder.CENTER, TitledBorder.CENTER));
        for (Assistant assistant : assistants) {
            JLabel lbl = new JLabel(" ", new DynamicIcon(imageProvider.getAssistant(assistant)), SwingConstants.TRAILING);
            lbl.setToolTipText("<html>Value: " + assistant.getValue() + "<br>Moves: " + assistant.getMoves() + "</html>");
            assistantsPanel.add(lbl);
        }
    }

    public List<Assistant> getAssistants() {
        return assistants;
    }

    public List<Island> getIslands() {
        return islands;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public List<Player> getOriginalPlayersOrder() {
        return originalPlayersOrder;
    }

    public List<Player> getPlayersOrder() {
        return playersOrder;
    }

    public int getPosMotherNature() {
        return posMotherNature;
    }

    public List<Cloud> getClouds() {
        return clouds;
    }

    public List<Professor> getProfessors() {
        return professors;
    }

    public int getCoinReserve() {
        return coinReserve;
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public boolean isExpert() {
        return expert;
    }

    public JPanel getBottomPanel() {
        return bottomPanel;
    }
}
