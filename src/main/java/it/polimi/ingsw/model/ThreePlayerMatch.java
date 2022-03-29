package it.polimi.ingsw.model;

import java.util.List;

public class ThreePlayerMatch extends Match{

    public ThreePlayerMatch(int id, List<Team> teams, List<Player> playerOrder, boolean expert){
        super(id, teams, playerOrder, expert);

        for (Player player : playerOrder)
            player.getSchool().addStudentsToEntrance(extractStudent(2));
    }

    @Override
    public void populateClouds() {
        for (Cloud c : clouds)
            if (!studentBag.isEmpty())
                c.addStudents(extractStudent(4));
    }
}