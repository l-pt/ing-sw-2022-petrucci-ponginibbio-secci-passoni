package it.polimi.ingsw.model;

import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.*;

public class SchoolTest extends TestCase {
    @Test
    public void entranceTest() {
        School school = new School();
        List<Student> students = new ArrayList<>();
        students.add(new Student(PawnColor.PINK));
        students.add(new Student(PawnColor.RED));

        school.addStudentsToEntrance(students);
        assertEquals(school.getEntranceCount(PawnColor.PINK), 1);
        assertEquals(school.getEntranceCount(PawnColor.RED), 1);
        assertEquals(school.getEntranceCount(PawnColor.YELLOW), 0);
        assertEquals(school.getEntranceCount(PawnColor.BLUE), 0);
        assertEquals(school.getEntranceCount(PawnColor.GREEN), 0);
        assertEquals(students, school.getEntrance());

        school.removeEntranceStudentsByColor(PawnColor.PINK, 1);
        assertEquals(school.getEntranceCount(PawnColor.PINK), 0);
        assertEquals(school.getEntranceCount(PawnColor.RED), 1);
        assertEquals(school.getEntranceCount(PawnColor.YELLOW), 0);
        assertEquals(school.getEntranceCount(PawnColor.BLUE), 0);
        assertEquals(school.getEntranceCount(PawnColor.GREEN), 0);

        Exception e = assertThrows(IllegalArgumentException.class, () -> school.removeEntranceStudentsByColor(PawnColor.PINK, 1));
        assertEquals(e.getMessage(), "There are not enough students with color " + PawnColor.PINK.name() + " in the entrance");
    }

    @Test
    public void addStudentToTableTest() throws IllegalMoveException {
        School school = new School();
        List<Student> students = new ArrayList<>();
        students.add(new Student(PawnColor.PINK));
        students.add(new Student(PawnColor.RED));
        students.add(new Student(PawnColor.RED));
        students.add(new Student(PawnColor.YELLOW));
        students.add(new Student(PawnColor.BLUE));
        students.add(new Student(PawnColor.GREEN));

        school.addStudentsToEntrance(students);
        school.addStudentToTable(PawnColor.PINK);
        assertEquals(school.getEntranceCount(PawnColor.PINK), 0);
        assertEquals(school.getEntranceCount(PawnColor.RED), 2);
        assertEquals(school.getEntranceCount(PawnColor.YELLOW), 1);
        assertEquals(school.getEntranceCount(PawnColor.BLUE), 1);
        assertEquals(school.getEntranceCount(PawnColor.GREEN), 1);

        Map<PawnColor, List<Student>> map = new HashMap<>();
        map.put(PawnColor.PINK, List.of(students.get(0)));
        map.put(PawnColor.RED, Collections.emptyList());
        map.put(PawnColor.YELLOW, Collections.emptyList());
        map.put(PawnColor.GREEN, Collections.emptyList());
        map.put(PawnColor.BLUE, Collections.emptyList());

        assertEquals(map, school.getTables());

        assertEquals(school.getTableCount(PawnColor.PINK), 1);
        assertEquals(school.getTableCount(PawnColor.RED), 0);
        assertEquals(school.getTableCount(PawnColor.YELLOW), 0);
        assertEquals(school.getTableCount(PawnColor.BLUE), 0);
        assertEquals(school.getTableCount(PawnColor.GREEN), 0);

        Exception e = assertThrows(IllegalMoveException.class, () -> school.addStudentToTable(PawnColor.PINK));
        assertEquals("There are no students with color PINK in the entrance", e.getMessage());
    }

