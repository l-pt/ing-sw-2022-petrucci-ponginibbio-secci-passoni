package it.polimi.ingsw.character;

import it.polimi.ingsw.Match;
import it.polimi.ingsw.PawnColor;
import it.polimi.ingsw.Student;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class StudentCharacter extends Character {
    private List<Student> students;

    public StudentCharacter(int id, int cost, String description) {
        super(id, cost, description);
        students = new ArrayList<>(getInitialStudentsNumber());
    }

    public void setup(Match match) {
        addStudents(match.extractStudent(getInitialStudentsNumber()));
    }

    /**
     * @return A list of n students of the given color
     * @throws IllegalArgumentException if there are not enough students of the given color
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

    public int getStudentsColorCount(PawnColor color) {
        int count = 0;
        for (Student student : students) {
            if (student.getColor() == color) {
                ++count;
            }
        }
        return count;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void addStudents(List<Student> students) {
        this.students.addAll(students);
    }

    /**
     * @return int the number of students that have to be put on this card
     * during the setup phase
     */
    public abstract int getInitialStudentsNumber();
}
