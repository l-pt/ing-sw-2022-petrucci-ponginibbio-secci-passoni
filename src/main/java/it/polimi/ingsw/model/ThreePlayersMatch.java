package it.polimi.ingsw.model;

import java.util.List;

public class ThreePlayersMatch extends Match {

    public ThreePlayersMatch(List<Team> teams, List<Player> playerOrder, boolean expert){
        super(teams, playerOrder, expert);

        for (Player player : playerOrder)
            player.getSchool().addStudentsToEntrance(extractStudents(2));
    }

    /**
     * Adds towers to all the teams (for a 3 players match)
     */
    @Override
    public void setupTowers(){
        for (Team team : teams)
            for (int i = 0; i < 6; ++i)
                team.addTower(new Tower(team.getTowerColor()));
    }

    /**
     * Adds 4 students to all the clouds (for a 3 players match)
     */
    @Override
    public void populateClouds() {
        for (Cloud c : clouds)
            if (!studentBag.isEmpty())
                c.addStudents(extractStudents(4));
    }
}