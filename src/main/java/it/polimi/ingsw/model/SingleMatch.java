package it.polimi.ingsw.model;

import java.util.List;

public class SingleMatch extends Match{
    private List<Player> players;

    public SingleMatch (List<Character> allCharacters, boolean expert, List<Player> playerOrder, int id, List<Player> players){
        super(allCharacters, expert, playerOrder, id);
        this.players = players;
    }

    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public List<Player> getWinningPlayers() {
        return players.stream().sorted((p1, p2) -> {
            int towers1 = getTowersByColor(p1.getTowerColor());
            int towers2 = getTowersByColor(p2.getTowerColor());
            // Positive = p2 wins
            // Decreasing order
            if (towers1 == towers2) {
                return p2.getSchool().getProfessors().size() - p1.getSchool().getProfessors().size();
            } else {
                return towers2 - towers1;
            }
        }).limit(1).toList();
    }
}
