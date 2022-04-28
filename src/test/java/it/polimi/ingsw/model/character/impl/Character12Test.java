package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Character12Test extends TestCase {
    @Test
    public void useTest() throws IllegalMoveException {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(1, "test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(0, List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player2), TowerColor.BLACK);
        Match match = new Match(0, List.of(team1, team2), List.of(player1, player2), true);
        Character12 character = new Character12();
        player1.addCoin();
        player1.addCoin();

        Map<Player, Integer> redStudents = new HashMap<>();
        for (Player player : match.getPlayersOrder()) {
            redStudents.put(player, player.getSchool().getTableCount(PawnColor.RED));
        }
        character.use(match, player1.getId(), PawnColor.RED);
        //Check that every player has returned 3 red students
        for (Player player : match.getPlayersOrder()) {
            assertEquals(player.getSchool().getTableCount(PawnColor.RED), Math.max(redStudents.get(player) - 3, 0));
        }
    }
}
