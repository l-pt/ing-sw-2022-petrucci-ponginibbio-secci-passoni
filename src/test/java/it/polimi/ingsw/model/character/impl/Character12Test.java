package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Character12Test extends TestCase {
    @Test
    public void useTest() throws IllegalMoveException {
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(List.of(player2), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2), true);
        Character12 character = new Character12();
        player1.addCoin();
        player1.addCoin();

        Map<String, Integer> redStudents = new HashMap<>();
        for (Player player : match.getPlayersOrder()) {
            redStudents.put(player.getName(), player.getSchool().getTableCount(PawnColor.RED));
        }
        character.use(match, player1.getName(), PawnColor.RED);
        //Check that every player has returned 3 red students
        for (Player player : match.getPlayersOrder()) {
            Assertions.assertEquals(player.getSchool().getTableCount(PawnColor.RED), Math.max(redStudents.get(player.getName()) - 3, 0));
        }
    }
}
