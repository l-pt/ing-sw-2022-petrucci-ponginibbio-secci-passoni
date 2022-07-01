package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.character.StudentCharacter;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.character.StudentMapCharacter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Character7 extends StudentCharacter implements StudentMapCharacter {
    public Character7() {
        super(6, 1, "You may take up to 3 students from this card and replace them " +
                "with the same number of students from your entrance.");
    }

    /**
     * Uses the ability: "You may take up to 3 students from this card and replace them
     * with the same number of students from your entrance."
     * @param match Match
     * @param playerName The username of a player
     * @param studentsInMap Map of student that are going from the entrance of the given player to the character
     * @param studentsOutMap Map of students that are going from the character to the entrance of the given player
     * @throws IllegalMoveException When the number of students in or out are smaller than 1 or bigger the 3.
     * When the number of student in is different from the number of student out.
     * When there aren't any players with the given name.
     * When the given player doesn't have enough coins to play the character.
     * When there aren't enough students with the right colors on the character.
     * When there aren't enough students with the right colors in the entrance of the given player.
     */
    public void use(Match match, String playerName, Map<PawnColor, Integer> studentsInMap, Map<PawnColor, Integer> studentsOutMap) throws IllegalMoveException {
        if (studentsInMap.size() < 1 || studentsInMap.size() > 3 || studentsOutMap.size() < 1 || studentsOutMap.size() > 3) {
            throw new IllegalMoveException("Invalid student number");
        }
        if (studentsInMap.values().stream().mapToInt(Integer::intValue).sum() != studentsOutMap.values().stream().mapToInt(Integer::intValue).sum()) {
            throw new IllegalMoveException("Different map sizes");
        }
        Player player = match.getPlayerFromName(playerName);
        checkCost(player);
        List<Student> studentsOut = new ArrayList<>();
        for (Map.Entry<PawnColor, Integer> entry : studentsOutMap.entrySet()) {
            if (getStudentsColorCount(entry.getKey()) < entry.getValue()) {
                throw new IllegalMoveException("There are not enough students with color " + entry.getKey().name() + " on this character");
            }
            List<Student> extracted = removeStudentsByColor(entry.getKey(), entry.getValue());
            studentsOut.addAll(extracted);
        }
        List<Student> studentsIn = new ArrayList<>();
        for (Map.Entry<PawnColor, Integer> entry : studentsInMap.entrySet()) {
            if (player.getSchool().getEntranceCount(entry.getKey()) < entry.getValue()) {
                throw new IllegalMoveException("There are not enough students with color " + entry.getKey().name() + " in the entrance");
            }
            List<Student> extracted = player.getSchool().removeEntranceStudentsByColor(entry.getKey(), entry.getValue());
            studentsIn.addAll(extracted);
        }
        player.getSchool().addStudentsToEntrance(studentsOut);
        addStudents(studentsIn);
        player.removeCoins(cost);
        incrementCost();
        match.updateView();
    }

    /**
     * Gets the initial student number on the character (6)
     * @return The initial student number on the character (6)
     */
    @Override
    public int getInitialStudentsNumber() {
        return 6;
    }
}
