package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class School {
    private List<Student> entrance;
    private DiningRoom diningRoom;
    private List<Professor> professors;
    private List<Tower> towers;

    public School(List<Tower> towers) {
        entrance = new ArrayList<>();
        diningRoom = new DiningRoom();
        professors = new ArrayList<>();
        this.towers = towers;
    }

    public void addStudents(List<Student> students) {
        entrance.addAll(entrance);
    }

    public void addProfessor(Professor professor) {
        professors.add(professor);
    }

    public void removeStudent(Student student) {
        entrance.remove(student);
    }

    public void removeProfessor(Professor professor) {
        professors.remove(professor);
    }

    public void addTower(Tower tower) {
        towers.add(tower);
    }

    public void removeTower(Tower tower) {
        towers.remove(tower);
    }
}
