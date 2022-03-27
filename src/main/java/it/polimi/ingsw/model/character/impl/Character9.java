package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.IllegalMoveException;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.PawnColor;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.character.Character;

public class Character9 extends Character {
    public Character9() {
        super(8, 3, "Choose a color of student; during the influence calculation this turn, " +
                "that color adds no influence.");
    }

    public void use(Match match, int playerId, PawnColor color) throws IllegalMoveException {
        Player player = match.getPlayerFromId(playerId);
        checkCost(player);
        match.getInfluenceCalculationPolicy().setExcludedColor(color);
        player.removeCoins(cost);
        incrementCost();
    }
}
