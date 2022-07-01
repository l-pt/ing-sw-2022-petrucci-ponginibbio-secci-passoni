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

    /**
     * Uses the ability: "Choose an island and resolve the island as if mother nature had ended her movement there.
     * Mother nature will still move and the island where she ends her movement will also be resolved."
     * @param match Match
     * @param playerName The username of a player
     * @param island The island number
     * @throws IllegalMoveException When the island number is smaller than 0 or bigger than the islands size.
     * When there aren't any players with the given name.
     * When the given player doesn't have enough coins to play the character
     */
    public void use(Match match, String playerName, int island) throws IllegalMoveException {
        //Checks the chosen island number
        if (island < 0 || island >= match.getIslands().size()) {
            throw new IllegalMoveException("Island must be between 1 and " + match.getIslands().size());
        }
        Player player = match.getPlayerFromName(playerName);

        //Checks the coins of the player
        checkCost(player);

        //Calculates the influence of the chosen island
        match.islandInfluence(island, true);

        player.removeCoins(cost);
        incrementCost();

        //Updates the state of game for the view
        match.updateView();
    }
}
