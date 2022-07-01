package it.polimi.ingsw.model;

import java.util.*;

public class School {
    private final List<Student> entrance;
    private final List<Professor> professors;
    private final Map<PawnColor, List<Student>> tables;

    public School() {
        entrance = new ArrayList<>();
        professors = new ArrayList<>();
        tables = new HashMap<>();
        for (PawnColor color : PawnColor.values())
            tables.put(color, new ArrayList<>());
    }

    /**
     * getEntrance()
     * @return The list of students in the entrance
     */
    public List<Student> getEntrance() {
        return entrance;
    }

    /**
     * Returns the number of students of the given color in the entrance
     * @param color The student color
     * @return The number of students of the given color in the entrance
     */
    public int getEntranceCount(PawnColor color) {
        int count = 0;
        for (Student student : entrance)
            if (student.getColor() == color)
                ++count;
        return count;
    }

    /**
     * addStudentsToEntrance()
     * @param students The list of students to add to the entrance
     */
    public void addStudentsToEntrance(List<Student> students) {
        entrance.addAll(students);
    }

    /**
     * Moves a student of the given color from the entrance to the appropriate student table
     * @param color The student color
     * @throws IllegalMoveException If there are no students of the given color in the entrance
     */
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
     * @throws IllegalArgumentException If there are not enough students of the given color in the entrance
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

    /**
     * getProfessors()
     * @return The list of professors
     */
    public List<Professor> getProfessors() {
        return professors;
    }

    /**
     * Check if there is the professor of the given color in the school
     * @param color The professor color
     * @return True if there is the professor of the given color in the school
     */
    public boolean isColoredProfessor(PawnColor color){
        for (Professor professor : professors)
            if (professor.getColor().equals(color))
                return true;
        return false;
    }

    /**
     * addProfessor()
     * @param professor The professor to add
     */
    public void addProfessor(Professor professor) {
        professors.add(professor);
    }

    /**
     * Removes the professor of the given color from the school
     * @param color The professor color
     * @return The professor of the given color
     * @throws IllegalArgumentException If there is no professor of the given color in the school
     */
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

    /**
     * getTables()
     * @return The map of the student tables
     */
    public Map<PawnColor, List<Student>> getTables() {
        return tables;
    }

    /**
     * Calculates the number of students at the student table of the given color
     * @param color The student color
     * @return The number of students at the student table of the given color
     */
    public int getTableCount(PawnColor color) {
        return tables.get(color).size();
    }

    /**
     * addStudentsToTable()
     * @param students The list of students to move to the student tables
     */
    public void addStudentsToTable(List<Student> students) {
        for (Student student : students) {
            tables.get(student.getColor()).add(student);
        }
    }

    /**
     * Removes a certain number of students of the same color from their table
     * @param color The student color
     * @param n The number of students to remove
     * @return The list of removed students
     * @throws IllegalArgumentException When there aren't enough students with the given color in the table
     */
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