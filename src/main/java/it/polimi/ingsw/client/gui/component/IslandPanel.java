package it.polimi.ingsw.client.gui.component;

import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.model.Tower;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IslandPanel extends JPanel {
    /**
     * Size of the grid layout
     */
    private static final int DIM = 8;
    private static Image islandImage;

    public IslandPanel(Island island) {
        super(new GridLayout(DIM, DIM));
        if (islandImage == null) {
            try {
                islandImage = ImageIO.read(getClass().getResource("/island/1.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            };
        }
        JLabel[] grid = new JLabel[DIM * DIM];
        List<Object> pieces = new ArrayList<>(island.getStudents().size() + island.getTowers().size());
        pieces.addAll(island.getStudents());
        pieces.addAll(island.getTowers());
        int pieceIndex = 0;
        for (int i = 2; i < DIM - 2 && pieceIndex < pieces.size(); ++i) {
            for (int j = 2; j <= DIM - 2 && pieceIndex < pieces.size(); ++j) {
                Image image;
                try {
                    if (pieces.get(pieceIndex) instanceof Tower) {
                        image = ImageIO.read(getClass().getResource("/towers/" + ((Tower) pieces.get(pieceIndex++)).getColor().name() + ".png"));
                    } else {
                        image = ImageIO.read(getClass().getResource("/students/" + ((Student) pieces.get(pieceIndex++)).getColor().name() + ".png"));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                };
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
        g.drawImage(islandImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH), 0, 0, null);
    }
}
