package it.polimi.ingsw.view.gui.component;

import it.polimi.ingsw.view.gui.ImageProvider;
import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.model.character.StudentCharacter;
import it.polimi.ingsw.model.character.impl.Character5;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Character card panel. Also draws students and no entry tiles when appropriate.
 */
public class CharacterPanel extends JPanel {
    private final Character character;
    private final ImageProvider imageProvider;

    /**
     * Constructor
     * @param character Character to draw
     * @param imageProvider Image objects provider
     */
    public CharacterPanel(Character character, ImageProvider imageProvider) {
        super(new GridBagLayout());
        this.character = character;
        this.imageProvider = imageProvider;
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        add(topPanel, new GridBagConstraints(
                0, 0,
                1, 1,
                1D, 0.6D,
                GridBagConstraints.PAGE_START, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        JPanel bottomPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        bottomPanel.setOpaque(false);
        BufferedImage[] images;
        if (character instanceof StudentCharacter) {
            images = ((StudentCharacter) character).getStudents().stream().map(s -> imageProvider.getStudent(s.getColor())).toArray(BufferedImage[]::new);
        } else if (character instanceof Character5) {
            images = IntStream.range(0, ((Character5) character).getNoEntry()).mapToObj(i -> imageProvider.getNoEntry()).toArray(BufferedImage[]::new);
        } else {
            images = new BufferedImage[0];
        }
        if (character.getCoin()) {
            images = Arrays.copyOf(images, images.length + 1);
            images[images.length - 1] = imageProvider.getCoin();
        }

        for (BufferedImage image : images) {
            bottomPanel.add(new JLabel(" ", new DynamicIcon(image), SwingConstants.TRAILING));
        }
        for (int i = 0; i < 8 - images.length; ++i) {
            bottomPanel.add(new JLabel());
        }

        add(bottomPanel, new GridBagConstraints(
                0, 1,
                1, 1,
                1D, 0.4D,
                GridBagConstraints.PAGE_END, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));
    }

    /**
     * @see JPanel#paintComponent(Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(imageProvider.getCharacter(character).getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH), 0, 0, null);
    }
}
