package it.polimi.ingsw.model;

import java.util.List;

public class InfluenceCalculationPolicy {
    private boolean countTowers;
    private PawnColor excludedColor;

    public InfluenceCalculationPolicy() {
        countTowers = true;
        excludedColor = null;
    }

    public void setCountTowers(boolean countTowers) {
        this.countTowers = countTowers;
    }

    public void setExcludedColor(PawnColor excludedColor) {
        this.excludedColor = excludedColor;
    }

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
