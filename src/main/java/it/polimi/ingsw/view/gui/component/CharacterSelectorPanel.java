package it.polimi.ingsw.view.gui.component;

import it.polimi.ingsw.model.character.Character;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel used for selecting the character to play during the match. It also has an option to play no characters.
 */
public class CharacterSelectorPanel extends JPanel {
    public static final int SELECTION_NONE = -1;
    /**
     * Maps character number [1-12] to character index [0, characters.size())
     */
    private final Map<Integer, Integer> characterMap = new HashMap<>(3);
    private int selection = SELECTION_NONE;

    /**
     * Constructor
     * @param characters List of characters to select from
     */
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
     * @return {@link CharacterSelectorPanel#SELECTION_NONE} if "Don't use any character" was selected,
     * otherwise the index of the selected character in the interval [0, characters.size()) is returned
     */
    public int getSelection() {
        return selection;
    }
}
