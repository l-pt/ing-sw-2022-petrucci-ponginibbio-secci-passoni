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

    /**
     * @return Boolean true if there is a player in the team with the same
     * name, false otherwise
     */
    public boolean isTeamMember(Player player) {
        for (Player p : players) {
            if (p.getName().equals(player.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * getPlayers()
     * @return The list of the team players
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * getTowers()
     * @return The list of the team's towers
     */
    public List<Tower> getTowers() {
        return towers;
    }

    /**
     * addTower()
     * @param tower The tower to add
     */
    public void addTower(Tower tower){towers.add(tower);}

    /**
     * addTowers()
     * @param towers The towers to add
     */
    public void addTowers(List<Tower> towers) {
        this.towers.addAll(towers);
    }

    /**
     * removeTowers()
     * @param size The number of towers to remove
     * @return The list of removed towers
     * @throws IllegalArgumentException If the number of towers to remove exceeds the number of towers owned by the team
     */
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

    /**
     * getTowerColor()
     * @return The color of the team's towers
     */
    public TowerColor getTowerColor() {
        return towerColor;
    }
}