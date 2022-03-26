package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class School {
    private List<Student> entrance;
    private List<Professor> professors;
    private Map<PawnColor, List<Student>> tables;

    public School() {
        entrance = new ArrayList<>();
        professors = new ArrayList<>();
        tables = new HashMap<>();
        for (PawnColor color : PawnColor.values())
            tables.put(color, new ArrayList<Student>());
    }

    public void addStudentsToEntrance(List<Student> students) {
        entrance.addAll(students);
    }

    public void addStudentToTable(PawnColor color) {
        for (Student student : entrance)
            if(student.getColor().equals(color)){
                removeStudent(student);
                tables.get(color).add(student);
                break;
            }
    }

    public void addStudents(List<Student> students){
        for (Student student : students)
            addStudentToTable(student.getColor());
    }

    public void removeStudent(Student student){
        entrance.remove(student);
    }

    public void removeStudentsFromTable(List<Student> students){
        for (Student student : students)
            tables.get(student.getColor()).remove(tables.get(student.getColor()).size()-1);
    }

    public List<Student> removeStudentsFromColor(PawnColor color, int n){
        List<Student> result = new ArrayList<>(n);
        for(int i=0; i<n; ++i)
            result.add(tables.get(color).remove(tables.get(color).size()-1));
        return result;
    }

    public List<Professor> getProfessors() {
        return professors;
    }

    public void addProfessor(Professor professor) {
        professors.add(professor);
    }

    public Professor removeProfessor(PawnColor color) {
        Professor professor = null;
        for(int i = 0; i < professors.size(); ++i)
            if(professors.get(i).getColor().equals(color)) {
                professor = professors.get(i);
                professors.remove(professors.get(i));
            }
        return professor;
    }

    public boolean isColoredProfessor(PawnColor color){
        for(int i=  0; i < professors.size(); ++i)
            if(professors.get(i).getColor().equals(color))
                return true;
        return false;
    }

    public int getTableCount(PawnColor color) {
        return tables.get(color).size();
    }
}