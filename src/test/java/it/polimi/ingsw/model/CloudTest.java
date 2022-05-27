package it.polimi.ingsw.model;

import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class CloudTest extends TestCase {

    @Test
    public void cloudTest() {
        List<Student> students = new ArrayList<>();
        students.add(new Student(PawnColor.BLUE));

        Cloud cloud = new Cloud();
        cloud.addStudents(students);

        List<Student> removedStudents = cloud.removeStudents();
        Assertions.assertEquals(students, removedStudents);

        removedStudents = cloud.removeStudents();
        Assertions.assertEquals(removedStudents.size(), 0);
    }
}
