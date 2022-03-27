package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.character.Character;

import java.util.List;

public class Character12 extends Character {
    public Character12() {
        super(11, 3, "Choose a type of student: every player (including yourself) must return " +
                "3 students of that type from their dining room to the bag. If any player " +
                "has fewer than 3 students of that type, return as many students as they have.");
    }

    public void use(Match match, int playerId, PawnColor color) throws IllegalMoveException {
        Player player = match.getPlayerFromId(playerId);
        checkCost(player);
        for (Player p : match.getPlayersOrder()) {
            List<Student> extracted = p.getSchool().removeStudentsFromColor(color, Math.min(3, p.getSchool().getTableCount(color)));
            match.addStudents(extracted);
        }
        player.removeCoins(cost);
        incrementCost();
    }
}