    @Test
    public void addStudentsTest() throws IllegalMoveException {
        School school = new School();
        List<Student> students = new ArrayList<>();
        students.add(new Student(PawnColor.PINK));
        students.add(new Student(PawnColor.RED));
        students.add(new Student(PawnColor.RED));
        students.add(new Student(PawnColor.YELLOW));
        students.add(new Student(PawnColor.BLUE));
        students.add(new Student(PawnColor.GREEN));

        school.addStudentsToEntrance(students);
        school.addStudents(students);
        assertEquals(school.getEntranceCount(PawnColor.PINK), 0);
        assertEquals(school.getEntranceCount(PawnColor.RED), 0);
        assertEquals(school.getEntranceCount(PawnColor.YELLOW), 0);
        assertEquals(school.getEntranceCount(PawnColor.BLUE), 0);
        assertEquals(school.getEntranceCount(PawnColor.GREEN), 0);

        assertEquals(school.getTableCount(PawnColor.PINK), 1);
        assertEquals(school.getTableCount(PawnColor.RED), 2);
        assertEquals(school.getTableCount(PawnColor.YELLOW), 1);
        assertEquals(school.getTableCount(PawnColor.BLUE), 1);
        assertEquals(school.getTableCount(PawnColor.GREEN), 1);
    }

    @Test
    public void addStudentsToTableTest() {
        School school = new School();
        List<Student> students = new ArrayList<>();
        students.add(new Student(PawnColor.PINK));
        students.add(new Student(PawnColor.RED));
        students.add(new Student(PawnColor.RED));
        students.add(new Student(PawnColor.YELLOW));
        students.add(new Student(PawnColor.BLUE));
        students.add(new Student(PawnColor.GREEN));

        school.addStudentsToTable(students);
        assertEquals(school.getTableCount(PawnColor.PINK), 1);
        assertEquals(school.getTableCount(PawnColor.RED), 2);
        assertEquals(school.getTableCount(PawnColor.YELLOW), 1);
        assertEquals(school.getTableCount(PawnColor.BLUE), 1);
        assertEquals(school.getTableCount(PawnColor.GREEN), 1);
    }

    @Test
    public void removeStudentsByColor() {
        School school = new School();
        List<Student> students = new ArrayList<>();
        students.add(new Student(PawnColor.PINK));
        students.add(new Student(PawnColor.RED));
        students.add(new Student(PawnColor.RED));
        students.add(new Student(PawnColor.YELLOW));
        students.add(new Student(PawnColor.BLUE));
        students.add(new Student(PawnColor.GREEN));

        school.addStudentsToTable(students);
        school.removeStudentsByColor(PawnColor.RED, 1);
        assertEquals(school.getTableCount(PawnColor.PINK), 1);
        assertEquals(school.getTableCount(PawnColor.RED), 1);
        assertEquals(school.getTableCount(PawnColor.YELLOW), 1);
        assertEquals(school.getTableCount(PawnColor.BLUE), 1);
        assertEquals(school.getTableCount(PawnColor.GREEN), 1);

        Exception e = assertThrows(IllegalArgumentException.class, () -> school.removeStudentsByColor(PawnColor.PINK, 2));
        assertEquals(e.getMessage(), "There are not enough students with color " + PawnColor.PINK.name() + " on the table");
    }

    @Test
    public void professorsTest() {
        School school = new School();
        for (PawnColor color : PawnColor.values()) {
            assertEquals(school.isColoredProfessor(color), false);
        }
        assertEquals(school.getProfessors().size(), 0);

        school.addProfessor(new Professor(PawnColor.PINK));
        assertEquals(school.getProfessors().size(), 1);
        assertEquals(school.isColoredProfessor(PawnColor.PINK), true);

        Exception e = assertThrows(IllegalArgumentException.class, () -> school.removeProfessor(PawnColor.RED));
        assertEquals(e.getMessage(), "No professor with color " + PawnColor.RED.name());

        Professor professor = school.removeProfessor(PawnColor.PINK);
        assertEquals(professor.getColor(), PawnColor.PINK);
        assertEquals(school.isColoredProfessor(PawnColor.PINK), false);
        assertEquals(school.getProfessors().size(), 0);
    }
}
