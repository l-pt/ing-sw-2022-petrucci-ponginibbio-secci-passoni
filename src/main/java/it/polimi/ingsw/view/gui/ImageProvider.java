package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.Assistant;
import it.polimi.ingsw.model.PawnColor;
import it.polimi.ingsw.model.TowerColor;
import it.polimi.ingsw.model.Wizard;
import it.polimi.ingsw.model.character.Character;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The ImageProvider class provides methods to get {@link BufferedImage} objects of the game elements, like students,
 * assistants, professors, etc.
 * Images are cached to avoid reading them from disk over and over again.
 */
public class ImageProvider {
    private BufferedImage school;
    private BufferedImage island;
    private final Map<PawnColor, BufferedImage> professors;
    private final Map<PawnColor, BufferedImage> students;
    private final Map<TowerColor, BufferedImage> towers;
    private BufferedImage cloud;
    private final BufferedImage[] assistants;
    private final BufferedImage[] characters;
    private final Map<Wizard, BufferedImage> wizards;
    private BufferedImage coin;
    private BufferedImage motherNature;
    private BufferedImage noEntry;

    public ImageProvider() {
        professors = new HashMap<>(PawnColor.values().length);
        students = new HashMap<>(PawnColor.values().length);
        towers = new HashMap<>(TowerColor.values().length);
        assistants = new BufferedImage[10];
        characters = new BufferedImage[12];
        wizards = new HashMap<>(Wizard.values().length);
    }

    /**
     * Load an image from the resources directory
     * @param path Path of the image relative to the resources directory
     * @return A {@link BufferedImage} object containing the image data
     */
    private BufferedImage getImage(String path) {
        BufferedImage res;
        try {
            res = ImageIO.read(getClass().getResource(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    /**
     * getSchool()
     * @return The school board image
     */
    public BufferedImage getSchool() {
        if (school == null) {
            school = getImage("/Plancia_DEF.png");
        }
        return school;
    }

    /**
     * getIsland()
     * @return The island card image
     */
    public BufferedImage getIsland() {
        if (island == null) {
            island = getImage("/island/" + 1 + ".png");
        }
        return island;
    }

    /**
     * getProfessor()
     * @param color Color of the professor
     * @return Professor image with the given color
     */
    public BufferedImage getProfessor(PawnColor color) {
        BufferedImage res = professors.get(color);
        if (res == null) {
            professors.put(color, res = getImage("/professors/" + color.name() + ".png"));
        }
        return res;
    }

    /**
     * getStudent()
     * @param color Student color
     * @return Student image with the given color
     */
    public BufferedImage getStudent(PawnColor color) {
        BufferedImage res = students.get(color);
        if (res == null) {
            students.put(color, res = getImage("/students/" + color.name() + ".png"));
        }
        return res;
    }

    /**
     * getTower()
     * @param color Tower color
     * @return Tower image with the given color
     */
    public BufferedImage getTower(TowerColor color) {
        BufferedImage res = towers.get(color);
        if (res == null) {
            towers.put(color, res = getImage("/towers/" + color.name() + ".png"));
        }
        return res;
    }

    /**
     * getCloud()
     * @return Cloud card image
     */
    public BufferedImage getCloud() {
        if (cloud == null) {
            cloud = getImage("/cloud_card.png");
        }
        return cloud;
    }

    /**
     * getAssistant()
     * @param assistant {@link Assistant} object
     * @return Image for the given assistant
     */
    public BufferedImage getAssistant(Assistant assistant) {
        if (assistants[assistant.getValue() - 1] == null) {
            assistants[assistant.getValue() - 1] = getImage("/assistants/" + assistant.getValue() + ".png");
        }
        return assistants[assistant.getValue() - 1];
    }

    /**
     * getCharacter()
     * @param character {@link Character} object
     * @return Image for the given character
     */
    public BufferedImage getCharacter(Character character) {
        if (characters[character.getId()] == null) {
            characters[character.getId()] = getImage("/characters/" + character.getId() + ".jpg");
        }
        return characters[character.getId()];
    }

    /**
     * getWizard()
     * @param wizard {@link Wizard} object
     * @return Image for the given wizard
     */
    public BufferedImage getWizard(Wizard wizard) {
        BufferedImage res = wizards.get(wizard);
        if (res == null) {
            wizards.put(wizard, res = getImage("/assistants/" + wizard.name() + ".png"));
        }
        return res;
    }

    /**
     * getCoin()
     * @return Coin image
     */
    public BufferedImage getCoin() {
        if (coin == null) {
            coin = getImage("/Moneta_base.png");
        }
        return coin;
    }

    /**
     * getMotherNature()
     * @return Mother nature image
     */
    public BufferedImage getMotherNature() {
        if (motherNature == null) {
            motherNature = getImage("/mother_nature.png");
        }
        return motherNature;
    }

    /**
     * getNoEntry()
     * @return No Entry card image
     */
    public BufferedImage getNoEntry() {
        if (noEntry == null) {
            noEntry = getImage("/noentry.png");
        }
        return noEntry;
    }
}
