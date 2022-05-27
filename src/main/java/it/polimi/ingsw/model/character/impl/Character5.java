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

    public void use(Match match, String playerName, int island) throws IllegalMoveException {
        if (island < 0 || island >= match.getIslands().size()) {
            throw new IllegalMoveException("Island must be between 1 and " + match.getIslands().size());
        }
        Player player = match.getPlayerFromName(playerName);
        checkCost(player);
        if (noEntry <= 0) {
            throw new IllegalMoveException("No Entry tiles absent");
        }
        match.getIslands().get(island).addNoEntry(1);
        --noEntry;
        player.removeCoins(cost);
        incrementCost();
    }

    public void addNoEntry(){++noEntry;}

    public void setNoEntry(int noEntry) {
        this.noEntry = noEntry;
    }

    public int getNoEntry() {return noEntry;}
}
