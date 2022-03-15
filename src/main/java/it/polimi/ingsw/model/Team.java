package it.polimi.ingsw.model;

import java.util.List;

public class Team {
    private List<Player> players;
    private int id;
    private List<Tower> towers;
    private TowerColor towerColor;

    public Team(List<Player> players, int id, List<Tower> towers, TowerColor towerColor) {
        this.players = players;
        this.id = id;
        this.towers = towers;
        this.towerColor = towerColor;
    }

    public void addTower(Tower tower) {
        towers.add(tower);
    }

    public void removeTower(Tower tower) {
        towers.remove(tower);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getId() {
        return id;
    }

    public List<Tower> getTowers() {
        return towers;
    }

    public TowerColor getTowerColor() {
        return towerColor;
    }
}
