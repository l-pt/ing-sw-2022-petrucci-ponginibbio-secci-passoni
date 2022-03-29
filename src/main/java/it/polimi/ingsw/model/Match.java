package it.polimi.ingsw.model;

import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.model.character.StudentCharacter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Match {
    private int id;
    private List<Team> teams;
    private List<Player> playerOrder;
    private int posMotherNature;
    protected List<Island> islands;
    private List<Cloud> clouds;
    private List<Student> studentBag;
    private List<Professor> professors;
    public boolean lastTurn;
    private boolean expert;
    private int coinsReserve;
    private List<Character> characters;
    private boolean drawAllowed;
    private InfluenceCalculationPolicy influencePolicy;

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
            if(i != posMotherNature && i != (posMotherNature + 6) % 12)
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

        lastTurn = false;

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
        influencePolicy = new InfluenceCalculationPolicy();

        for (Character character : characters) {
            if (character instanceof StudentCharacter) {
                ((StudentCharacter) character).setup(this);
            }
        }
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
            /*TODO
            for (Character character : characters)
                if(character.getId() == 4)
                    character.addNoEntry();
             */
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
        if (playerOrder.size() == 3)
            for (Cloud c : clouds)
                c.addStudents(extractStudent(4));
        else for (Cloud c : clouds)
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
     * Get the character object with given index and class
     * Ex: match.getCharacter(0, Character2.class).use(match, player);
     */
    public <T extends Character> T getCharacter(int characterIndex, Class<T> characterClass) throws IllegalMoveException {
        if (characterIndex < 0 || characterIndex >= 3) {
            throw new IllegalMoveException("Invalid character index " + characterIndex);
        }
        Character character = characters.get(characterIndex);
        if (!characterClass.isAssignableFrom(character.getClass())) {
            throw new IllegalMoveException("Character with index " + characterIndex + " is not of class " + characterClass.getName());
        }
        return (T) character;
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

    protected int getTowersByColor(TowerColor color) {
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

    public void useAssistant(int playerId, int value) throws Exception{
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
                    throw new Exception();
            }
        getPlayerFromId(playerId).setCurrentAssistant(getPlayerFromId(playerId).getAssistantFromValue(value));
        if(getPlayerFromId(playerId).getAssistants().isEmpty())
            lastTurn = true;
        if(getPosFromId(playerId) == playerOrder.size()-1)
            orderPlayers();
    }
}