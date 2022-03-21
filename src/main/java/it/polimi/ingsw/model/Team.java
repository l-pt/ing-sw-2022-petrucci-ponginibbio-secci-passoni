package it.polimi.ingsw.model;

import java.util.List;

public class Team {
    private int id;
    private List<Player> players;
    private List<Tower> towers;
    private TowerColor towerColor;

    public Team(List<Player> players, int id, List<Tower> towers, TowerColor towerColor) {
        this.id = id;
        this.players = players;
        this.towers = towers;
        this.towerColor = towerColor;
    }

    public int getId() {
        return id;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Tower> getTowers() {
        return towers;
    }

    public void addTower(Tower tower) {
        towers.add(tower);
    }

    public void removeTower(Tower tower) {
        towers.remove(tower);
    }

    public TowerColor getTowerColor() {
        return towerColor;
    }
}
