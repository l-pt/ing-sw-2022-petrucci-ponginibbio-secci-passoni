package it.polimi.ingsw.client.gui.component;

import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.Student;

import javax.swing.*;
import java.util.List;

public class EntranceStudentSelectorPanel extends JPanel {
    public static final int SELECTION_NONE = -2;
    public static final int SELECTION_TABLE = -1;
    private Student student;
    private int selection = SELECTION_NONE;

    public EntranceStudentSelectorPanel(Student student, List<Island> islands) {
        super();
        this.student = student;
        add(new JLabel(student.getColor().name() + " Student: "));
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.addItem("Don't move");
        comboBox.addItem("Table");
        for (int i = 0; i < islands.size(); ++i) {
            comboBox.addItem("Island " + (i + 1));
        }

        comboBox.addActionListener(actionEvent -> {
            String sel = (String) comboBox.getSelectedItem();
            selection = switch (sel) {
                case "Don't move" -> SELECTION_NONE;
                case "Table" -> SELECTION_TABLE;
                default -> Integer.parseInt(sel.split(" ")[1]) - 1;
            };
        });
        add(comboBox);
    }

    public Student getStudent() {
        return student;
    }

    /**
     * @return SELECTION_NONE if "Don't move" was selected, SELECTION_TABLE if "Table" was selected,
     * otherwise the index of the selected island [0, islands.size()) is returned
     */
    public int getSelection() {
        return selection;
    }
}
