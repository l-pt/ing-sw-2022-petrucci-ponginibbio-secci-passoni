package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private int id;
    private List<Player> players;
    private List<Tower> towers;
    private TowerColor towerColor;

    public Team(int id, List<Player> players, TowerColor towerColor) {
        this.id = id;
        this.players = players;
        towers = new ArrayList<>();
        this.towerColor = towerColor;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Tower> getTowers() {
        return towers;
    }

    public void addTower(Tower tower){towers.add(tower);}

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