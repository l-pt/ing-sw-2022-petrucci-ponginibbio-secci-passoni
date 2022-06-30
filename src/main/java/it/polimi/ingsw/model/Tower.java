package it.polimi.ingsw.model;

public class Tower {
    private final TowerColor color;

    public Tower(TowerColor color) {
        this.color = color;
    }

    /**
     * getColor()
     * @return The color of the tower
     */
    public TowerColor getColor() {
        return color;
    }
}
