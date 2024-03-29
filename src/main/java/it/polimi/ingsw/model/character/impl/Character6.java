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

    /**
     * Uses the ability: "When resolving a conquering on an island, towers do not count towards influence."
     * @param match Match
     * @param playerName The username of a player
     * @throws IllegalMoveException When there aren't any players with the given name.
     * When the given player doesn't have enough coins to play the character
     */
    public void use(Match match, String playerName) throws IllegalMoveException {
        Player player = match.getPlayerFromName(playerName);

        //Checks the coins of the player
        checkCost(player);

        //Sets count towers to false
        match.getInfluencePolicy().setCountTowers(false);

        player.removeCoins(cost);
        incrementCost();

        //Updates the state of game for the view
        match.updateView();
    }
}
