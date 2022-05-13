package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.IllegalMoveException;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.PawnColor;

public interface ColorCharacter {
    void use(Match match, String playerName, PawnColor color) throws IllegalMoveException;
}
