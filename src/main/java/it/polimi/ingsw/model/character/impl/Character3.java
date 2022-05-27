package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.IllegalMoveException;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.model.character.IslandCharacter;

public class Character3 extends Character implements IslandCharacter {
    public Character3() {
        super(2, 3, "Choose an island and resolve the island as if mother nature had ended " +
                "her movement there. Mother nature will still move and the island where she " +
                "ends her movement will also be resolved.");
    }

    public void use(Match match, String playerName, int island) throws IllegalMoveException {
        if (island < 0 || island >= match.getIslands().size()) {
            throw new IllegalMoveException("Island must be between 1 and " + match.getIslands().size());
        }
        Player player = match.getPlayerFromName(playerName);
        checkCost(player);
        match.islandInfluence(island, true);
        player.removeCoins(cost);
        incrementCost();
        match.updateView();
    }
}
