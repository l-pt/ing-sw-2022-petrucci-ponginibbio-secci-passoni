package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class Cloud {
    private List<Student> students;

    public Cloud() {
        students = new ArrayList<>();
    }

    public void addStudents(List<Student> students) {
        this.students.addAll(students);
    }

    public List<Student> removeStudents() {
        List<Student> result = new ArrayList<>(students);
        students.clear();
        return result;
    }
}