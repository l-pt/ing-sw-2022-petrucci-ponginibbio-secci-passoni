package it.polimi.ingsw.client.gui.component;

import it.polimi.ingsw.model.Student;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class CloudPanel extends JPanel {
    /**
     * Size of the grid layout
     */
    private static final int DIM = 8;
    private static Image cloudImage;

    public CloudPanel(List<Student> students) {
        super(new GridLayout(DIM, DIM));
        if (cloudImage == null) {
            try {
                cloudImage = ImageIO.read(getClass().getResource("/cloud_card.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            };
        }
        JLabel[] grid = new JLabel[DIM * DIM];
        for (int i = 0; i < DIM; ++i) {
            for (int j = 0; j < DIM; ++j) {
                grid[i * DIM + j] = new JLabel();
            }
        }
        int studentsIndex = 0;
        for (int i = DIM / 2 - 1; i <= DIM / 2 && studentsIndex < students.size(); ++i) {
            for (int j = DIM / 2 - 1; j <= DIM / 2 && studentsIndex < students.size(); ++j) {
                Image studentImage;
                try {
                    studentImage = ImageIO.read(getClass().getResource("/students/" + students.get(studentsIndex++).getColor().name() + ".png"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                };
                grid[i * DIM + j] = new JLabel(" ", new DynamicIcon(studentImage), SwingConstants.TRAILING);
            }
        }
        for (JLabel lbl : grid) {
            add(lbl);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(cloudImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH), 0, 0, null);
    }
}
