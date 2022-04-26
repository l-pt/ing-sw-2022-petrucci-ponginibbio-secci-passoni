package it.polimi.ingsw.character.impl;

import it.polimi.ingsw.IllegalMoveException;
import it.polimi.ingsw.Match;
import it.polimi.ingsw.PawnColor;
import it.polimi.ingsw.Player;
import it.polimi.ingsw.character.Character;

public class Character9 extends Character {
    public Character9() {
        super(8, 3, "Choose a color of student; during the influence calculation this turn, " +
                "that color adds no influence.");
    }

    public void use(Match match, int playerId, PawnColor color) throws IllegalMoveException {
        Player player = match.getPlayerFromId(playerId);
        checkCost(player);
        match.getInfluencePolicy().setExcludedColor(color);
        player.removeCoins(cost);
        incrementCost();
    }
}
