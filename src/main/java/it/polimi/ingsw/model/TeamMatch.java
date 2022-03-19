package it.polimi.ingsw.model;

import java.util.List;

public class TeamMatch extends Match{
    private List<Team> teams;

    public TeamMatch(List<Character> allCharacters, boolean expert, List<Player> playerOrder, int id, List<Team> teams){
        super(allCharacters, expert, playerOrder, id);
        this.teams=teams;
    }

    public List<Team> getTeams() {
        return teams;
    }
}
