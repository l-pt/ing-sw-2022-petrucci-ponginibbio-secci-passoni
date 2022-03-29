package it.polimi.ingsw.model;

import java.util.*;

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

    public void addStudentsToTable(List<Student> students) {
        for (Student student : students) {
            tables.get(student.getColor()).add(student);
        }
    }

    public void removeStudent(Student student){
        entrance.remove(student);
    }

    /**
     * @return A list of n students of the given color
     * @throws IllegalArgumentException if there are not enough students of the given color
     */
    public List<Student> removeEntranceStudentsByColor(PawnColor color, int n) throws IllegalArgumentException {
        List<Student> result = new ArrayList<>(n);
        Iterator<Student> it = entrance.iterator();
        while (it.hasNext()) {
            Student student = it.next();
            if (student.getColor() == color) {
                result.add(student);
                it.remove();
                if (result.size() == n) {
                    return result;
                }
            }
        }
        throw new IllegalArgumentException("There are not enough students with color " + color.name() + " in the entrance");
    }

    public int getEntranceCount(PawnColor color) {
        int count = 0;
        for (Student student : entrance)
            if (student.getColor() == color)
                ++count;
        return count;
    }

    public List<Student> removeStudentsByColor(PawnColor color, int n){
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