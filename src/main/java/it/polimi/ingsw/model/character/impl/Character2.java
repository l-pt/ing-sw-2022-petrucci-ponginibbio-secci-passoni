package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.IllegalMoveException;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.model.character.NoParametersCharacter;

public class Character2 extends Character implements NoParametersCharacter {
    public Character2() {
        super(1, 2, "During this turn, you can take control of any number of professors " +
                "even if you have the same number of students as the player who currently controls them.");
    }

    public void use(Match match, String playerName) throws IllegalMoveException {
        Player player = match.getPlayerFromName(playerName);
        checkCost(player);
        match.setDrawAllowed(true);
        player.removeCoins(cost);
        incrementCost();
    }
}
