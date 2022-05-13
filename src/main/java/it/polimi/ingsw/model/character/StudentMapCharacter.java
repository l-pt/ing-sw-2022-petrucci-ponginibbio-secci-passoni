package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.IllegalMoveException;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.PawnColor;

import java.util.Map;

public interface StudentMapCharacter {
    public void use(Match match, String playerName, Map<PawnColor, Integer> map1, Map<PawnColor, Integer> map2) throws IllegalMoveException;
}
