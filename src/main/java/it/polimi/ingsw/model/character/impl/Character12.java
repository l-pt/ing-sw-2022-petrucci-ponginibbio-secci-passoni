package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.IllegalMoveException;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.PawnColor;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.model.character.ColorCharacter;

public class Character12 extends Character implements ColorCharacter {
    public Character12() {
        super(11, 3, "Choose a type of student: every player (including yourself) must return " +
                "3 students of that type from their dining room to the bag. If any player " +
                "has fewer than 3 students of that type, return as many students as they have.");
    }

    public void use(Match match, String playerName, PawnColor color) throws IllegalMoveException {
        Player player = match.getPlayerFromName(playerName);
        checkCost(player);
        for (Player p : match.getPlayersOrder())
            match.addStudents(p.getSchool().removeStudentsByColor(color, Math.min(3, p.getSchool().getTableCount(color))));
        player.removeCoins(cost);
        incrementCost();
    }
}