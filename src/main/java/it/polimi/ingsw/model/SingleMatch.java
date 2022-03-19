package it.polimi.ingsw.model;

import java.util.List;

public class SingleMatch extends Match{
    private List<Player> players;

    public SingleMatch (List<Character> allCharacters, boolean expert, List<Player> playerOrder, int id, List<Player> players){
        super(allCharacters, expert, playerOrder, id);
        this.players=players;
    }

    public List<Player> getPlayers() {
        return players;
    }
}
