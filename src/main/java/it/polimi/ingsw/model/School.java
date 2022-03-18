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
        for (PawnColor color : PawnColor.values())
            tables.put(color, new ArrayList<Student>());
    }

    public void addStudentsToEntrance(List<Student> students) {
        entrance.addAll(students);
    }

    public void addProfessor(Professor professor) {
        professors.add(professor);
    }

    public void removeStudent(Student student) {
        entrance.remove(student);
    }

    public Professor removeProfessor(PawnColor color) {
        Professor professor=null;
        for(int i=0; i<professors.size(); ++i)
            if(professors.get(i).getColor()==color) {
                professor=professors.get(i);
                professors.remove(professors.get(i));
            }
        return professor;
    }

    public void addTower(Tower tower) {
        towers.add(tower);
    }

    public void addTowers(List<Tower>towers){
        this.towers.addAll(towers);
    }

    public void removeTower(Tower tower) {
        towers.remove(tower);
    }

    public List<Tower> removeTowers(int n){
        List<Tower> t = new ArrayList<>(n);
        if(towers.size()>n) {
            for (Tower tower : towers){
                t.add(tower);
                towers.remove(tower);
            }
        }
        // else condizione di vittoria perché si finiscono le torri
        return t;
    }

    public int getTableCount(PawnColor color) {
        return tables.get(color).size();
    }

    public void addStudentToTable(Student student) {
        Student s=student;
        removeStudent(student);
        tables.get(s.getColor()).add(s);
    }

    public List<Professor> getProfessors() {
        return professors;
    }

    public boolean isColoredProfessor(PawnColor color){
        for(int i=0; i<professors.size(); ++i)
            if(professors.get(i).getColor()==color)
                return true;
        return false;
    }

    public List<Tower> getTowers() {
        return towers;
    }
}