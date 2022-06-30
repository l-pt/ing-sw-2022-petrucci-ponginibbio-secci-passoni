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

    /**
     * getStudents()
     * @return The students on the island
     */
    public List<Student> getStudents() {
        return students;
    }

    /**
     * Adds the student on the island
     * @param student Student
     */
    public void addStudent(Student student) {
        students.add(student);
    }

    /**
     * Adds the students on the island
     * @param students List of students
     */
    public void addStudents(List<Student> students){
        this.students.addAll(students);
    }

    /**
     * getTowers()
     * @return The towers on the island
     */
    public List<Tower> getTowers() {
        return towers;
    }

    /**
     * Adds the towers on the island
     * @param towers List of towers of the same color
     */
    public void addTowers(List<Tower> towers){
        this.towers.addAll(towers);
    }

    /**
     * Removes all the towers on the island
     * @return All the towers on the island
     */
    public List<Tower> removeAllTowers(){
        List<Tower> result = new ArrayList<>(towers);
        towers.clear();
        return result;
    }

    /**
     * getNoEntry()
     * @return The number of no entry on the island
     */
    public int getNoEntry() {
        return noEntry;
    }

    /**
     * Adds n no entries on the island
     * @param n Number of no entries
     */
    public void addNoEntry(int n){noEntry += n;}

    /**
     * Removes 1 no entry from the island
     */
    public void removeNoEntry(){--noEntry;}

    /**
     * Gets the influence of a player on an island
     * @param player PLayer
     * @param influenceCalculationPolicy The policies for counting influence
     * @return The influence of a player on an island
     */
    public int getInfluence(Player player, InfluenceCalculationPolicy influenceCalculationPolicy) {
        return influenceCalculationPolicy.getStudentsInfluence(player, students) +
                influenceCalculationPolicy.getTowersInfluence(player, towers) +
                player.getAdditionalInfluence();
    }
}