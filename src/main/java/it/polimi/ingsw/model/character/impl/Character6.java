package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.IllegalMoveException;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.character.Character;

public class Character6 extends Character {
    public Character6() {
        super(5, 3, "When resolving a conquering on an island, towers do not count towards influence.");
    }

    public void use(Match match, int playerId) throws IllegalMoveException {
        Player player = match.getPlayerFromId(playerId);
        checkCost(player);
        match.setNoTowersCount(true);
        player.removeCoins(cost);
        incrementCost();
    }
}
