package it.polimi.ingsw.view.gui.component;

import it.polimi.ingsw.view.gui.ImageProvider;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.model.Tower;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for drawing island cards and character, towers on them.
 */
public class IslandPanel extends JPanel {
    /**
     * Size of the grid layout
     */
    private static final int DIM = 8;
    private int islandSize;
    private ImageProvider imageProvider;

    public IslandPanel(Island island, boolean motherNature, int index, ImageProvider imageProvider) {
        super(new GridLayout(DIM, DIM));
        this.islandSize = Math.max(island.getTowers().size(), 1);
        this.imageProvider = imageProvider;
        String title = "Island " + (index + 1);
        if (islandSize > 1) {
            title = title + " (x" + islandSize + ")";
        }
        if (island.getNoEntry() > 0) {
            title = title + " (" + island.getNoEntry() + " no entry)";
        }
        setBorder(BorderFactory.createTitledBorder(title));
        JLabel[] grid = new JLabel[DIM * DIM];
        List<Object> pieces = new ArrayList<>(island.getStudents().size() + island.getTowers().size());
        pieces.addAll(island.getStudents());
        pieces.addAll(island.getTowers());
        int pieceIndex = 0;
        rows: for (int i = 2; i <= DIM - 2; ++i) {
            for (int j = 2; j <= DIM - 2; ++j) {
                if (pieceIndex >= pieces.size() && !motherNature) {
                    break rows;
                }
                Image image;
                if (motherNature) {
                    image = imageProvider.getMotherNature();
                    motherNature = false;
                } else {
                    if (pieces.get(pieceIndex) instanceof Tower) {
                        image = imageProvider.getTower(((Tower) pieces.get(pieceIndex++)).getColor());
                    } else {
                        image = imageProvider.getStudent(((Student) pieces.get(pieceIndex++)).getColor());
                    }
                }
                grid[i * DIM + j] = new JLabel(" ", new DynamicIcon(image), SwingConstants.TRAILING);
            }
        }
        for (int i = 0; i < DIM * DIM; ++i) {
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
        g.drawImage(imageProvider.getIsland(islandSize).getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH), 0, 0, null);
    }
}
