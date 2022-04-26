package it.polimi.ingsw.character.impl;

import it.polimi.ingsw.IllegalMoveException;
import it.polimi.ingsw.Match;
import it.polimi.ingsw.Player;
import it.polimi.ingsw.character.Character;

public class Character6 extends Character {
    public Character6() {
        super(5, 3, "When resolving a conquering on an island, towers do not count towards influence.");
    }

    public void use(Match match, int playerId) throws IllegalMoveException {
        Player player = match.getPlayerFromId(playerId);
        checkCost(player);
        match.getInfluencePolicy().setCountTowers(false);
        player.removeCoins(cost);
        incrementCost();
    }
}
