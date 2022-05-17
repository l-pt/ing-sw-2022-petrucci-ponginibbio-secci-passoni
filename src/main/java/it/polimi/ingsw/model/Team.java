package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Team {
    private List<Player> players;
    private List<Tower> towers;
    private TowerColor towerColor;

    public Team(List<Player> players, TowerColor towerColor) {
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

    public List<Tower> removeTowers(int size) throws IllegalArgumentException {
        if (size > towers.size()) {
            throw new IllegalArgumentException("Team does not have enough towers");
        }
        List<Tower> removed = new ArrayList<>(size);
        Iterator<Tower> iterator = towers.iterator();
        while (removed.size() < size) {
            removed.add(iterator.next());
            iterator.remove();
        }
        return removed;
    }

    public TowerColor getTowerColor() {
        return towerColor;
    }
}