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

    public void use(Match match, String playerName, Map<PawnColor, Integer> entranceStudentsMap, Map<PawnColor, Integer> diningRoomStudentsMap) throws IllegalMoveException {
        if (entranceStudentsMap.size() < 1 || entranceStudentsMap.size() > 2 || diningRoomStudentsMap.size() < 1 || diningRoomStudentsMap.size() > 2) {
            throw new IllegalMoveException("Invalid student number");
        }
        if (entranceStudentsMap.size() != diningRoomStudentsMap.size()) {
            throw new IllegalMoveException("Different map sizes");
        }

        Player player = match.getPlayerFromName(playerName);
        checkCost(player);

        List<Student> diningRoomStudents = new ArrayList<>();
        for (Map.Entry<PawnColor, Integer> entry : diningRoomStudentsMap.entrySet()) {
            if (player.getSchool().getTableCount(entry.getKey()) < entry.getValue()) {
                throw new IllegalMoveException("There are not enough students with color " + entry.getKey().name() + " in the dining room");
            }
            diningRoomStudents.addAll(player.getSchool().removeStudentsByColor(entry.getKey(), entry.getValue()));
        }

        List<Student> entranceStudents = new ArrayList<>();
        for (Map.Entry<PawnColor, Integer> entry : entranceStudentsMap.entrySet()) {
            if (player.getSchool().getEntranceCount(entry.getKey()) < entry.getValue()) {
                throw new IllegalMoveException("There are not enough students with color " + entry.getKey().name() + " in the entrance");
            }
            List<Student> extracted = player.getSchool().removeEntranceStudentsByColor(entry.getKey(), entry.getValue());
            entranceStudents.addAll(extracted);
        }

        player.getSchool().addStudentsToEntrance(diningRoomStudents);
        player.getSchool().addStudentsToTable(entranceStudents);
        player.removeCoins(cost);
        incrementCost();
        match.updateView();
    }
}
