package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.IllegalMoveException;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.model.character.NoParametersCharacter;

public class Character4 extends Character implements NoParametersCharacter {
    public Character4() {
        super(3, 1, "You may move mother nature up to 2 additional islands " +
                "than is indicated by the assistant card you have played.");
    }

    public void use(Match match, String playerName) throws IllegalMoveException {
        Player player = match.getPlayerFromName(playerName);
        checkCost(player);
        player.setAdditionalMoves(2);
        player.removeCoins(cost);
        incrementCost();
        match.updateView();
    }
}
