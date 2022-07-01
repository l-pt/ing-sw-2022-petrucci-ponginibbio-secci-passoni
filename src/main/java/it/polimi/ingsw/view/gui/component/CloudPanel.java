package it.polimi.ingsw.view.gui.component;

import it.polimi.ingsw.view.gui.ImageProvider;
import it.polimi.ingsw.model.Student;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel for drawing cloud cards and students on them.
 */
public class CloudPanel extends JPanel {
    /**
     * Size of the grid layout
     */
    private static final int DIM = 8;
    private ImageProvider imageProvider;

    /**
     * Constructor
     * @param students List of students on the cloud
     * @param imageProvider Image objects provider
     */
    public CloudPanel(List<Student> students, ImageProvider imageProvider) {
        super(new GridLayout(DIM, DIM));
        this.imageProvider = imageProvider;
        JLabel[] grid = new JLabel[DIM * DIM];
        int studentsIndex = 0;
        for (int i = DIM / 2 - 1; i <= DIM / 2 && studentsIndex < students.size(); ++i) {
            for (int j = DIM / 2 - 1; j <= DIM / 2 && studentsIndex < students.size(); ++j) {
                grid[i * DIM + j] = new JLabel(" ", new DynamicIcon(imageProvider.getStudent(students.get(studentsIndex++).getColor())), SwingConstants.TRAILING);
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

    /**
     * @see JPanel#paintComponent(Graphics)
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(imageProvider.getCloud().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH), 0, 0, null);
    }
}
