package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Match {
    private int id;
    private List<Player> playerOrder;
    private int posMotherNature;
    private List<Island> islands;
    private List<Cloud> clouds;
    private List<Student> studentBag;
    private List<Professor> professors;
    private boolean expert;
    private int coinsReserve;
    private List<Character> characters;
    private boolean drawAllowed;
    private int additionalMoves;
    private boolean noTowersCount;
    private boolean noMotherNatureMoves;
    private PawnColor noStudentsCount;

    public Match(List<Character> allCharacters, boolean expert, List<Player> playerOrder, int id) {
        this.id = id;

        this.playerOrder = playerOrder;

        islands = new ArrayList<>();

        studentBag = new ArrayList<>();
        for (PawnColor color : PawnColor.values())
            for (int i = 0; i < 2; ++i)
                studentBag.add(new Student(color));

        posMotherNature = new Random().nextInt(12);
        for (int i = 0; i < 12; ++i) {
            islands.add(new Island());
            if(i!=posMotherNature && i != (posMotherNature + 6) % 12)
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

        this.expert = expert;

        if(expert) {
            coinsReserve = 20 - playerOrder.size();
            characters = new ArrayList<>(3);
            while (characters.size() != 3) {
                int randomIndex = new Random().nextInt(allCharacters.size());
                if (!characters.contains(allCharacters.get(randomIndex)))
                    characters.add(allCharacters.get(randomIndex));
            }
        }else coinsReserve = 0;
        drawAllowed = false;
        noMotherNatureMoves = false;
        additionalMoves = 0;
        noTowersCount = false;
        noStudentsCount = null;
    }

    public int getId() {
        return id;
    }

    public Player getPlayerFromColor(TowerColor color){
        for(Player player : playerOrder)
            if(player.getSchool().getTowers().get(0).getColor().equals(color))
                return player;
        return null;
    }

    public List<Player> getPlayersOrder() {
        return playerOrder;
    }

    public void orderPlayers() {
        playerOrder.sort((Player p1, Player p2) -> {
            return p1.getCurrentAssistant().getValue() - p2.getCurrentAssistant().getValue();
        });
    }

    public int getPosMotherNature() {
        return posMotherNature;
    }

    public List<Island> getIslands() {
        return islands;
    }

    public List<Cloud> getClouds() {
        return clouds;
    }

    public void addStudents(List<Student> students){
        studentBag.addAll(students);
    }

    public void removeStudents(PawnColor color){
        for (Player player : playerOrder)
            if(player.getSchool().getTableCount(color) != 0)
                addStudents(player.getSchool().removeStudentsFromColor(color, Math.min(3, player.getSchool().getTableCount(color))));
    }

    public List<Student> extractStudent(int n) {
        int randomIndex;
        List<Student> result = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            randomIndex = new Random().nextInt(studentBag.size());
            result.add(studentBag.get(randomIndex));
            studentBag.remove(studentBag.get(randomIndex));
        }
        return result;
    }

    public Professor removeProfessor(PawnColor color) {
        Professor professor = null;
        for(int i=0; i < professors.size(); ++i)
            if(professors.get(i).getColor().equals(color)) {
                professor = professors.get(i);
                professors.remove(professors.get(i));
            }
        return professor;
    }

    public Player whoHaveProfessor(PawnColor color){
        for(int i=0; i<playerOrder.size(); ++i)
            if(playerOrder.get(i).getSchool().isColoredProfessor(color))
                return playerOrder.get(i);
        return null;
    }

    public void addStudent(Student student, Player player){
        player.getSchool().addStudentToTable(student);
        PawnColor color = student.getColor();
        if(expert)
            checkNumberStudents(color, player);
        if(!player.getSchool().isColoredProfessor(color)){
            if(whoHaveProfessor(color) == null)
                player.getSchool().addProfessor(removeProfessor(color));
            else if(player.getSchool().getTableCount(color) == whoHaveProfessor(color).getSchool().getTableCount(color) && drawAllowed)
                player.getSchool().addProfessor(whoHaveProfessor(color).getSchool().removeProfessor(color));
            else if(player.getSchool().getTableCount(color) > whoHaveProfessor(color).getSchool().getTableCount(color))
                player.getSchool().addProfessor(whoHaveProfessor(color).getSchool().removeProfessor(color));
        }
    }

    public void checkNumberStudents(PawnColor color, Player player){
        if(player.getSchool().getTableCount(color) == 3 || player.getSchool().getTableCount(color) == 6 || player.getSchool().getTableCount(color) == 9) {
            player.addCoin();
            --coinsReserve;
        }
    }

    public void islandInfluence(int index){
        boolean draw = false;
        int max = -1, pos = 0;
        if(islands.get(index).getNoEntry() == 0) {
            for (int i = 0; i < playerOrder.size(); ++i) {
                if (islands.get(index).getInfluence(playerOrder.get(i), noTowersCount, noStudentsCount) > max) {
                    max = islands.get(index).getInfluence(playerOrder.get(i), noTowersCount, noStudentsCount);
                    pos = i;
                    draw = false;
                } else if (islands.get(index).getInfluence(playerOrder.get(i), noTowersCount, noStudentsCount) == max)
                    draw = true;
            }
            if (!draw && max > 0 && !playerOrder.get(pos).getSchool().getTowers().get(0).getColor().equals(islands.get(index).getTowers().get(0).getColor())) {
                List<Tower> t = islands.get(index).removeAllTowers();
                getPlayerFromColor(t.get(0).getColor()).getSchool().addTowers(t);
                islands.get(index).addTowers(playerOrder.get(pos).getSchool().removeTowers(t.size()));
                checkIslands(index);
            }
        }else {
            islands.get(index).removeNoEntry();
            /* TO DO
            for (Character character : characters)
                if(character.getId() == 4)
                    character.addNoEntry();
             */
        }
    }
    public void checkIslands(int index) {
        if (!islands.get((index + 1) % islands.size()).getTowers().isEmpty() && islands.get((index + 1) % islands.size()).getTowers().get(0).getColor().equals(islands.get(index).getTowers().get(0).getColor()))
            uniteIslands(Math.min(index, (index + 1) % islands.size()), Math.max(index, (index + 1) % islands.size()));
        else if (!islands.get((index - 1) % islands.size()).getTowers().isEmpty() && islands.get((index - 1) % islands.size()).getTowers().get(0).getColor().equals(islands.get(index).getTowers().get(0).getColor()))
            uniteIslands(Math.min(index, (index - 1) % islands.size()), Math.max(index, (index - 1) % islands.size()));
    }

    public void uniteIslands(int min, int max) {
        islands.get(min).addStudents(islands.get(max).getStudents());
        islands.get(min).addTowers(islands.get(max).getTowers());
        islands.get(min).addNoEntry(islands.get(max).getNoEntry());
        islands.remove(max);
        if(!noMotherNatureMoves)
            posMotherNature = min;
    }

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
        if (player.getCurrentAssistant().getMoves() + additionalMoves >= moves && moves >= 1)
            posMotherNature = (posMotherNature + moves) % islands.size();
        else throw new Exception();
    }

    public boolean isExpert() {
        return expert;
    }

    public int getCoins() {
        return coinsReserve;
    }

    public void useCharacter(int num, Player player, int indexIsland, List<Student> studentsIn, List<Student> studentsOut, PawnColor color){
        if(num < 3 && num >= 0 && player.getCoins() >= characters.get(num).getCost()) {
            switch (characters.get(num).getId()) {
                case 0:
                    /* TO DO
                    islands.get(indexIsland).addStudents(characters.get(num).getStudent());
                    characters.get(num).removeStudents();
                    characters.get(num).addStudents(extractStudent(1));
                    */
                    break;
                case 1:
                    drawAllowed = true;
                    break;
                case 2:
                    noMotherNatureMoves = true;
                    islandInfluence(indexIsland);
                    noMotherNatureMoves = false;
                    break;
                case 3:
                    additionalMoves = 2;
                    break;
                case 4:
                    /* TO DO
                    if(characters.get(num).getNoEntry() > 0) {
                        islands.get(indexIsland).addNoEntry();
                        characters.get(num).removeNoEntry();
                    }
                     */
                    break;
                case 5:
                    noTowersCount = true;
                    break;
                case 6:
                    /* TO DO
                    characters.get(num).addStudents(studentsOut);
                    player.getSchool().removeStudents(studentsOut);
                    player.getSchool().addStudentsToEntrance(studentsIn);
                     */
                    break;
                case 7:
                    player.setAdditionalInfluence(2);
                    break;
                case 8:
                    noStudentsCount = color;
                    break;
                case 9:
                    player.getSchool().addStudents(studentsIn);
                    player.getSchool().removeStudentsFromTable(studentsOut);
                    player.getSchool().addStudentsToEntrance(studentsOut);
                    break;
                case 10:
                    /* TO DO
                    player.getSchool().addStudent(studentsIn);
                    checkNumberStudents();
                    characters.get(num).removeStudents(studentsIn);
                    characters.get(num).addStudents(extractStudent(1));
                     */
                    break;
                case 11:
                    removeStudents(color);
                    break;
            }
            player.removeCoins(characters.get(num).getCost());
            coinsReserve += characters.get(num).getCost();
            characters.get(num).incrementCost();
        }
    }

    public void resetAbility(){
        drawAllowed = false;
        additionalMoves = 0;
        noTowersCount = false;
        noStudentsCount = null;
        for(Player player : playerOrder)
            player.setAdditionalInfluence(0);
    }
}