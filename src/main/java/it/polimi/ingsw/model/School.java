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
            tables.put(color, new ArrayList<>());
    }

    public List<Student> getEntrance() {
        return entrance;
    }

    public int getEntranceCount(PawnColor color) {
        int count = 0;
        for (Student student : entrance)
            if (student.getColor() == color)
                ++count;
        return count;
    }

    public void addStudentsToEntrance(List<Student> students) {
        entrance.addAll(students);
    }

    public void addStudentFromEntranceToTable(PawnColor color) throws IllegalMoveException {
        if (getEntranceCount(color) == 0) {
            throw new IllegalMoveException("There are no students with color " + color.name() + " in the entrance");
        }
        for (Student student : entrance)
            if (student.getColor().equals(color)) {
                entrance.remove(student);
                tables.get(color).add(student);
                break;
            }
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

    public List<Professor> getProfessors() {
        return professors;
    }

    public boolean isColoredProfessor(PawnColor color){
        for (Professor professor : professors)
            if (professor.getColor().equals(color))
                return true;
        return false;
    }

    public void addProfessor(Professor professor) {
        professors.add(professor);
    }

    public Professor removeProfessor(PawnColor color) throws IllegalArgumentException {
        Professor professor = null;
        for(int i = 0; i < professors.size(); ++i)
            if(professors.get(i).getColor().equals(color)) {
                professor = professors.get(i);
                professors.remove(professors.get(i));
            }
        if (professor == null) {
            throw new IllegalArgumentException("No professor with color " + color.name());
        }
        return professor;
    }

    public Map<PawnColor, List<Student>> getTables() {
        return tables;
    }

    public int getTableCount(PawnColor color) {
        return tables.get(color).size();
    }

    public void addStudentsToTable(List<Student> students) {
        for (Student student : students) {
            tables.get(student.getColor()).add(student);
        }
    }

    public List<Student> removeStudentsByColor(PawnColor color, int n) throws IllegalArgumentException{
        if (tables.get(color).size() < n) {
            throw new IllegalArgumentException("There are not enough students with color " + color.name() + " on the table");
        }
        List<Student> result = new ArrayList<>(n);
        for(int i=0; i<n; ++i)
            result.add(tables.get(color).remove(tables.get(color).size()-1));
        return result;
    }
}