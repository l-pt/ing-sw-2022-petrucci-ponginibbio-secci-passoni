package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.PawnColor;
import it.polimi.ingsw.model.Student;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class StudentCharacter extends Character {
    private List<Student> students;

    public StudentCharacter(int id, int cost, String description) {
        super(id, cost, description);
        students = new ArrayList<>(getInitialStudentsNumber());
    }

    /**
     * Adds the amount of students given by getInitialStudentsNumber() on the character
     * @param match Match
     */
    public void setup(Match match) {
        addStudents(match.extractStudents(getInitialStudentsNumber()));
    }

    /**
     * Removes n students of a certain color form the character
     * @param color PawnColor of the students
     * @param n number of students
     * @return N students of the given color
     * @throws IllegalArgumentException
     */
    public List<Student> removeStudentsByColor(PawnColor color, int n) throws IllegalArgumentException {
        List<Student> result = new ArrayList<>(n);
        Iterator<Student> it = students.iterator();
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
        throw new IllegalArgumentException("There are not enough students with color " + color.name() + " on this character");
    }

    /**
     * Gets the number of students of a certain color on the character
     * @param color PawnColor of the students
     * @return The number of students of a certain color on the character
     */
    public int getStudentsColorCount(PawnColor color) {
        int count = 0;
        for (Student student : students) {
            if (student.getColor() == color) {
                ++count;
            }
        }
        return count;
    }

    /**
     * getStudents()
     * @return The students on the character
     */
    public List<Student> getStudents() {
        return students;
    }

    /**
     * Adds a list of students on the character
     * @param students List of students
     */
    public void addStudents(List<Student> students) {
        this.students.addAll(students);
    }

    /**
     * Gets the number of students that have to be put on the character during the setup phase
     * @return The number of students that have to be put on the character during the setup phase
     */
    public abstract int getInitialStudentsNumber();
}
