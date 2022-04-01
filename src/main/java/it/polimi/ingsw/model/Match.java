package it.polimi.ingsw.model;

import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.model.character.StudentCharacter;
import it.polimi.ingsw.model.character.impl.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Match {
    private int id;
    protected List<Team> teams;
    private List<Player> playerOrder;
    private int posMotherNature;
    private List<Island> islands;
    protected List<Cloud> clouds;
    protected List<Student> studentBag;
    private List<Professor> professors;
    private boolean lastTurn;
    private boolean expert;
    private int coinsReserve;
    private List<Character> characters;
    private boolean drawAllowed;
    private InfluenceCalculationPolicy influencePolicy;

    public Match(int id, List<Team> teams, List<Player> playerOrder, boolean expert) {

        //set match id, match difficulty, teams and player order
        this.id = id;
        this.expert = expert;
        this.teams = teams;
        this.playerOrder = playerOrder;

        //allocate memory for islands, studentBag, clouds, professors
        this.islands = new ArrayList<>();
        this.studentBag = new ArrayList<>();
        this.clouds = new ArrayList<>();
        this.professors = new ArrayList<>();

        //prepare bag for initialization of islands
        //2 students of each color to be randomly placed on the islands corresponding to mother nature
        for (PawnColor color : PawnColor.values())
            for (int i = 0; i < 2; ++i)
                studentBag.add(new Student(color));

        //randomly select an island index for mother nature
        posMotherNature = new Random().nextInt(12);
        for (int i = 0; i < 12; ++i) {
            islands.add(new Island());

            //add random student to all islands except where mother nature is and directly across (+/- 6 positions)
            if(i != posMotherNature && i != (posMotherNature + 6) % 12)
                islands.get(i).addStudent(extractStudent(1).get(0));
        }

        //fill the bag with the rest of the students
        for (PawnColor color : PawnColor.values())
            for (int i = 0; i < 24; ++i)
                studentBag.add(new Student(color));

        for (Player player : playerOrder)
            player.getSchool().addStudentsToEntrance(extractStudent(7));

        //add a cloud for each player
        for (int i = 0; i < playerOrder.size(); ++i)
            clouds.add(new Cloud());

        //create a professor for each color
        for (PawnColor color : PawnColor.values())
            professors.add(new Professor(color));

        //at the init, it is not the last turn
        lastTurn = false;

        //specific parameters for expert mode
        if(expert) {
            coinsReserve = 20 - playerOrder.size();

            //Add 1 coin to all players
            for (Player player : playerOrder) {
                player.addCoin();
            }

            //Extract 3 random characters
            List<Class<? extends Character>> allCharacters = new ArrayList<>(12);
            allCharacters.add(Character1.class);
            allCharacters.add(Character2.class);
            allCharacters.add(Character3.class);
            allCharacters.add(Character4.class);
            allCharacters.add(Character5.class);
            allCharacters.add(Character6.class);
            allCharacters.add(Character7.class);
            allCharacters.add(Character8.class);
            allCharacters.add(Character9.class);
            allCharacters.add(Character10.class);
            allCharacters.add(Character11.class);
            allCharacters.add(Character12.class);
            characters = new ArrayList<>(3);
            Random randomGenerator = new Random();
            for (int i = 0; i < 3; ++i) {
                int randomIndex = randomGenerator.nextInt(allCharacters.size());
                try {
                    characters.add(allCharacters.remove(randomIndex).getDeclaredConstructor().newInstance());
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }else coinsReserve = 0;
        drawAllowed = false;
        influencePolicy = new InfluenceCalculationPolicy();

        for (Character character : characters) {
            if (character instanceof StudentCharacter) {
                ((StudentCharacter) character).setup(this);
            }
        }
    }

    public void setupTowers(){
        for (Team team : teams)
            for (int i = 0; i < 8; ++i)
                team.addTower(new Tower(team.getTowerColor()));
    }

    public Team getTeamFromColor(TowerColor color){
        for(Team team : teams)
            if(team.getTowers().get(0).getColor().equals(color))
                return team;
        return null;
    }

    public Team getTeamFromPlayer(Player player){
        for (Team team : teams)
            for(Player p : team.getPlayers())
                if(p.equals(player))
                    return team;
                return null;
    }

    public Player getPlayerFromId(int id){
        for(Player player : playerOrder)
            if (player.getId() == id)
                return player;
            return null;
    }

    public int getPosFromId(int id){
        for (int i = 0; i < playerOrder.size(); ++i)
            if (playerOrder.get(i).getId() == id)
                return i;
            return -1;
    }

    public List<Player> getPlayersOrder() {
        return playerOrder;
    }

    public void orderPlayers() {
        playerOrder.sort((Player p1, Player p2) -> {
            return p1.getCurrentAssistant().getValue() - p2.getCurrentAssistant().getValue();
        });
    }

    public List<Island> getIslands() {
        return islands;
    }

    public List<Student> getStudentBag(){return studentBag;}

    public void addStudents(List<Student> students){
        studentBag.addAll(students);
    }

    public List<Student> extractStudent(int n) {
        int randomIndex;
        List<Student> result = new ArrayList<>(Math.min(n, studentBag.size()));
        if (n > studentBag.size())
            lastTurn = true;
        for (int i = 0; i < Math.min(n, studentBag.size()); ++i) {
            randomIndex = new Random().nextInt(studentBag.size());
            result.add(studentBag.get(randomIndex));
            studentBag.remove(studentBag.get(randomIndex));
        }
        return result;
    }

    public Professor removeProfessor(PawnColor color) {
        Professor professor = null;
        for(int i = 0; i < professors.size(); ++i)
            if(professors.get(i).getColor().equals(color)) {
                professor = professors.get(i);
                professors.remove(professors.get(i));
            }
        return professor;
    }

    public Player whoHaveProfessor(PawnColor color){
        for(int i = 0; i < playerOrder.size(); ++i)
            if(playerOrder.get(i).getSchool().isColoredProfessor(color))
                return playerOrder.get(i);
        return null;
    }

    public void addStudent(PawnColor color, int id){
        Player player = getPlayerFromId(id);
        player.getSchool().addStudentToTable(color);
        if(expert)
            checkNumberStudents(color, player);
        checkProfessors(color, id);
    }

    public void checkNumberStudents(PawnColor color, Player player){
        if(player.getSchool().getTableCount(color) == 3 || player.getSchool().getTableCount(color) == 6 || player.getSchool().getTableCount(color) == 9) {
            player.addCoin();
            --coinsReserve;
        }
    }

    public void checkProfessors(PawnColor color, int id){
        Player player = getPlayerFromId(id);
        if(!player.getSchool().isColoredProfessor(color)){
            if(whoHaveProfessor(color) == null)
                player.getSchool().addProfessor(removeProfessor(color));
            else if(player.getSchool().getTableCount(color) == whoHaveProfessor(color).getSchool().getTableCount(color) && drawAllowed)
                player.getSchool().addProfessor(whoHaveProfessor(color).getSchool().removeProfessor(color));
            else if(player.getSchool().getTableCount(color) > whoHaveProfessor(color).getSchool().getTableCount(color))
                player.getSchool().addProfessor(whoHaveProfessor(color).getSchool().removeProfessor(color));
        }
    }

    public void islandInfluence(int index, boolean noMotherNatureMoves){
        boolean draw = false;
        int max = -1, pos = 0;
        if(islands.get(index).getNoEntry() == 0) {
            for (int i = 0; i < playerOrder.size(); ++i) {
                if (islands.get(index).getInfluence(playerOrder.get(i), influencePolicy) > max) {
                    max = islands.get(index).getInfluence(playerOrder.get(i), influencePolicy);
                    pos = i;
                    draw = false;
                } else if (islands.get(index).getInfluence(playerOrder.get(i), influencePolicy) == max)
                    draw = true;
            }
            if (!draw && max > 0 && !playerOrder.get(pos).getTowerColor().equals(islands.get(index).getTowers().get(0).getColor())) {
                if(islands.get(index).getTowers().size() < getTeamFromPlayer(playerOrder.get(pos)).getTowers().size()) {
                    List<Tower> t = islands.get(index).removeAllTowers();
                    getTeamFromColor(t.get(0).getColor()).addTowers(t);
                    islands.get(index).addTowers(getTeamFromPlayer(playerOrder.get(pos)).removeTowers(t.size()));
                    checkIslands(index, noMotherNatureMoves);
                }
                else endGame(getTeamFromPlayer(playerOrder.get(pos)));
            }
        }else {
            islands.get(index).removeNoEntry();
            for (Character character : characters)
                if(character instanceof Character5)
                    ((Character5) character).addNoEntry();
        }
    }
    public void checkIslands(int index, boolean noMotherNatureMoves) {
        if (!islands.get((index + 1) % islands.size()).getTowers().isEmpty() && islands.get((index + 1) % islands.size()).getTowers().get(0).getColor().equals(islands.get(index).getTowers().get(0).getColor()))
            uniteIslands(Math.min(index, (index + 1) % islands.size()), Math.max(index, (index + 1) % islands.size()), noMotherNatureMoves);
        if (!islands.get((index - 1) % islands.size()).getTowers().isEmpty() && islands.get((index - 1) % islands.size()).getTowers().get(0).getColor().equals(islands.get(index).getTowers().get(0).getColor()))
            uniteIslands(Math.min(index, (index - 1) % islands.size()), Math.max(index, (index - 1) % islands.size()), noMotherNatureMoves);
    }

    public void uniteIslands(int min, int max, boolean noMotherNatureMoves) {
        islands.get(min).addStudents(islands.get(max).getStudents());
        islands.get(min).addTowers(islands.get(max).getTowers());
        islands.get(min).addNoEntry(islands.get(max).getNoEntry());
        islands.remove(max);
        if(!noMotherNatureMoves)
            posMotherNature = min;
        if (islands.size() <= 3)
            endGame(getWinningTeam());
    }

    public void populateClouds() {
        for (Cloud c : clouds)
            if (!studentBag.isEmpty())
                c.addStudents(extractStudent(3));
    }

    public void moveStudentsFromCloud(int index, int id) throws Exception {
        if (index >= 0 && index < clouds.size())
            getPlayerFromId(id).getSchool().addStudentsToEntrance(clouds.get(index).removeStudents());
        else throw new Exception();
    }

    public void moveMotherNature(int moves, int id) throws Exception {
        Player player = getPlayerFromId(id);
        if (player.getCurrentAssistant().getMoves() + player.getAdditionalMoves() >= moves && moves >= 1)
            posMotherNature = (posMotherNature + moves) % islands.size();
        else throw new Exception();
    }

    public int getCoins() {
        return coinsReserve;
    }

    public void setDrawAllowed(boolean drawAllowed) {
        this.drawAllowed = drawAllowed;
    }

    public InfluenceCalculationPolicy getInfluencePolicy() {
        return influencePolicy;
    }

    /**
     * Get the character object with given index
     */
    public Character getCharacter(int characterIndex) throws IllegalMoveException {
        if (characterIndex < 0 || characterIndex >= 3) {
            throw new IllegalMoveException("Invalid character index " + characterIndex);
        }
        return characters.get(characterIndex);
    }

    public void resetAbility(){
        drawAllowed = false;
        influencePolicy.setCountTowers(true);
        influencePolicy.setExcludedColor(null);
        for(Player player : playerOrder) {
            player.setAdditionalInfluence(0);
            player.setAdditionalMoves(0);
        }
    }

    public int getTowersByColor(TowerColor color) {
        return (int) islands.stream().flatMap(island -> island.getTowers().stream()).filter(t -> t.getColor() == color).count();
    }

    public Team getWinningTeam() {
        return teams.stream().min((t1, t2) -> {
            int towers1 = getTowersByColor(t1.getTowerColor());
            int towers2 = getTowersByColor(t2.getTowerColor());
            if (towers1 == towers2)
                return t2.getPlayers().stream().mapToInt(p -> p.getSchool().getProfessors().size()).sum() - t2.getPlayers().stream().mapToInt(p -> p.getSchool().getProfessors().size()).sum();
            else
                return towers2 - towers1;
        }).get();
    }

    public void endGame(Team team) {
        //TODO
    }

    public void checkLastTurn(){
        if(lastTurn)
            endGame(getWinningTeam());
    }

    public void useAssistant(int playerId, int value) throws IllegalMoveException {
        Player player = getPlayerFromId(playerId);
        if (player.getAssistantFromValue(value) == null) {
            throw new IllegalMoveException("Player " + player.getName() + " does not have an assistant with value " + value);
        }
        boolean var = false;
        for(int i = 0; i < getPosFromId(playerId); ++i)
            if (playerOrder.get(i).getCurrentAssistant().getValue() == value)
                var = true;
        if(var)
            for (int i = 0; i < getPosFromId(playerId); ++i) {
                var = false;
                for (int j = 0; j < getPlayerFromId(playerId).getAssistants().size(); ++i)
                    if (getPlayerFromId(playerId).getAssistants().get(j).getValue() == playerOrder.get(i).getCurrentAssistant().getValue())
                        var = true;
                if (!var)
                    throw new IllegalMoveException("Cannot play this assistant");
            }
        getPlayerFromId(playerId).setCurrentAssistant(getPlayerFromId(playerId).getAssistantFromValue(value));
        if(getPlayerFromId(playerId).getAssistants().isEmpty())
            lastTurn = true;
        if(getPosFromId(playerId) == playerOrder.size()-1)
            orderPlayers();
    }
}