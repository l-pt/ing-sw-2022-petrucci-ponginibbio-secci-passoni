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
        super(new GridLayout(rows, columns));
        this.imageProvider = imageProvider;

        Map<PawnColor, List<Integer>> studentsPosMap = Map.of(
                PawnColor.GREEN, List.of(169, 171, 174, 177, 179, 182, 185, 187, 190, 193),
                PawnColor.RED, List.of(328, 330, 333, 336, 338, 341, 344, 346, 349, 352),
                PawnColor.YELLOW, List.of(487, 489, 492, 495, 497, 500, 503, 505, 508, 511),
                PawnColor.PINK, List.of(646, 648, 651, 654, 656, 659, 662, 664, 667, 670),
                PawnColor.BLUE, List.of(805, 807, 810, 813, 815, 818, 821, 823, 826, 829)
        );
        Map<PawnColor, Integer> profPosMap = Map.of(
                PawnColor.GREEN, 198,
                PawnColor.RED, 357,
                PawnColor.YELLOW, 516,
                PawnColor.PINK, 675,
                PawnColor.BLUE, 834
        );
        int[] entrancePos = new int[]{372, 375, 478, 481, 584, 587, 690, 693, 796, 799};
        int[] towersPos = new int[]{469, 473, 575, 579, 681, 685, 787, 791};

        JLabel[] grid = new JLabel[rows * columns];

        int i = 0;
        for (Student student : player.getSchool().getEntrance()) {
            grid[entrancePos[i++]] = new JLabel(new DynamicIcon(imageProvider.getStudent(student.getColor())));
        }

        i = 0;
        for (Tower tower : towers) {
            grid[towersPos[i++]] = new JLabel(new DynamicIcon(imageProvider.getTower(tower.getColor())));
        }

        for (Professor professor : player.getSchool().getProfessors()) {
            JLabel profLabel = new JLabel(new DynamicIcon(imageProvider.getProfessor(professor.getColor())));
            profLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            grid[profPosMap.get(professor.getColor())] = profLabel;
        }

        for (Map.Entry<PawnColor, List<Student>> entry : player.getSchool().getTables().entrySet()) {
            i = 0;
            for (Student student : entry.getValue()) {
                JLabel studentLabel = new JLabel(new DynamicIcon(imageProvider.getStudent(entry.getKey())));
                studentLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                grid[studentsPosMap.get(entry.getKey()).get(i++)] = studentLabel;
            }
        }

        for (i = 0; i < grid.length; ++i) {
            if (grid[i] == null) {
                grid[i] = new JLabel();
            }
        }
        for (JLabel lbl : grid) {
            add(lbl);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(imageProvider.getSchool().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH), 0, 0, null);
    }
}
