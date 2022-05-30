package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

public class Character4Test extends TestCase {
    @Test
    public void useTest() throws IllegalMoveException {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(List.of(player2), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2), true);
        Character4 character = new Character4();
        player1.setCurrentAssistant(player1.getAssistantFromValue(1));

        character.use(match, player1.getName());
        Exception e = assertThrows(IllegalMoveException.class, () -> match.moveMotherNature(player1.getName(), 4));
        Assertions.assertEquals(e.getMessage(), "Mother nature moves must be between 1 and " + (player1.getCurrentAssistant().getMoves() + player1.getAdditionalMoves()));

        int posMotherNature = match.getPosMotherNature();
        match.moveMotherNature(player1.getName(), 3);
        Assertions.assertEquals(match.getPosMotherNature(), (posMotherNature + 3) % match.getIslands().size());
    }
}
