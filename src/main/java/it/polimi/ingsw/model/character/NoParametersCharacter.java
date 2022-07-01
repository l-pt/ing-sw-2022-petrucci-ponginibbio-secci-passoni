package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.IllegalMoveException;
import it.polimi.ingsw.model.Match;

public interface NoParametersCharacter {
    void use(Match match, String playerName) throws IllegalMoveException;
}
