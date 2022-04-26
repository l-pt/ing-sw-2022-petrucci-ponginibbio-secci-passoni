package it.polimi.ingsw.character.impl;

import it.polimi.ingsw.IllegalMoveException;
import it.polimi.ingsw.Match;
import it.polimi.ingsw.PawnColor;
import it.polimi.ingsw.Player;
import it.polimi.ingsw.character.Character;

public class Character12 extends Character {
    public Character12() {
        super(11, 3, "Choose a type of student: every player (including yourself) must return " +
                "3 students of that type from their dining room to the bag. If any player " +
                "has fewer than 3 students of that type, return as many students as they have.");
    }

    public void use(Match match, int playerId, PawnColor color) throws IllegalMoveException {
        Player player = match.getPlayerFromId(playerId);
        checkCost(player);
        for (Player p : match.getPlayersOrder())
            match.addStudents(p.getSchool().removeStudentsByColor(color, Math.min(3, p.getSchool().getTableCount(color))));
        player.removeCoins(cost);
        incrementCost();
    }
}