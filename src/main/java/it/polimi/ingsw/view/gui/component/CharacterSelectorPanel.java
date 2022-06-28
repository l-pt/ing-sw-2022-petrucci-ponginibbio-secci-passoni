package it.polimi.ingsw.view.gui.component;

import it.polimi.ingsw.model.character.Character;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CharacterSelectorPanel extends JPanel {
    public static final int SELECTION_NONE = -1;
    //Maps character number [1-12] to character index [0-2]
    private Map<Integer, Integer> characterMap = new HashMap<>(3);
    private int selection = SELECTION_NONE;

    public CharacterSelectorPanel(List<Character> characters) {
        super();
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.addItem("Don't use any character");
        for (int i = 0; i < characters.size(); ++i) {
            comboBox.addItem("Character " + (characters.get(i).getId() + 1));
            characterMap.put(characters.get(i).getId() + 1, i);
        }
        comboBox.addActionListener(actionEvent -> {
            String sel = (String) comboBox.getSelectedItem();
            if (sel.equals("Don't use any character")) {
                selection = SELECTION_NONE;
            } else {
                selection = characterMap.get(Integer.parseInt(sel.split(" ")[1]));
            }
        });
        add(comboBox);
    }

    /**
     * @return SELECTION_NONE if "Don't use any character" was selected,
     * otherwise the index of the selected character [0, characters.size()) is returned
     */
    public int getSelection() {
        return selection;
    }
}
