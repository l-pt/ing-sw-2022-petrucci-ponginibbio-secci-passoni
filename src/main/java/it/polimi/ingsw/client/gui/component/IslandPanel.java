package it.polimi.ingsw.client.gui.component;

import it.polimi.ingsw.client.gui.ImageProvider;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.model.Tower;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class IslandPanel extends JPanel {
    /**
     * Size of the grid layout
     */
    private static final int DIM = 8;
    private Island island;
    private ImageProvider imageProvider;

    public IslandPanel(Island island, ImageProvider imageProvider) {
        super(new GridLayout(DIM, DIM));
        this.island = island;
        this.imageProvider = imageProvider;
        JLabel[] grid = new JLabel[DIM * DIM];
        List<Object> pieces = new ArrayList<>(island.getStudents().size() + island.getTowers().size());
        pieces.addAll(island.getStudents());
        pieces.addAll(island.getTowers());
        int pieceIndex = 0;
        for (int i = 2; i < DIM - 2 && pieceIndex < pieces.size(); ++i) {
            for (int j = 2; j <= DIM - 2 && pieceIndex < pieces.size(); ++j) {
                Image image;
                if (pieces.get(pieceIndex) instanceof Tower) {
                    image = imageProvider.getTower(((Tower) pieces.get(pieceIndex++)).getColor());
                } else {
                    image = imageProvider.getStudent(((Student) pieces.get(pieceIndex++)).getColor());
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
        g.drawImage(imageProvider.getIsland(Math.max(island.getTowers().size(), 1)).getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH), 0, 0, null);
    }
}
