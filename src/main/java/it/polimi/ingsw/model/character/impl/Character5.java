package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.IllegalMoveException;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.character.Character;

public class Character5 extends Character {
    private int noEntry;

    public Character5() {
        super(4, 2, "Place a no entry tile on an island of your choice. The first time mother nature " +
                "ends her movement there, put the no entry tile back onto this card. DO NOT calculate " +
                "influence on that island, or place any towers.");
        noEntry = 4;
    }

    public void use(Match match, int playerId, int island) throws IllegalMoveException {
        if (island < 0 || island >= match.getIslands().size()) {
            throw new IllegalMoveException("Island must be between 0 and " + (match.getIslands().size() - 1));
        }
        Player player = match.getPlayerFromId(playerId);
        checkCost(player);
        if (noEntry <= 0) {
            throw new IllegalMoveException("No Entry tiles absent");
        }
        match.getIslands().get(island).addNoEntry(1);
        --noEntry;
        player.removeCoins(cost);
        incrementCost();
    }

    public void addNoEntry(){++noEntry;}
}
