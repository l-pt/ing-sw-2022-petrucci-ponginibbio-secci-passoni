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

    @Override
    public List<Player> getWinningPlayers() {
        return teams.stream().sorted((t1, t2) -> {
            int towers1 = getTowersByColor(t1.getTowerColor());
            int towers2 = getTowersByColor(t2.getTowerColor());
            if (towers1 == towers2) {
                return t2.getPlayers().stream().mapToInt(p -> p.getSchool().getProfessors().size()).sum() - t2.getPlayers().stream().mapToInt(p -> p.getSchool().getProfessors().size()).sum();
            } else {
                return towers2 - towers1;
            }
        }).limit(1).toList().get(0).getPlayers();
    }
}
