package it.polimi.ingsw.character.impl;

import it.polimi.ingsw.IllegalMoveException;
import it.polimi.ingsw.Match;
import it.polimi.ingsw.Player;
import it.polimi.ingsw.character.Character;

public class Character8 extends Character {
    public Character8() {
        super(7, 2, "During the influence calculation this turn, you count as having 2 more influence.");
    }

    public void use(Match match, int playerId) throws IllegalMoveException {
        Player player = match.getPlayerFromId(playerId);
        checkCost(player);
        player.setAdditionalInfluence(2);
        player.removeCoins(cost);
        incrementCost();
    }
}