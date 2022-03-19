package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Match {
    private List<Island> islands;
    private List<Cloud> clouds;
    private List<Student> studentBag;
    private List<Professor> professors;
    private List<Character> characters;
    private int coinsReserve;
    private boolean expert;
    private List<Player> playerOrder;
    private int id;
    private int posMotherNature;
    private boolean drawAllowed;

    public Match(List<Character> allCharacters, boolean expert, List<Player> playerOrder, int id) {
        this.expert = expert;

        this.playerOrder=playerOrder;

        islands = new ArrayList<>();

        studentBag = new ArrayList<>();
        for (PawnColor color : PawnColor.values())
            for (int i = 0; i < 2; ++i)
                studentBag.add(new Student(color));

        Random randomGenerator = new Random();
        posMotherNature = randomGenerator.nextInt(12);
        for (int i = 0; i < 12; ++i) {
            islands.add(new Island());
            if(i!=posMotherNature && i!=(posMotherNature + 6) % 12)
                islands.get(i).addStudent(extractStudent(1).get(0));
        }

        for (PawnColor color : PawnColor.values())
            for (int i = 0; i < 24; ++i)
                studentBag.add(new Student(color));

        clouds = new ArrayList<>();
        for (int i = 0; i < playerOrder.size(); ++i)
            clouds.add(new Cloud());

        professors = new ArrayList<>();
        for (PawnColor color : PawnColor.values())
            professors.add(new Professor(color));

        if(expert) {
            characters = new ArrayList<>(3);
            while (characters.size() != 3) {
                int randomIndex = randomGenerator.nextInt(allCharacters.size());
                if (!characters.contains(allCharacters.get(randomIndex)))
                    characters.add(allCharacters.get(randomIndex));
            }
            coinsReserve = 20 - playerOrder.size();
            drawAllowed = false;
        }else coinsReserve = 0;

        this.id = id;
    }

    /*public void useCharacter(int index){
        if(index)
    }*/

    public void orderPlayers() {
        playerOrder.sort((Player p1, Player p2) -> {
            return p1.getCurrentAssistant().getValue() - p2.getCurrentAssistant().getValue();
        });
    }

    public List<Island> getIslands() {
        return islands;
    }

    public List<Cloud> getClouds() {
        return clouds;
    }

    public List<Student> extractStudent(int n) {
        List<Student> result = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            Random randomGenerator = new Random();
            int randomIndex = randomGenerator.nextInt(studentBag.size());
            result.add(studentBag.get(randomIndex));
            studentBag.remove(studentBag.get(randomIndex));
        }
        return result;
    }

    public Professor removeProfessor(PawnColor color) {
        Professor professor=null;
        for(int i=0; i<professors.size(); ++i)
            if(professors.get(i).getColor()==color) {
                professor=professors.get(i);
                professors.remove(professors.get(i));
            }
        return professor;
    }

    public void addCoins(int coins) {
        coinsReserve += coins;
    }

    public void removeCoin() {
        --coinsReserve;
    }

    public int getCoins() {
        return coinsReserve;
    }

    public boolean isExpert() {
        return expert;
    }

    public List<Player> getPlayersOrder() {
        return playerOrder;
    }

    public int getId() {
        return id;
    }

    public int getPosMotherNature() {
        return posMotherNature;
    }

    public Player getPlayerFromColor(TowerColor color){
        for(Player player : playerOrder)
            if(player.getSchool().getTowers().get(0).getColor()==color)
                return player;
            return null;
    }

    public Player whoHaveProfessor(PawnColor color){
        for(int i=0; i<playerOrder.size(); ++i)
            if(playerOrder.get(i).getSchool().isColoredProfessor(color))
                return playerOrder.get(i);
        return null;
    }

    public void addStudent(Student student, Player player){
        player.getSchool().addStudentToTable(student);
        PawnColor color=student.getColor();
        if(expert && (player.getSchool().getTableCount(color)==3 || player.getSchool().getTableCount(color)==6 || player.getSchool().getTableCount(color)==9))
            player.addCoin();
        if(!player.getSchool().isColoredProfessor(color)){
            if(whoHaveProfessor(color)==null)
                player.getSchool().addProfessor(removeProfessor(color));
            else if(player.getSchool().getTableCount(color)==whoHaveProfessor(color).getSchool().getTableCount(color) && drawAllowed)
                player.getSchool().addProfessor(whoHaveProfessor(color).getSchool().removeProfessor(color));
            else if(player.getSchool().getTableCount(color)>whoHaveProfessor(color).getSchool().getTableCount(color))
                player.getSchool().addProfessor(whoHaveProfessor(color).getSchool().removeProfessor(color));
        }
    }

    public void islandInfluence(int index){
        boolean draw=false;
        int max=-1, pos=0;
        for(int i=0; i<playerOrder.size(); ++i){
            if(islands.get(index).getInfluence(playerOrder.get(i))>max){
                max=islands.get(index).getInfluence(playerOrder.get(i));
                pos=i;
                draw=false;
            }else if(islands.get(index).getInfluence(playerOrder.get(i))==max)
                draw=true;
        }
        if(!draw && max>0 && playerOrder.get(pos).getSchool().getTowers().get(0).getColor()!=islands.get(index).getTowers().get(0).getColor()) {
            List<Tower> t = islands.get(index).removeAllTowers();
            getPlayerFromColor(t.get(0).getColor()).getSchool().addTowers(t);
            islands.get(index).addTowers(playerOrder.get(pos).getSchool().removeTowers(t.size()));
            // checkIslands(index)
        }
    }
    /*public void checkIslands(int index){

    }*/

    public void populateClouds() {
        if (playerOrder.size() == 3)
            for (Cloud c : clouds)
                for (int i = 0; i < 4; ++i)
                    c.addStudent(studentBag.remove(new Random().nextInt(studentBag.size())));
            else for (Cloud c : clouds)
                for (int i = 0; i < 3; ++i)
                    c.addStudent(studentBag.remove(new Random().nextInt(studentBag.size())));
    }

    public void moveStudentsFromCloud(int index, Player player) throws Exception {
        if (index >= 0 && index < clouds.size())
            player.getSchool().addStudentsToEntrance(clouds.get(index).removeStudents());
        else throw new Exception();
    }

    public void moveMotherNature(int moves, Player player) throws Exception {
        if (player.getCurrentAssistant().getMoves() >= moves && moves >=1)
            posMotherNature = (posMotherNature + moves) % 12;
        else throw new Exception();
    }
}