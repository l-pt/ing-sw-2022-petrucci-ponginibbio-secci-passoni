package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class Island {
    private List<Student> students;
    private List<Tower> towers;
    private boolean motherNature;
    private boolean noEntry;

    public Island(boolean motherNature) {
        students = new ArrayList<>();
        towers = new ArrayList<>();
        this.motherNature = motherNature;
        noEntry = false;
    }

    public boolean isMotherNature() {
        return motherNature;
    }

    public boolean isNoEntry() {
        return noEntry;
    }

    public void setMotherNature(boolean motherNature) {
        this.motherNature = motherNature;
    }

    public void setNoEntry(boolean noEntry) {
        this.noEntry = noEntry;
    }

    //public int getInfluence(Team team)

    public void addTower(Tower tower) {
        towers.add(tower);
    }

    public void addStudent(Student student) {
        students.add(student);
    }
}
