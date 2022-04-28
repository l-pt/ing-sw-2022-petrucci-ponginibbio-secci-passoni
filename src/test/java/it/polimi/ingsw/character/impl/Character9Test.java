package it.polimi.ingsw.character.impl;

import it.polimi.ingsw.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.util.List;

public class Character9Test extends TestCase {
    @Test
    public void useTest() throws IllegalMoveException {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(1, "test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(0, List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player2), TowerColor.BLACK);
        Match match = new Match(0, List.of(team1, team2), List.of(player1, player2), true);
        Character9 character = new Character9();
        player1.addCoin();
        player1.addCoin();

        character.use(match, player1.getId(), PawnColor.RED);
        assertEquals(match.getInfluencePolicy().getExcludedColor(), PawnColor.RED);
    }
}