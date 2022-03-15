package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiningRoom {
    private Map<PawnColor, List<Student>> tables;

    public DiningRoom() {
        tables = new HashMap<>();
        for (PawnColor color : PawnColor.values()) {
            tables.put(color, new ArrayList<Student>());
        }
    }

    public int getTableCount(PawnColor color) {
        return tables.get(color).size();
    }

    public void addStudent(Student s) {
        tables.get(s.getColor()).add(s);
    }
}
