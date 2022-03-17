package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class StudentCharacter extends Character {
    private List<Student> students;

    public StudentCharacter(int cost, String description, int id) {
        super(cost, description, id);
        students = new ArrayList<>();
    }

    public List<Student> getStudents() {
        return students;
    }

    public void addStudents(List<Student> students) {
        this.students.addAll(students);
    }

    public void removeStudents(List<Student> students) {
        this.students.removeAll(students);
    }
}
