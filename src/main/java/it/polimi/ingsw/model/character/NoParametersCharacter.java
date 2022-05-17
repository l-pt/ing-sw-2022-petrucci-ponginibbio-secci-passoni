package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.IllegalMoveException;
import it.polimi.ingsw.model.Match;

public interface NoParametersCharacter {
    public void use(Match match, String playerName) throws IllegalMoveException;
}
