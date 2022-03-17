package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class School {
    private List<Student> entrance;
    private List<Professor> professors;
    private List<Tower> towers;
    private Map<PawnColor, List<Student>> tables;

    public School(List<Tower> towers) {
        entrance = new ArrayList<>();
        professors = new ArrayList<>();
        this.towers = towers;
        tables = new HashMap<>();
        for (PawnColor color : PawnColor.values()) {
            tables.put(color, new ArrayList<Student>());
        }
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

    public int getTableCount(PawnColor color) {
        return tables.get(color).size();
    }

    public void addStudent(Student s) {
        tables.get(s.getColor()).add(s);
    }

    public List<Professor> getProfessors() {
        return professors;
    }
}
