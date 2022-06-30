package it.polimi.ingsw.model;

import java.util.List;

public class InfluenceCalculationPolicy {
    private boolean countTowers;
    private PawnColor excludedColor;

    public InfluenceCalculationPolicy() {
        countTowers = true;
        excludedColor = null;
    }

    /**
     * getCountTowers()
     * @return True if the towers will be counted for the islands influence
     */
    public boolean getCountTowers() {
        return countTowers;
    }

    /**
     * setCountTowers(countTowers)
     * @param countTowers True if the towers will be counted for the islands influence
     */
    public void setCountTowers(boolean countTowers) {
        this.countTowers = countTowers;
    }

    /**
     * getExcludedColor()
     * @return The student color that won't be counted for the islands influence
     */
    public PawnColor getExcludedColor() {
        return excludedColor;
    }

    /**
     * setExcludedColor(excludedColor)
     * @param excludedColor The student color that won't be counted for the islands influence
     */
    public void setExcludedColor(PawnColor excludedColor) {
        this.excludedColor = excludedColor;
    }

    /**
     * Gets the number of towers on an island with the same color of the player towers
     * @param player Player
     * @param towers Towers on an island
     * @return The number of towers on an island with the same color of the player towers
     */
    public int getTowersInfluence(Player player, List<Tower> towers) {
        int result = 0;
        if (countTowers) {
            for (Tower tower : towers) {
                if (tower.getColor() == player.getTowerColor()) {
                    ++result;
                }
            }
        }
        return result;
    }

    /**
     * Gets the number of students on an island with the same color of the player professors
     * @param player Player
     * @param students Students on an island
     * @return The number of students on an island with the same color of the player professors
     */
    public int getStudentsInfluence(Player player, List<Student> students) {
        int result = 0;
        for (Student student : students) {
            if (student.getColor() != excludedColor) {
                for (Professor professor : player.getSchool().getProfessors()) {
                    if (professor.getColor() == student.getColor()) {
                        ++result;
                        break;
                    }
                }
            }
        }
        return result;
    }
}
