package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.IllegalMoveException;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.PawnColor;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.model.character.ColorCharacter;

public class Character9 extends Character implements ColorCharacter {
    public Character9() {
        super(8, 3, "Choose a color of student; during the influence calculation this turn, " +
                "that color adds no influence.");
    }

    /**
     * Uses the ability: "Choose a color of student; during the influence calculation this turn,
     * that color adds no influence."
     * @param match Match
     * @param playerName The username of a player
     * @param color PawnColor of a student
     * @throws IllegalMoveException When there aren't any players with the given name.
     * When the given player doesn't have enough coins to play the character
     */
    public void use(Match match, String playerName, PawnColor color) throws IllegalMoveException {
        Player player = match.getPlayerFromName(playerName);

        //Checks the coins of the player
        checkCost(player);

        //Sets the excluded student color
        match.getInfluencePolicy().setExcludedColor(color);

        player.removeCoins(cost);
        incrementCost();

        //Updates the state of game for the view
        match.updateView();
    }
}
