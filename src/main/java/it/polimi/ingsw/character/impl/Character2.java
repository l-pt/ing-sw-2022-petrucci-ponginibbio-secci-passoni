package it.polimi.ingsw.character.impl;

import it.polimi.ingsw.IllegalMoveException;
import it.polimi.ingsw.Match;
import it.polimi.ingsw.Player;
import it.polimi.ingsw.character.Character;

public class Character2 extends Character {
    public Character2() {
        super(1, 2, "During this turn, you can take control of any number of professors " +
                "even if you have the same number of students as the player who currently controls them.");
    }

    public void use(Match match, int playerId) throws IllegalMoveException {
        Player player = match.getPlayerFromId(playerId);
        checkCost(player);
        match.setDrawAllowed(true);
        player.removeCoins(cost);
        incrementCost();
    }
}
