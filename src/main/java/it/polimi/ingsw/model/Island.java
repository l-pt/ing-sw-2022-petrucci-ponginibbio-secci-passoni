package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class Island {
    private List<Student> students;
    private List<Tower> towers;
    private int noEntry;

    public Island() {
        students = new ArrayList<>();
        towers = new ArrayList<>();
        noEntry = 0;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void addStudents(List<Student> students){
        this.students.addAll(students);
    }

    public List<Tower> getTowers() {
        return towers;
    }

    public void addTowers(List<Tower> towers){
        this.towers.addAll(towers);
    }

    public List<Tower> removeAllTowers(){
        List<Tower> result = new ArrayList<>(towers);
        towers.clear();
        return result;
    }

    public int getNoEntry() {
        return noEntry;
    }

    public void addNoEntry(int n){noEntry += n;}

    public void removeNoEntry(){--noEntry;}

    public int getInfluence(Player player, boolean noTowersCount, PawnColor noStudentCount) {
        int result = 0;
        if (towers.size() > 0 && towers.get(0).getColor() == player.getSchool().getTowers().get(0).getColor() && !noTowersCount)
            result += towers.size();
        for (Professor professor : player.getSchool().getProfessors())
            if (noStudentCount == null || noStudentCount != professor.getColor())
                result += player.getSchool().getTableCount(professor.getColor());
        return result + player.getAdditionalInfluence();
    }
}