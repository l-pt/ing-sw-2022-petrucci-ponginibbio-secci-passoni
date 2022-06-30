package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class Character8Test extends TestCase {
    @Test
    public void useTest() throws IllegalMoveException {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(List.of(player2), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2), true);
        Character8 character = new Character8();
        player1.addCoin();

        player1.getSchool().addProfessor(new Professor(PawnColor.RED));
        match.getIslands().get(0).addStudents(match.extractStudents(5));

        InfluenceCalculationPolicy calculationPolicy = new InfluenceCalculationPolicy();
        int influence = match.getIslands().get(0).getInfluence(player1, calculationPolicy);
        character.use(match, player1.getName());
        Assertions.assertEquals(influence + 2, match.getIslands().get(0).getInfluence(player1, calculationPolicy));
    }
}
