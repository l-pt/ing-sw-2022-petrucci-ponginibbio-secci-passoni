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

    public int getInfluence(Player player) {
        int result = 0;
        if (towers.size() > 0 && towers.get(0).getColor() == player.getSchool().getTowers().get(0).getColor())
            result += towers.size();
        for (Professor professor : player.getSchool().getProfessors())
            result += player.getSchool().getTableCount(professor.getColor());
        return result;
    }

    public void addTower(Tower tower) {
        towers.add(tower);
    }

    public void addTowers(List<Tower> towers){
        this.towers.addAll(towers);
    }

    public List<Tower> removeAllTowers(){
        List<Tower> t = new ArrayList<>(towers.size());
        for(Tower tower: towers){
            t.add(tower);
            towers.remove(tower);
        }
        return t;
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public List<Tower> getTowers() {
        return towers;
    }
}
