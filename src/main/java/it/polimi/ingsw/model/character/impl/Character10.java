package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.character.StudentMapCharacter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Character10 extends Character implements StudentMapCharacter {
    public Character10() {
        super(9, 1, "You may exchange up to 2 students between your entrance and your dining room.");
    }

    /**
     * Uses the ability: "You may exchange up to 2 students between your entrance and your dining room."
     * @param match Match
     * @param playerName The username of a player
     * @param entranceStudentsMap Map of students that are going from the tables of the given player to his entrance
     * @param diningRoomStudentsMap Map of student that are going from the entrance of the given player to his tables
     * @throws IllegalMoveException When the number of students in or out are smaller than 1 or bigger the 2.
     * When the number of student in is different from the number of student out.
     * When there aren't any players with the given name.
     * When the given player doesn't have enough coins to play the character.
     * When there aren't enough students with the right colors in the tables of the given player
     * When there aren't enough students with the right colors in the entrance of the given player
     */
    public void use(Match match, String playerName, Map<PawnColor, Integer> entranceStudentsMap, Map<PawnColor, Integer> diningRoomStudentsMap) throws IllegalMoveException {
        //Checks the sizes of entranceStudentsMap and diningRoomStudentsMap
        if (entranceStudentsMap.size() < 1 || entranceStudentsMap.size() > 2 || diningRoomStudentsMap.size() < 1 || diningRoomStudentsMap.size() > 2) {
            throw new IllegalMoveException("Invalid student number");
        }

        //Checks if the sizes of entranceStudentsMap and diningRoomStudentsMap are the same
        if (entranceStudentsMap.values().stream().mapToInt(Integer::intValue).sum() != diningRoomStudentsMap.values().stream().mapToInt(Integer::intValue).sum()) {
            throw new IllegalMoveException("Different map sizes");
        }

        Player player = match.getPlayerFromName(playerName);

        //Checks the coins of the player
        checkCost(player);

        //Extracts the students from the player dining room
        List<Student> diningRoomStudents = new ArrayList<>();
        for (Map.Entry<PawnColor, Integer> entry : diningRoomStudentsMap.entrySet()) {
            if (player.getSchool().getTableCount(entry.getKey()) < entry.getValue()) {
                throw new IllegalMoveException("There are not enough students with color " + entry.getKey().name() + " in the dining room");
            }
            diningRoomStudents.addAll(player.getSchool().removeStudentsByColor(entry.getKey(), entry.getValue()));
        }

        //Extracts the students from the player entrance
        List<Student> entranceStudents = new ArrayList<>();
        for (Map.Entry<PawnColor, Integer> entry : entranceStudentsMap.entrySet()) {
            if (player.getSchool().getEntranceCount(entry.getKey()) < entry.getValue()) {
                throw new IllegalMoveException("There are not enough students with color " + entry.getKey().name() + " in the entrance");
            }
            List<Student> extracted = player.getSchool().removeEntranceStudentsByColor(entry.getKey(), entry.getValue());
            entranceStudents.addAll(extracted);
        }

        //Adds students in the entrance and in the dining room
        player.getSchool().addStudentsToEntrance(diningRoomStudents);
        player.getSchool().addStudentsToTable(entranceStudents);

        player.removeCoins(cost);
        incrementCost();

        //Updates the state of game for the view
        match.updateView();
    }
}
