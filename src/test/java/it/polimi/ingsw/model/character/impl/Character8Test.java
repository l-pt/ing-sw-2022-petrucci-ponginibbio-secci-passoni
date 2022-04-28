package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.util.List;

public class Character8Test extends TestCase {
    @Test
    public void useTest() throws IllegalMoveException {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(1, "test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(0, List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player2), TowerColor.BLACK);
        Match match = new Match(0, List.of(team1, team2), List.of(player1, player2), true);
        Character8 character = new Character8();
        player1.addCoin();

        player1.getSchool().addProfessor(new Professor(PawnColor.RED));
        match.getIslands().get(0).addStudents(match.extractStudent(5));

        InfluenceCalculationPolicy calculationPolicy = new InfluenceCalculationPolicy();
        int influence = match.getIslands().get(0).getInfluence(player1, calculationPolicy);
        character.use(match, player1.getId());
        assertEquals(influence + 2, match.getIslands().get(0).getInfluence(player1, calculationPolicy));
    }
}
