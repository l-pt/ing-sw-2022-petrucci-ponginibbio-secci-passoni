package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.IllegalMoveException;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.model.character.NoParametersCharacter;

public class Character6 extends Character implements NoParametersCharacter {
    public Character6() {
        super(5, 3, "When resolving a conquering on an island, towers do not count towards influence.");
    }

    public void use(Match match, String playerName) throws IllegalMoveException {
        Player player = match.getPlayerFromName(playerName);
        checkCost(player);
        match.getInfluencePolicy().setCountTowers(false);
        player.removeCoins(cost);
        incrementCost();
        match.updateView();
    }
}
