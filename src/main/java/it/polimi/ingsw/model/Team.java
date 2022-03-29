package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private int id;
    private List<Player> players;
    private List<Tower> towers;
    private TowerColor towerColor;

    public Team(List<Player> players, int id, boolean is3Team, TowerColor towerColor) {
        this.id = id;
        this.players = players;
        if(is3Team)
            for (int i = 0; i < 6; ++i)
                towers.add(new Tower(towerColor));
        else for (int i = 0; i < 8; ++i)
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