package it.polimi.ingsw.character.impl;

import it.polimi.ingsw.IllegalMoveException;
import it.polimi.ingsw.Match;
import it.polimi.ingsw.Player;
import it.polimi.ingsw.character.Character;

public class Character4 extends Character {
    public Character4() {
        super(3, 1, "You may move mother nature up to 2 additional islands " +
                "than is indicated by the assistant card you have played.");
    }

    public void use(Match match, int playerId) throws IllegalMoveException {
        Player player = match.getPlayerFromId(playerId);
        checkCost(player);
        player.setAdditionalMoves(2);
        player.removeCoins(cost);
        incrementCost();
    }
}
