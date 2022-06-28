package it.polimi.ingsw.model;

import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.model.character.StudentCharacter;
import it.polimi.ingsw.model.character.impl.*;
import it.polimi.ingsw.model.observer.Observable;
import it.polimi.ingsw.server.protocol.message.UpdateViewMessage;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Match extends Observable<UpdateViewMessage> {
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
    private boolean gameFinished;
    private String currentPlayer;

    public Match(List<Team> teams, List<Player> playerOrder, boolean expert) {
        //set match id, match difficulty, teams and player order
        this.expert = expert;
        this.teams = teams;
        setupTowers();
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

        populateClouds();

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
                    throw new RuntimeException("Error instantiating character classes");
                }
            }
            //Setup characters
            for (Character character : characters) {
                if (character instanceof StudentCharacter) {
                    ((StudentCharacter) character).setup(this);
                }
            }
        }else coinsReserve = 0;
        drawAllowed = false;
        influencePolicy = new InfluenceCalculationPolicy();
        gameFinished = false;
        currentPlayer = playerOrder.get(0).getName();
    }

    /**
     *
     */
    public void setupTowers(){
        for (Team team : teams) {
            for (int i = 0; i < 8; ++i) {
                team.addTower(new Tower(team.getTowerColor()));
            }
        }
    }

    /**
     *
     * @return
     */
    public List<Team> getTeams() {return teams;}

    /**
     *
     * @param color
     * @return
     * @throws IllegalMoveException
     */
    public Team getTeamFromColor(TowerColor color) throws IllegalMoveException {
        for(Team team : teams) {
            if (team.getTowers().get(0).getColor().equals(color)) {
                return team;
            }
        }
        throw new IllegalMoveException("A team with tower color " + color.name() + " does not exist");
    }

    /**
     *
     * @param player
     * @return
     * @throws IllegalMoveException
     */
    public Team getTeamFromPlayer(Player player) throws IllegalMoveException {
        for (Team team : teams) {
            for (Player p : team.getPlayers()) {
                if (p.equals(player)) {
                    return team;
                }
            }
        }
        throw new IllegalMoveException("Player is not in a team");
    }

    /**
     *
     * @param playerName
     * @return
     * @throws IllegalMoveException
     */
    public Player getPlayerFromName(String playerName) throws IllegalMoveException {
        for(Player player : playerOrder) {
            if (player.getName().equals(playerName)) {
                return player;
            }
        }
        throw new IllegalMoveException("Invalid Name");
    }

    /**
     *
     * @param playerName
     * @return
     * @throws IllegalMoveException
     */
    public int getPosFromName(String playerName) throws IllegalMoveException {
        for (int i = 0; i < playerOrder.size(); ++i) {
            if (playerOrder.get(i).getName().equals(playerName)) {
                return i;
            }
        }
        throw new IllegalMoveException("Invalid Name");
    }

    /**
     *
     * @return
     */
    public List<Player> getPlayersOrder() {
        return playerOrder;
    }

    /**
     *
     * @return
     */
    public int getPosMotherNature() {
        return posMotherNature;
    }

    /**
     *
     * @return
     */
    public List<Character> getCharacters() {
        return characters;
    }

    /**
     *
     * @return
     */
    public List<Island> getIslands() {
        return islands;
    }

    /**
     *
     * @return
     */
    public List<Cloud> getClouds() {
        return clouds;
    }

    /**
     *
     * @return
     */
    public List<Student> getStudentBag(){return studentBag;}

    /**
     *
     * @return
     */
    public List<Professor> getProfessors() {
        return professors;
    }

    /**
     *
     * @param students
     */
    public void addStudents(List<Student> students){
        studentBag.addAll(students);
    }

    /**
     *
     * @param n
     * @return
     */
    public List<Student> extractStudent(int n) {
        int randomIndex;
        List<Student> result = new ArrayList<>(Math.min(n, studentBag.size()));
        if (n >= studentBag.size()) {
            lastTurn = true;
        }
        for (int i = 0; i < Math.min(n, studentBag.size()); ++i) {
            randomIndex = new Random().nextInt(studentBag.size());
            result.add(studentBag.get(randomIndex));
            studentBag.remove(studentBag.get(randomIndex));
        }
        return result;
    }

    /**
     *
     * @param color
     * @return
     * @throws IllegalMoveException
     */
    public Professor removeProfessor(PawnColor color) throws IllegalMoveException {
        for(int i = 0; i < professors.size(); ++i) {
            if (professors.get(i).getColor().equals(color)) {
                return professors.remove(i);
            }
        }
        throw new IllegalMoveException("No professor of color " + color.name() + " on the table");
    }

    /**
     *
     * @param color
     * @return
     */
    public Player whoHasProfessor(PawnColor color){
        for (Player player : playerOrder) {
            if (player.getSchool().isColoredProfessor(color)) {
                return player;
            }
        }
        return null;
    }

    /**
     *
     * @param playerName
     * @param color
     * @param n
     * @throws IllegalMoveException
     */
    public void playerMoveStudents(String playerName, PawnColor color, int n) throws IllegalMoveException {
        if (n < 0) {
            throw new IllegalArgumentException("n must be non negative");
        }
        for(int i = 0; i < n; ++i) {
            getPlayerFromName(playerName).getSchool().addStudentFromEntranceToTable(color);
            if(expert) {
                checkNumberStudents(playerName, color);
            }
            checkProfessors(playerName, color);
        }
    }

    /**
     *
     * @param playerName
     * @param color
     * @throws IllegalMoveException
     */
    public void checkNumberStudents(String playerName, PawnColor color) throws IllegalMoveException {
        Player player = getPlayerFromName(playerName);
        if(player.getSchool().getTableCount(color) == 3 || player.getSchool().getTableCount(color) == 6 || player.getSchool().getTableCount(color) == 9) {
            player.addCoin();
            --coinsReserve;
        }
    }

    /**
     *
     * @param playerName
     * @param color
     * @throws IllegalMoveException
     */
    public void checkProfessors(String playerName, PawnColor color) throws IllegalMoveException {
        Player player = getPlayerFromName(playerName);
        if(!player.getSchool().isColoredProfessor(color)){
            if(whoHasProfessor(color) == null) {
                player.getSchool().addProfessor(removeProfessor(color));
            } else if(player.getSchool().getTableCount(color) == whoHasProfessor(color).getSchool().getTableCount(color) && drawAllowed) {
                player.getSchool().addProfessor(whoHasProfessor(color).getSchool().removeProfessor(color));
            }else if(player.getSchool().getTableCount(color) > whoHasProfessor(color).getSchool().getTableCount(color)) {
                player.getSchool().addProfessor(whoHasProfessor(color).getSchool().removeProfessor(color));
            }
        }
    }

    /**
     *
     * @param index
     * @param noMotherNatureMoves
     * @throws IllegalMoveException
     */
    public void islandInfluence(int index, boolean noMotherNatureMoves) throws IllegalMoveException {
        boolean draw = false;
        int max = -1, pos = 0;
        if(islands.get(index).getNoEntry() == 0) {
            for (int i = 0; i < playerOrder.size(); ++i) {
                if (islands.get(index).getInfluence(playerOrder.get(i), influencePolicy) > max) {
                    max = islands.get(index).getInfluence(playerOrder.get(i), influencePolicy);
                    pos = i;
                    draw = false;
                } else if (islands.get(index).getInfluence(playerOrder.get(i), influencePolicy) == max) {
                    draw = true;
                }
            }
            if (!draw && max > 0 && (islands.get(index).getTowers().size() == 0 || !playerOrder.get(pos).getTowerColor().equals(islands.get(index).getTowers().get(0).getColor()))) {
                if(islands.get(index).getTowers().size() < getTeamFromPlayer(playerOrder.get(pos)).getTowers().size()) {
                    if (islands.get(index).getTowers().size() == 0 && getTeamFromPlayer(playerOrder.get(pos)).getTowers().size() == 1) {
                        gameFinished = true;
                    } else {
                        List<Tower> t = islands.get(index).removeAllTowers();
                        if (t.size() > 0) {
                            getTeamFromColor(t.get(0).getColor()).addTowers(t);
                            islands.get(index).addTowers(getTeamFromPlayer(playerOrder.get(pos)).removeTowers(t.size()));
                        } else {
                            islands.get(index).addTowers(getTeamFromPlayer(playerOrder.get(pos)).removeTowers(1));
                        }
                        checkIslands(index, noMotherNatureMoves);
                    }
                } else {
                    gameFinished = true;
                }
            }
        }else {
            islands.get(index).removeNoEntry();
            for (Character character : characters) {
                if (character instanceof Character5) {
                    ((Character5) character).addNoEntry();
                }
            }
        }
    }

    /**
     *
     * @param index
     * @param noMotherNatureMoves
     */
    public void checkIslands(int index, boolean noMotherNatureMoves) {
        if (!islands.get(islandIndex(index + 1)).getTowers().isEmpty() && islands.get(islandIndex(index + 1)).getTowers().get(0).getColor().equals(islands.get(index).getTowers().get(0).getColor())) {
            uniteIslands(Math.min(index, islandIndex(index + 1)), Math.max(index, islandIndex(index + 1)), noMotherNatureMoves);
        }
        if (!islands.get(islandIndex(index - 1)).getTowers().isEmpty() && islands.get(islandIndex(index - 1)).getTowers().get(0).getColor().equals(islands.get(index).getTowers().get(0).getColor())) {
            uniteIslands(Math.min(index, islandIndex(index - 1)), Math.max(index, islandIndex(index - 1)), noMotherNatureMoves);
        }
    }

    /**
     *
     * @param idx
     * @return
     */
    public int islandIndex(int idx) {
        return ((idx % islands.size()) < 0) ? (idx % islands.size()) + islands.size() : idx % islands.size();
    }

    /**
     *
     * @param min
     * @param max
     * @param noMotherNatureMoves
     */
    public void uniteIslands(int min, int max, boolean noMotherNatureMoves) {
        islands.get(min).addStudents(islands.get(max).getStudents());
        islands.get(min).addTowers(islands.get(max).getTowers());
        islands.get(min).addNoEntry(islands.get(max).getNoEntry());
        islands.remove(max);
        if(!noMotherNatureMoves) {
            posMotherNature = min;
        }
        if (islands.size() <= 3) {
            gameFinished = true;
        }
    }

    /**
     *
     */
    public void populateClouds() {
        for (Cloud c : clouds) {
            c.addStudents(extractStudent(3));
        }
        updateView();
    }

    /**
     *
     * @param playerName
     * @param cloudIndex
     * @throws IllegalMoveException
     */
    public void moveStudentsFromCloud(String playerName, int cloudIndex) throws IllegalMoveException {
        if (cloudIndex >= 0 && cloudIndex < clouds.size()) {
            if (clouds.get(cloudIndex).getStudents().size() == 0) {
                throw new IllegalMoveException("Cloud already chosen by another player this turn");
            } else {
                getPlayerFromName(playerName).getSchool().addStudentsToEntrance(clouds.get(cloudIndex).removeStudents());
                updateView();
            }
        } else throw new IllegalMoveException("Island number must be between 1 and " + getClouds().size());
    }

    /**
     *
     * @param playerName
     * @param moves
     * @throws IllegalMoveException
     */
    public void moveMotherNature(String playerName, int moves) throws IllegalMoveException {
        Player player = getPlayerFromName(playerName);
        if ((player.getCurrentAssistant().getMoves() + player.getAdditionalMoves()) >= moves && moves >= 1) {
            posMotherNature = (posMotherNature + moves) % islands.size();
            islandInfluence(posMotherNature, false);
            updateView();
        } else throw new IllegalMoveException("Mother nature moves must be between 1 and " + (player.getCurrentAssistant().getMoves() + player.getAdditionalMoves()));
    }

    /**
     *
     * @return
     */
    public boolean isExpert() {
        return expert;
    }

    /**
     *
     * @return
     */
    public int getCoins() {
        return coinsReserve;
    }

    /**
     *
     * @param drawAllowed
     */
    public void setDrawAllowed(boolean drawAllowed) {
        this.drawAllowed = drawAllowed;
    }

    /**
     *
     * @return
     */
    public boolean getDrawAllowed() {
        return drawAllowed;
    }

    /**
     *
     * @return
     */
    public InfluenceCalculationPolicy getInfluencePolicy() {
        return influencePolicy;
    }

    /**
     *
     * @param cl
     * @return
     * @throws IllegalMoveException
     */
    public Character getCharacterFromType(Class<? extends Character> cl) throws IllegalMoveException {
        for (Character character : characters) {
            if (character.getClass().equals(cl)) {
                return character;
            }
        }
        throw new IllegalMoveException("There are no characters with class " + cl);
    }

    /**
     *
     */
    public void resetAbility(){
        drawAllowed = false;
        influencePolicy.setCountTowers(true);
        influencePolicy.setExcludedColor(null);
        for(Player player : playerOrder) {
            player.setAdditionalInfluence(0);
            player.setAdditionalMoves(0);
        }
    }

    /**
     *
     * @param color
     * @return
     */
    public int getTowersByColor(TowerColor color) {
        return (int) islands.stream().flatMap(island -> island.getTowers().stream()).filter(t -> t.getColor() == color).count();
    }

    /**
     * Get the winning team
     * @return The winning team, or null in case of a draw
     */
    public Team getWinningTeam() {
        int maxTowers = -1;
        List<Team> teamsWithMaxTowers = new ArrayList<>(teams.size());
        for (Team t : teams) {
            int teamTowers = getTowersByColor(t.getTowerColor());
            if (teamTowers > maxTowers) {
                maxTowers = teamTowers;
                teamsWithMaxTowers.clear();
                teamsWithMaxTowers.add(t);
            } else if (teamTowers == maxTowers) {
                teamsWithMaxTowers.add(t);
            }
        }
        if (teamsWithMaxTowers.size() == 1) {
            return teamsWithMaxTowers.get(0);
        }
        int maxProfessors = -1;
        List<Team> teamsWithMaxProfessors = new ArrayList<>(teams.size());
        for (Team t : teamsWithMaxTowers) {
            int teamProfessors = t.getPlayers().stream().mapToInt(p -> p.getSchool().getProfessors().size()).sum();
            if (teamProfessors > maxProfessors) {
                maxProfessors = teamProfessors;
                teamsWithMaxProfessors.clear();
                teamsWithMaxProfessors.add(t);
            } else if (teamProfessors == maxProfessors) {
                teamsWithMaxProfessors.add(t);
            }
        }
        if (teamsWithMaxProfessors.size() == 1) {
            return teamsWithMaxProfessors.get(0);
        }
        return null;
    }

    /**
     *
     * @return
     */
    public boolean isGameFinished() {
        return gameFinished;
    }

    /**
     *
     * @return
     */
    public boolean isLastTurn() {
        return lastTurn;
    }

    /**
     *
     * @param currentPlayer
     */
    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
        updateView();
    }

    /**
     *
     * @param playerName
     * @param value
     * @throws IllegalMoveException
     */
    public void useAssistant(String playerName, int value) throws IllegalMoveException {
        Player player = getPlayerFromName(playerName);
        int playerPos = getPosFromName(playerName);
        if (value < 1 || value > 10) {
            throw new IllegalMoveException("The value must be between 1 and 10");
        }
        if (player.getAssistantFromValue(value) == null) {
            throw new IllegalMoveException("You don't have an assistant with value " + value);
        }
        for(int i = 0; i < playerPos; ++i) {
            if (playerOrder.get(i).getCurrentAssistant().getValue() == value) {
                List<Assistant> assistants = new ArrayList<>();
                for (int j = 0; j < playerPos; ++j) {
                    assistants.add(playerOrder.get(j).getCurrentAssistant());
                }
                if (!assistants.containsAll(player.getAssistants())) {
                    throw new IllegalMoveException("You can't use this assistant because another player already used it this turn");
                }
                break;
            }
        }
        getPlayerFromName(playerName).setCurrentAssistant(getPlayerFromName(playerName).getAssistantFromValue(value));
        if(getPlayerFromName(playerName).getAssistants().isEmpty()) {
            lastTurn = true;
        }
        if(getPosFromName(playerName) == playerOrder.size() - 1) {
            playerOrder.sort(Comparator.comparingInt((Player p) -> p.getCurrentAssistant().getValue()));
        }
    }

    /**
     *
     * @param playerName
     * @param islandsStudents
     * @param tableStudents
     * @throws IllegalMoveException
     */
    public void moveStudentsToIslandsAndTable(String playerName, Map<Integer, Map<PawnColor, Integer>> islandsStudents, Map<PawnColor, Integer> tableStudents) throws IllegalMoveException {
        //Check that the player has moved exactly three students
        if (islandsStudents.values().stream().flatMap(m -> m.entrySet().stream()).mapToInt(Map.Entry::getValue).sum() +
                tableStudents.values().stream().mapToInt(Integer::intValue).sum() != 3) {
            throw new IllegalMoveException("You have to move exactly three students from the entrance");
        }
        //Check that all island indexes are valid
        for (int island : islandsStudents.keySet()) {
            if (island < 0 || island >= islands.size()) {
                throw new IllegalMoveException("Island " + island + " does not exist");
            }
        }
        //Move students from entrance to islands
        for (Map.Entry<Integer, Map<PawnColor, Integer>> entry : islandsStudents.entrySet()) {
            int island = entry.getKey();
            for (Map.Entry<PawnColor, Integer> islandEntry : entry.getValue().entrySet()) {
                List<Student> extractedStudents = getPlayerFromName(playerName).getSchool().removeEntranceStudentsByColor(islandEntry.getKey(), islandEntry.getValue());
                getIslands().get(island).addStudents(extractedStudents);
            }
        }
        //Move students from entrance to table
        for (Map.Entry<PawnColor, Integer> entry : tableStudents.entrySet()) {
            playerMoveStudents(playerName, entry.getKey(), entry.getValue());
        }
        updateView();
    }

    /**
     *
     */
    public void updateView() {
        notifyObservers(new UpdateViewMessage(
            teams,
            islands,
            playerOrder,
            posMotherNature,
            clouds,
            professors,
            coinsReserve,
            characters,
            expert,
            currentPlayer
        ));
    }
}