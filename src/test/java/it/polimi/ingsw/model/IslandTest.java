package it.polimi.ingsw.model;

import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class IslandTest extends TestCase {

    @Test
    public void studentAddTest() {
        List<Student> students = new ArrayList<>();
        students.add(new Student(PawnColor.GREEN));
        students.add(new Student(PawnColor.PINK));

        Island island = new Island();
        island.addStudents(students);

        //asserts that list of Student objects can be added to and retrieved from Island object
        assertEquals(island.getStudents(), students);
    }

    @Test
    public void towerTest() {
        List<Tower> towers = new ArrayList<>();
        towers.add(new Tower(TowerColor.BLACK));
        towers.add(new Tower(TowerColor.BLACK));

        Island island = new Island();
        island.addTowers(towers);

        //asserts that list of Tower objects can be added to Island object
        assertEquals(towers, island.getTowers());

        //asserts that all Tower objects can be removed from Island object using removeAllTowers()
        List<Tower> removedTowers = island.removeAllTowers();
        assertEquals(towers, removedTowers);

        //asserts that size of island.getTowers() will return zero when list is empty
        assertEquals(island.getTowers().size(), 0);
    }

    @Test
    public void noEntryTest() {
        Island island = new Island();

        //asserts that island.getNoEnrty() will return zero when noEntry is 0 //false
        assertEquals(island.getNoEntry(), 0);

        //asserts that island.getNoEnrty() will return one after noEntry is added //false
        island.addNoEntry(1);
        assertEquals(island.getNoEntry(), 1);

        //asserts that island.getNoEnrty() will return zero after noEntry is removed //false
        island.removeNoEntry();
        assertEquals(island.getNoEntry(), 0);
    }

    @Test
    public void influenceTest() {
        Player player = new Player("test", TowerColor.BLACK, Wizard.BLUE);
        Island island = new Island();
        InfluenceCalculationPolicy calculationPolicy = new InfluenceCalculationPolicy();

        assertEquals(island.getInfluence(player, calculationPolicy), 0);

        player.getSchool().addProfessor(new Professor(PawnColor.BLUE));
        island.addStudent(new Student(PawnColor.BLUE));
        island.addStudent(new Student(PawnColor.GREEN));
        island.addStudent(new Student(PawnColor.PINK));
        List<Tower> towers = new ArrayList<>();
        towers.add(new Tower(TowerColor.BLACK));
        island.addTowers(towers);
        assertEquals(island.getInfluence(player, calculationPolicy), 2);

        calculationPolicy.setCountTowers(false);
        assertEquals(island.getInfluence(player, calculationPolicy), 1);

        calculationPolicy.setCountTowers(true);
        calculationPolicy.setExcludedColor(PawnColor.BLUE);
        assertEquals(island.getInfluence(player, calculationPolicy), 1);

        calculationPolicy.setCountTowers(false);
        assertEquals(island.getInfluence(player, calculationPolicy), 0);

        calculationPolicy.setExcludedColor(null);
        island.removeAllTowers();
        assertEquals(island.getInfluence(player, calculationPolicy), 1);

        towers.clear();
        towers.add(new Tower(TowerColor.WHITE));
        island.addTowers(towers);
        calculationPolicy.setCountTowers(true);
        assertEquals(island.getInfluence(player, calculationPolicy), 1);

        calculationPolicy.setExcludedColor(PawnColor.YELLOW);
        assertEquals(island.getInfluence(player, calculationPolicy), 1);
    }
}
