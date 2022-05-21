package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.server.Server;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ControllerTest extends TestCase {
    @Test
    public void setupController() {
        Server server = null;
        Player player1 = new Player("test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player("test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(List.of(player2), TowerColor.BLACK);
        Match match = new Match(List.of(team1, team2), List.of(player1, player2), true);
        Controller c = new Controller(server, List.of(team1, team2), List.of(player1, player2), match.isExpert());

    }
}
