package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.IllegalMoveException;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.model.character.IslandCharacter;

public class Character5 extends Character implements IslandCharacter {
    private int noEntry;

    public Character5() {
        super(4, 2, "Place a no entry tile on an island of your choice. The first time mother nature " +
                "ends her movement there, put the no entry tile back onto this card. DO NOT calculate " +
                "influence on that island, or place any towers.");
        noEntry = 4;
    }

    /**
     * Uses the ability: "Place a no entry tile on an island of your choice.
     * The first time mother nature ends her movement there, put the no entry tile back onto this card.
     * DO NOT calculate influence on that island, or place any towers."
     * @param match Match
     * @param playerName The username of a player
     * @param island The island number
     * @throws IllegalMoveException When the island number is smaller than 0 or bigger than the islands size.
     * When there aren't any players with the given name.
     * When the given player doesn't have enough coins to play the character.
     * When there aren't any no entries on the character
     */
    public void use(Match match, String playerName, int island) throws IllegalMoveException {
        //Checks the chosen island number
        if (island < 0 || island >= match.getIslands().size()) {
            throw new IllegalMoveException("Island must be between 1 and " + match.getIslands().size());
        }
        Player player = match.getPlayerFromName(playerName);

        //Checks the coins of the player
        checkCost(player);

        //
        if (noEntry <= 0) {
            throw new IllegalMoveException("No Entry tiles absent");
        }

        //
        match.getIslands().get(island).addNoEntry(1);
        --noEntry;

        player.removeCoins(cost);
        incrementCost();

        //Updates the state of game for the view
        match.updateView();
    }

    /**
     * Adds 1 no entry on the character
     */
    public void addNoEntry(){++noEntry;}

    /**
     * Sets the new number of no entries on the character
     * @param noEntry The number of no entries
     */
    public void setNoEntry(int noEntry) {
        this.noEntry = noEntry;
    }

    /**
     * getNoEntry()
     * @return The number of no entries on the character
     */
    public int getNoEntry() {return noEntry;}
}
