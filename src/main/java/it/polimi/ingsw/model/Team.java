package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private int id;
    private List<Player> players;
    private List<Tower> towers;
    private TowerColor towerColor;

    public Team(int id, List<Player> players, int numTowers, TowerColor towerColor) {
        this.id = id;
        this.players = players;
        for (int i = 0; i < numTowers; ++i)
            towers.add(new Tower(towerColor));
        this.towerColor = towerColor;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Tower> getTowers() {
        return towers;
    }

    public void addTowers(List<Tower> towers) {
        this.towers.addAll(towers);
    }

    public List<Tower> removeTowers(int size) {
        List<Tower> towers = new ArrayList<>(size);
        for (int i=0; i<size; ++i)
            towers.add(this.towers.remove(i));
        return towers;
    }

    public TowerColor getTowerColor() {
        return towerColor;
    }
}