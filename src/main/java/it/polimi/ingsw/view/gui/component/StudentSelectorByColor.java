package it.polimi.ingsw.view.gui.component;

import it.polimi.ingsw.model.PawnColor;
import it.polimi.ingsw.model.Student;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel for selecting from a list of students.
 */
public class StudentSelectorByColor extends JPanel {
    private final Map<PawnColor, Integer> selections = new HashMap<>(PawnColor.values().length);

    /**
     * Constructor
     * @param students List of student to select from
     * @param max Maximum number of selectable students
     * @param title Title of the panel
     */
    public StudentSelectorByColor(List<Student> students, int max, String title) {
        super();
        add(new JLabel(title));
        Map<PawnColor, Integer> studentsMap = new HashMap<>(PawnColor.values().length);
        for (Student s : students) {
            studentsMap.put(s.getColor(), studentsMap.getOrDefault(s.getColor(), 0) + 1);
        }
        for (Map.Entry<PawnColor, Integer> entry : studentsMap.entrySet()) {
            JPanel entryPanel = new JPanel();
            entryPanel.add(new JLabel(entry.getKey().name() + ": "));
            JComboBox<Integer> comboBox = new JComboBox<>();
            for (int i = 0, j = Math.min(entry.getValue(), max); i <= j; ++i) {
                comboBox.addItem(i);
            }
            comboBox.addActionListener(actionEvent -> {
                if (((Integer) comboBox.getSelectedItem()) > 0) {
                    selections.put(entry.getKey(), (Integer) comboBox.getSelectedItem());
                }
            });
            entryPanel.add(comboBox);
            add(entryPanel);
        }
    }

    /**
     * Get the number of selected students for each color
     * @return A {@link Map} that maps a {@link PawnColor} to the number of selected students of that color
     */
    public Map<PawnColor, Integer> getSelection() {
        return selections;
    }
}
