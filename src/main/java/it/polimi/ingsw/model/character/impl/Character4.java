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

    /**
     * Uses the ability: "You may move mother nature up to 2 additional islands than is indicated by the assistant card you have played."
     * @param match Match
     * @param playerName The username of a player
     * @throws IllegalMoveException When there aren't any players with the given name.
     * When the given player doesn't have enough coins to play the character
     */
    public void use(Match match, String playerName) throws IllegalMoveException {
        Player player = match.getPlayerFromName(playerName);

        //Checks the coins of the player
        checkCost(player);

        //Gives 2 additional moves to the player
        player.setAdditionalMoves(2);

        player.removeCoins(cost);
        incrementCost();

        //Updates the state of game for the view
        match.updateView();
    }
}
