package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class Cloud {
    private final List<Student> students;

    public Cloud() {
        students = new ArrayList<>();
    }

    /**
     * getStudents()
     * @return The students on the cloud
     */
    public List<Student> getStudents() {
        return students;
    }

    /**
     * Adds a list of students on the cloud
     * @param students List of students
     */
    public void addStudents(List<Student> students) {
        this.students.addAll(students);
    }

    /**
     * Removes all the students on the cloud
     * @return The students on the cloud
     */
    public List<Student> removeStudents() {
        List<Student> result = new ArrayList<>(students);
        students.clear();
        return result;
    }
}