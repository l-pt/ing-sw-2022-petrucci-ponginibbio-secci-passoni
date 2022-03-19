package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class Island {
    private List<Student> students;
    private List<Tower> towers;
    private boolean noEntry;

    public Island() {
        students = new ArrayList<>();
        towers = new ArrayList<>();
        noEntry = false;
    }

    public boolean isNoEntry() {
        return noEntry;
    }

    public void setNoEntry(boolean noEntry) {
        this.noEntry = noEntry;
    }

    public int getInfluence(Player player, boolean noTowersCount, PawnColor noStudentCount) {
        int result = 0;
        if (towers.size() > 0 && towers.get(0).getColor() == player.getSchool().getTowers().get(0).getColor() && !noTowersCount)
            result += towers.size();
        for (Professor professor : player.getSchool().getProfessors()) {
            if (noStudentCount == null)
                result += player.getSchool().getTableCount(professor.getColor());
            else if (noStudentCount != professor.getColor())
                result += player.getSchool().getTableCount(professor.getColor());
        }
        return result;
    }

    public void addTower(Tower tower) {
        towers.add(tower);
    }

    public void addTowers(List<Tower> towers){
        this.towers.addAll(towers);
    }

    public List<Tower> removeAllTowers(){
        List<Tower> result = new ArrayList<>(towers);
        towers.clear();
        return result;
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public List<Tower> getTowers() {
        return towers;
    }
}
