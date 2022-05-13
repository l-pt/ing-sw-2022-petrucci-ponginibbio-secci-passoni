package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.IllegalMoveException;
import it.polimi.ingsw.model.Match;

public interface IslandCharacter {
    public void use(Match match, String playerName, int island) throws IllegalMoveException;
}
