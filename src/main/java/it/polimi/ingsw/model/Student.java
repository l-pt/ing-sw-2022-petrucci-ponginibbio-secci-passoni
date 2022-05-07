package it.polimi.ingsw.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Student {
    private final PawnColor color;

    public Student(PawnColor color) {
        this.color = color;
    }

    public PawnColor getColor() {
        return color;
    }

    public static String listToString(Collection<Student> students) {
        Map<PawnColor, Integer> colors = new HashMap<>();
        for (Student student : students) {
            colors.put(student.getColor(), colors.getOrDefault(student.getColor(), 0) + 1);
        }
        StringBuilder sb = new StringBuilder("(");
        for (Map.Entry<PawnColor, Integer> entry : colors.entrySet()) {
            if (entry.getValue() > 0) {
                sb.append(entry.getValue()).append(" ").append(entry.getKey().name()).append(" ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}