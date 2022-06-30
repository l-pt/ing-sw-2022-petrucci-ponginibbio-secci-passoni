package it.polimi.ingsw.view.gui.component;

import it.polimi.ingsw.view.gui.ImageProvider;
import it.polimi.ingsw.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Panel for the school board. This contains  entrance students, table students and team towers.
 */
public class SchoolPanel extends JPanel {
    private static final int rows = 19;
    private static final int columns = 53;
    private ImageProvider imageProvider;

    public SchoolPanel(Player player, List<Tower> towers, ImageProvider imageProvider) {
        super(new GridBagLayout());
        this.imageProvider = imageProvider;

        Map<PawnColor, List<Integer>> studentsPosMap = Map.of(
                PawnColor.GREEN, List.of(43, 45, 47, 49, 51, 53, 55, 57, 59, 61),
                PawnColor.RED, List.of(85, 87, 89, 91, 93, 95, 97, 99, 101, 103),
                PawnColor.YELLOW, List.of(127, 129, 131, 133, 135, 137, 139, 141, 143, 145),
                PawnColor.PINK, List.of(169, 171, 173, 175, 177, 179, 181, 183, 185, 187),
                PawnColor.BLUE, List.of(211, 213, 215, 217, 219, 221, 223, 225, 227, 229)
        );
        Map<PawnColor, Integer> profPosMap = Map.of(
                PawnColor.GREEN, 7,
                PawnColor.RED, 13,
                PawnColor.YELLOW, 19,
                PawnColor.PINK, 25,
                PawnColor.BLUE, 31
        );
        int[] entrancePos = new int[]{51, 53, 41, 43, 31, 33, 21, 23, 11, 13};
        int[] towersPos = new int[]{25, 27, 29, 41, 43, 45, 58, 60};

        //Entrance area from x=0 to x=534
        //Tables to x=2286
        //Professors to x=2600
        //Towers to x=3352

        //Entrance
        JPanel entrancePanel = new JPanel(new GridLayout(12, 5));
        entrancePanel.setOpaque(false);
        entrancePanel.setPreferredSize(new Dimension(16, 1));
        JLabel[] entranceGrid = new JLabel[12 * 5];
        int i = 0;
        for (Student student : player.getSchool().getEntrance()) {
            entranceGrid[entrancePos[i++]] = new JLabel(new DynamicIcon(imageProvider.getStudent(student.getColor())));
        }
        for (i = 0; i < entranceGrid.length; ++i) {
            if (entranceGrid[i] == null) {
                entranceGrid[i] = new JLabel();
            }
        }
        for (JLabel lbl : entranceGrid) {
            entrancePanel.add(lbl);
        }
        add(entrancePanel, new GridBagConstraints(
                0, 0,
                1, 1,
                534D / 3352D, 1.0D,
                GridBagConstraints.PAGE_START, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        //Tables
        JPanel tablesPanel = new JPanel(new GridLayout(13, 21));
        tablesPanel.setOpaque(false);
        tablesPanel.setPreferredSize(new Dimension(52, 1));
        JLabel[] tablesGrid = new JLabel[13 * 21];
        for (Map.Entry<PawnColor, List<Student>> entry : player.getSchool().getTables().entrySet()) {
            i = 0;
            for (Student student : entry.getValue()) {
                JLabel studentLabel = new JLabel(new DynamicIcon(imageProvider.getStudent(student.getColor())));
                studentLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                tablesGrid[studentsPosMap.get(entry.getKey()).get(i++)] = studentLabel;
            }
        }
        for (i = 0; i < tablesGrid.length; ++i) {
            if (tablesGrid[i] == null) {
                tablesGrid[i] = new JLabel();
            }
        }
        for (JLabel lbl : tablesGrid) {
            tablesPanel.add(lbl);
        }
        add(tablesPanel, new GridBagConstraints(
                1, 0,
                1, 1,
                (2286D - 534D) / 3352D, 1.0D,
                GridBagConstraints.PAGE_START, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        //Professors
        JPanel profPanel = new JPanel(new GridLayout(13, 3));
        profPanel.setOpaque(false);
        profPanel.setPreferredSize(new Dimension(9, 1));
        JLabel[] profGrid = new JLabel[13 * 3];
        for (Professor professor : player.getSchool().getProfessors()) {
            profGrid[profPosMap.get(professor.getColor())] = new JLabel(new DynamicIcon(imageProvider.getProfessor(professor.getColor())));
        }
        for (i = 0; i < profGrid.length; ++i) {
            if (profGrid[i] == null) {
                profGrid[i] = new JLabel();
            }
        }
        for (JLabel lbl : profGrid) {
            profPanel.add(lbl);
        }
        add(profPanel, new GridBagConstraints(
                2, 0,
                1, 1,
                (2600D - 2286D) / 3352D, 1.0D,
                GridBagConstraints.PAGE_START, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        //Towers
        JPanel towersPanel = new JPanel(new GridLayout(10, 8));
        towersPanel.setOpaque(false);
        towersPanel.setPreferredSize(new Dimension(22, 1));
        JLabel[] towerGrid = new JLabel[10 * 8];
        i = 0;
        for (Tower tower : towers) {
            towerGrid[towersPos[i++]] = new JLabel(new DynamicIcon(imageProvider.getTower(tower.getColor())));
        }
        for (i = 0; i < towerGrid.length; ++i) {
            if (towerGrid[i] == null) {
                towerGrid[i] = new JLabel();
            }
        }
        for (JLabel lbl : towerGrid) {
            towersPanel.add(lbl);
        }
        add(towersPanel, new GridBagConstraints(
                3, 0,
                1, 1,
                (3352D - 2600D) / 3352D, 1.0D,
                GridBagConstraints.PAGE_START, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(imageProvider.getSchool().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH), 0, 0, null);
    }
}
