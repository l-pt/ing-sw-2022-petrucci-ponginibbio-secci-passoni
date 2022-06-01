package it.polimi.ingsw.client.gui;

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

public class ImageProvider {
    private BufferedImage school;
    private BufferedImage[] island;
    private Map<PawnColor, BufferedImage> professors;
    private Map<PawnColor, BufferedImage> students;
    private Map<TowerColor, BufferedImage> towers;
    private BufferedImage cloud;
    private BufferedImage[] assistants;
    private BufferedImage[] characters;
    private Map<Wizard, BufferedImage> wizards;
    private BufferedImage coin;
    private BufferedImage motherNature;

    public ImageProvider() {
        island = new BufferedImage[3];
        professors = new HashMap<>(PawnColor.values().length);
        students = new HashMap<>(PawnColor.values().length);
        towers = new HashMap<>(TowerColor.values().length);
        assistants = new BufferedImage[10];
        characters = new BufferedImage[12];
        wizards = new HashMap<>(Wizard.values().length);
    }

    private BufferedImage getImage(String path) {
        BufferedImage res;
        try {
            res = ImageIO.read(getClass().getResource(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public BufferedImage getSchool() {
        if (school == null) {
            school = getImage("/Plancia_DEF.png");
        }
        return school;
    }

    public BufferedImage getIsland(int dim) {
        if (dim < 1 || dim > 3) {
            throw new IllegalArgumentException("dim must be between 1 and 3");
        }
        if (island[dim - 1] == null) {
            //TODO support dim = 2 and dim = 3
            island[dim - 1] = getImage("/island/" + 1 + ".png");
        }
        return island[dim - 1];
    }

    public BufferedImage getProfessor(PawnColor color) {
        BufferedImage res = professors.get(color);
        if (res == null) {
            professors.put(color, res = getImage("/professors/" + color.name() + ".png"));
        }
        return res;
    }

    public BufferedImage getStudent(PawnColor color) {
        BufferedImage res = students.get(color);
        if (res == null) {
            students.put(color, res = getImage("/students/" + color.name() + ".png"));
        }
        return res;
    }

    public BufferedImage getTower(TowerColor color) {
        BufferedImage res = towers.get(color);
        if (res == null) {
            towers.put(color, res = getImage("/towers/" + color.name() + ".png"));
        }
        return res;
    }

    public BufferedImage getCloud() {
        if (cloud == null) {
            cloud = getImage("/cloud_card.png");
        }
        return cloud;
    }

    public BufferedImage getAssistant(Assistant assistant) {
        if (assistants[assistant.getValue() - 1] == null) {
            assistants[assistant.getValue() - 1] = getImage("/assistants/" + assistant.getValue() + ".png");
        }
        return assistants[assistant.getValue() - 1];
    }

    public BufferedImage getCharacter(Character character) {
        if (characters[character.getId()] == null) {
            characters[character.getId()] = getImage("/characters/" + character.getId() + ".jpg");
        }
        return characters[character.getId()];
    }

    public BufferedImage getWizard(Wizard wizard) {
        BufferedImage res = wizards.get(wizard);
        if (res == null) {
            wizards.put(wizard, res = getImage("/assistants/" + wizard.name() + ".png"));
        }
        return res;
    }

    public BufferedImage getCoin() {
        if (coin == null) {
            coin = getImage("/Moneta_base.png");
        }
        return coin;
    }

    public BufferedImage getMotherNature() {
        if (motherNature == null) {
            motherNature = getImage("/mother_nature.png");
        }
        return motherNature;
    }
}
