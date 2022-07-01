package it.polimi.ingsw.model;

import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.model.character.StudentCharacter;
import it.polimi.ingsw.model.character.impl.*;
import it.polimi.ingsw.model.observer.Observable;
import it.polimi.ingsw.server.protocol.message.UpdateViewMessage;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Match extends Observable<UpdateViewMessage> {
    protected List<Team> teams;
    private final List<Player> playerOrder;
    private int posMotherNature;
    private final List<Island> islands;
    protected List<Cloud> clouds;
    protected List<Student> studentBag;
    private final List<Professor> professors;
    private boolean lastTurn;
    private final boolean expert;
    private int coinsReserve;
    private List<Character> characters;
    private boolean drawAllowed;
    private final InfluenceCalculationPolicy influencePolicy;
    private boolean gameFinished;
    private String currentPlayer;

    /**
     * Match Constructor creates a new Match object with the following parameters
     * @param teams as a group of players (black, white, or grey)
     * @param playerOrder input turn order of players
     * @param expert boolean to decide if expert mode is on for the match
     */
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
                islands.get(i).addStudent(extractStudents(1).get(0));
        }

        //fill the bag with the rest of the students
        for (PawnColor color : PawnColor.values())
            for (int i = 0; i < 24; ++i)
                studentBag.add(new Student(color));

        for (Player player : playerOrder)
            player.getSchool().addStudentsToEntrance(extractStudents(7));

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
     * Adds 8 towers to all the teams (for a 2 or 4 players match)
     */
    public void setupTowers(){
        for (Team team : teams) {
            for (int i = 0; i < 8; ++i) {
                team.addTower(new Tower(team.getTowerColor()));
            }
        }
    }

    /**
     * getTeams()
     * @return The teams of the match
     */
    public List<Team> getTeams() {return teams;}

    /**
     * Searches for a team with a certain tower color
     * @param color TowerColor of a team
     * @return The team with the given tower color
     * @throws IllegalMoveException When there isn't a team with the given tower color
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
     * Searches for a team which a certain player is part of
     * @param player Player
     * @return The team which the given player is part of
     * @throws IllegalMoveException When the given player isn't part of any teams
     */
    public Team getTeamFromPlayer(Player player) throws IllegalMoveException {
        for (Team team : teams) {
            for (Player p : team.getPlayers()) {
                if (p.equals(player)) {
                    return team;
                }
            }
        }
        throw new IllegalMoveException(player.getName() + " is not in a team");
    }

    /**
     * Searches for a player with a certain name
     * @param playerName The username of a player
     * @return The player with the given name
     * @throws IllegalMoveException When there aren't any players with the given name
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
     * Searches for the position of a player with a certain name in the order of play for the current turn
     * @param playerName The username of a player
     * @return The position of a player with the given name in the order of play for the current turn
     * @throws IllegalMoveException When there aren't any players with the given name
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
     * getPlayersOrder()
     * @return The order of play for the current turn
     */
    public List<Player> getPlayersOrder() {
        return playerOrder;
    }

    /**
     * getPosMotherNature()
     * @return The island number which mother nature is currently on
     */
    public int getPosMotherNature() {
        return posMotherNature;
    }

    /**
     * getCharacters()
     * @return The 3 characters of the match
     */
    public List<Character> getCharacters() {
        return characters;
    }

    /**
     * getIslands()
     * @return The islands of the match
     */
    public List<Island> getIslands() {
        return islands;
    }

    /**
     * getClouds()
     * @return The clouds of the match
     */
    public List<Cloud> getClouds() {
        return clouds;
    }

    /**
     * getStudentBag()
     * @return The students remaining in the student bag
     */
    public List<Student> getStudentBag(){return studentBag;}

    /**
     * getProfessors()
     * @return The professors that aren't taken by any players
     */
    public List<Professor> getProfessors() {
        return professors;
    }

    /**
     * Adds the given students to studentBag
     * @param students List of students
     */
    public void addStudents(List<Student> students){
        studentBag.addAll(students);
    }

    /**
     * Extracts n random students from studentBag, removing them from it
     * @param n Number of students that is required to extract from studentBag
     * @return List of n random students from studentBag, or all the students in studentBag if studentBag.size() is less than n
     */
    public List<Student> extractStudents(int n) {
        int randomIndex;
        List<Student> students = new ArrayList<>(Math.min(n, studentBag.size()));
        if (n >= studentBag.size()) {
            lastTurn = true;
        }
        for (int i = 0; i < Math.min(n, studentBag.size()); ++i) {
            randomIndex = new Random().nextInt(studentBag.size());
            students.add(studentBag.get(randomIndex));
            studentBag.remove(studentBag.get(randomIndex));
        }
        return students;
    }

    /**
     * Removes the professor of the given color on the board and returns it
     * @param color PawnColor of the professor
     * @return The professor of the given color
     * @throws IllegalMoveException When the professor of the given color is already taken by a player
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
     * Searches for the player with the professor of the given color
     * @param color PawnColor of the professor
     * @return The player with the professor of the given color, or null if there isn't a player with the professor of the given color
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
     * Moves n students of the given color from the entrance to the player tables
     * @param playerName The username of a player
     * @param color PawnColor of the students
     * @param n Number of students of the given color
     * @throws IllegalMoveException When n is negative
     */
    public void playerMoveStudents(String playerName, PawnColor color, int n) throws IllegalMoveException {
        if (n < 0) {
            throw new IllegalArgumentException("Students numbers must be positive");
        }
        for(int i = 0; i < n; ++i) {
            //Adds the students from the entrance to the table
            getPlayerFromName(playerName).getSchool().addStudentFromEntranceToTable(color);

            //Checks if the students of the table are 3, 6 or 9 (only if the match is expert)
            if(expert) {
                checkNumberStudents(playerName, color);
            }
            //Checks the number of students of that color and determinate the owner of the professor
            checkProfessors(playerName, color);
        }
    }

    /**
     * Checks the table count of the given color and if is 3, 6, or 9 adds a coin to the player coin reserve
     * @param playerName The username of a player
     * @param color PawnColor of the table
     * @throws IllegalMoveException When there aren't any players with the given name
     */
    public void checkNumberStudents(String playerName, PawnColor color) throws IllegalMoveException {
        Player player = getPlayerFromName(playerName);
        if(player.getSchool().getTableCount(color) == 3 || player.getSchool().getTableCount(color) == 6 || player.getSchool().getTableCount(color) == 9) {
            player.addCoin();
            --coinsReserve;
        }
    }

    /**
     * Checks the table count of the given color of all the players
     * and gives the professor to the player with the most students in the table of that color.
     * In case of a draw the professor remains to the original player
     * @param playerName The username of a player
     * @param color PawnColor of the professor
     * @throws IllegalMoveException When there aren't any players with the given name
     */
    public void checkProfessors(String playerName, PawnColor color) throws IllegalMoveException {
        Player player = getPlayerFromName(playerName);

        //Checks if the player already has that color professor
        if (!player.getSchool().isColoredProfessor(color)) {

            //Checks if no player has that color professor
            if (whoHasProfessor(color) == null) {
                player.getSchool().addProfessor(removeProfessor(color));

                //Checks if the player has the same amount of students of that color as the current owner of the professor
                //and the draw are allowed or if the player has the most amount of student of that color
            } else if ((player.getSchool().getTableCount(color) == whoHasProfessor(color).getSchool().getTableCount(color) && drawAllowed)
                    || (player.getSchool().getTableCount(color) > whoHasProfessor(color).getSchool().getTableCount(color))) {
                player.getSchool().addProfessor(whoHasProfessor(color).getSchool().removeProfessor(color));
            }
        }
    }

    /**
     * Checks the influence of all the players of a certain island and manages the towers on that island
     * @param index Index of an island
     * @param noMotherNatureMoves True if mother nature won't move, false otherwise
     * @throws IllegalMoveException If the player with the most influence of the island isn't part of any team
     */
    public void islandInfluence(int index, boolean noMotherNatureMoves) throws IllegalMoveException {
        boolean draw = false;
        int max = -1, pos = 0;

        //Checks if there aren't any no entries on that island
        if(islands.get(index).getNoEntry() == 0) {

            //Finds the max influence on the island, the position of that player and
            //if there are more players with the same max influence
            for (int i = 0; i < playerOrder.size(); ++i) {
                if (islands.get(index).getInfluence(playerOrder.get(i), influencePolicy) > max) {
                    max = islands.get(index).getInfluence(playerOrder.get(i), influencePolicy);
                    pos = i;
                    draw = false;
                } else if (islands.get(index).getInfluence(playerOrder.get(i), influencePolicy) == max) {
                    draw = true;
                }
            }

            //confirm that
            // 1) player wins the draw
            // 2) player doesn't have tower at index
            if (!draw && max > 0 && (islands.get(index).getTowers().isEmpty() || !playerOrder.get(pos).getTowerColor().equals(islands.get(index).getTowers().get(0).getColor()))) {

                //if island has fewer towers than the team
                if(islands.get(index).getTowers().size() < getTeamFromPlayer(playerOrder.get(pos)).getTowers().size()) {

                    //if no towers on island AND team has one tower
                    if (islands.get(index).getTowers().isEmpty() && getTeamFromPlayer(playerOrder.get(pos)).getTowers().size() == 1) {

                        //Game Over
                        gameFinished = true;

                    } else {

                        //pop all towers from island to a temporary list t
                        List<Tower> t = islands.get(index).removeAllTowers();

                        //check if t wasn't empty
                        if (t.size() > 0) {

                            //add colored tower to respective team
                            getTeamFromColor(t.get(0).getColor()).addTowers(t);

                            //add team towers to island, and remove old team towers from island
                            islands.get(index).addTowers(getTeamFromPlayer(playerOrder.get(pos)).removeTowers(t.size()));

                        } else {

                            //add team towers to island
                            islands.get(index).addTowers(getTeamFromPlayer(playerOrder.get(pos)).removeTowers(1));
                        }

                        //check to see if adjacent islands should be merged
                        checkIslands(index, noMotherNatureMoves);
                    }
                } else {

                    //Game Over
                    gameFinished = true;
                }
            }
        }else {

            //removes 1 no entry from the island
            islands.get(index).removeNoEntry();

            //add 1 no entry to the Character5
            for (Character character : characters) {
                if (character instanceof Character5) {
                    ((Character5) character).addNoEntry();
                }
            }
        }
    }

    /**
     * Checks the 2 adjacent islands of an island and unifies them if they have the same tower color
     * @param index Index of an island
     * @param noMotherNatureMoves True if mother nature won't move, false otherwise
     */
    public void checkIslands(int index, boolean noMotherNatureMoves) {

        //check if island adjacent to the right needs to be merged
        if (!islands.get(islandIndex(index + 1)).getTowers().isEmpty() &&
                islands.get(islandIndex(index + 1)).getTowers().get(0).getColor().equals(islands.get(index).getTowers().get(0).getColor())) {

            //merge islands
            uniteIslands(Math.min(index, islandIndex(index + 1)), Math.max(index, islandIndex(index + 1)), noMotherNatureMoves);
        }
        //check if island adjacent to the left needs to be merged
        if (!islands.get(islandIndex(index - 1)).getTowers().isEmpty() &&
                islands.get(islandIndex(index - 1)).getTowers().get(0).getColor().equals(islands.get(index).getTowers().get(0).getColor())) {

            //merge islands
            uniteIslands(Math.min(index, islandIndex(index - 1)), Math.max(index, islandIndex(index - 1)), noMotherNatureMoves);
        }
    }

    /**
     * Formats an index of an island in module islands.size()
     * @param idx Index of an island
     * @return The index of the island in module islands.size()
     */
    public int islandIndex(int idx) {
        return ((idx % islands.size()) < 0) ? (idx % islands.size()) + islands.size() : idx % islands.size();
    }

    /**
     * Unifies 2 islands with indexes min and max
     * @param min Min between the 2 indexes
     * @param max Max between the 2 indexes
     * @param noMotherNatureMoves True if mother nature won't move, false otherwise
     */
    public void uniteIslands(int min, int max, boolean noMotherNatureMoves) {

        //transfer all the data to one island: (min)
        islands.get(min).addStudents(islands.get(max).getStudents());
        islands.get(min).addTowers(islands.get(max).getTowers());
        islands.get(min).addNoEntry(islands.get(max).getNoEntry());

        //remove the island that doesn't hold all the data: (max)
        islands.remove(max);

        if(!noMotherNatureMoves) {
            posMotherNature = min;
        }
        if (islands.size() <= 3) {
            gameFinished = true;
        }
    }

    /**
     * Adds 3 students to all the clouds (for a 2 or 4 players match)
     */
    public void populateClouds() {
        for (Cloud c : clouds) {
            c.addStudents(extractStudents(3));
        }
        updateView();
    }

    /**
     * Moves all the students of a certain cloud to the player entrance
     * @param playerName The username of a player
     * @param cloudIndex Index of the selected cloud
     * @throws IllegalMoveException When a cloud has been already chosen by another player this turn.
     * When the given cloud index is less than 0 or more than the clouds number
     */
    public void moveStudentsFromCloud(String playerName, int cloudIndex) throws IllegalMoveException {
        if (cloudIndex >= 0 && cloudIndex < clouds.size()) {

            //if cloud has no students, it has already been selected
            if (clouds.get(cloudIndex).getStudents().isEmpty()) {
                throw new IllegalMoveException("Cloud already chosen by another player this turn");
            } else {

                //move students from selected cloud to students entrance
                getPlayerFromName(playerName).getSchool().addStudentsToEntrance(clouds.get(cloudIndex).removeStudents());

                //update game view
                updateView();
            }
        } else throw new IllegalMoveException("Island number must be between 1 and " + getClouds().size()); //out of bounds
    }

    /**
     * Moves mother nature by a certain amount of moves
     * @param playerName The username of a player
     * @param moves Moves that the player wants to make with mother nature
     * @throws IllegalMoveException When the given moves are less than 1 or more than the value of the assistant played by the player
     */
    public void moveMotherNature(String playerName, int moves) throws IllegalMoveException {
        Player player = getPlayerFromName(playerName);

        //check legality of mother nature move request
        if ((player.getCurrentAssistant().getMoves() + player.getAdditionalMoves()) >= moves && moves >= 1) {

            //move mother nature the requested number of islands (int moves)
            posMotherNature = (posMotherNature + moves) % islands.size();
            islandInfluence(posMotherNature, false);

            //update game view
            updateView();
        } else throw new IllegalMoveException("Mother nature moves must be between 1 and " + (player.getCurrentAssistant().getMoves() + player.getAdditionalMoves()));
    }

    /**
     * isExpert()
     * @return True if the match is expert, false otherwise
     */
    public boolean isExpert() {
        return expert;
    }

    /**
     * getCoins()
     * @return The number of coins available on the board
     */
    public int getCoins() {
        return coinsReserve;
    }

    /**
     * setDrawAllowed()
     * @param drawAllowed True if the Character2 ability is been played this turn, false otherwise
     */
    public void setDrawAllowed(boolean drawAllowed) {
        this.drawAllowed = drawAllowed;
    }

    /**
     * getDrawAllowed()
     * @return True if the Character2 ability is been played this turn, false otherwise
     */
    public boolean getDrawAllowed() {
        return drawAllowed;
    }

    /**
     * getInfluencePolicy()
     * @return The policies for counting influence
     */
    public InfluenceCalculationPolicy getInfluencePolicy() {
        return influencePolicy;
    }

    /**
     * Gets the character of a certain class
     * @param cl Class of a character
     * @return The character of the given class
     * @throws IllegalMoveException When there aren't any characters of the given class
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
     * Resets all characters abilities
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
     * Gets the number of towers with a certain color that are on all islands
     * @param color TowerColor of a team
     * @return The number of towers with the given color that are on all islands
     */
    public int getTowersByColor(TowerColor color) {
        return (int) islands.stream().flatMap(island -> island.getTowers().stream()).filter(t -> t.getColor() == color).count();
    }

    /**
     * Gets the winning team
     * @return The winning team, or null in case of a draw
     */
    public Team getWinningTeam() {

        //set maxTowers to undefined value: -1, and allocate memory to order teams by maxTowers
        int maxTowers = -1;
        List<Team> teamsWithMaxTowers = new ArrayList<>(teams.size());

        //traverse teams
        for (Team t : teams) {

            //counter for towers associated to each team
            int teamTowers = getTowersByColor(t.getTowerColor());

            //sort teamsWithMaxTowers based on tower count
            if (teamTowers > maxTowers) {
                maxTowers = teamTowers;
                teamsWithMaxTowers.clear();
                teamsWithMaxTowers.add(t);
            } else if (teamTowers == maxTowers) {
                teamsWithMaxTowers.add(t);
            }
        }

        //if only one team in teamsWithMaxTowers
        if (teamsWithMaxTowers.size() == 1) {
            return teamsWithMaxTowers.get(0); //return that team
        }

        //set maxProfessors to undefined value: -1, and allocate memory to order teams by maxProfessors
        int maxProfessors = -1;
        List<Team> teamsWithMaxProfessors = new ArrayList<>(teams.size());

        //travers teams ordered by tower count! higher tower count goes first
        for (Team t : teamsWithMaxTowers) {

            //get professors associated to each team
            int teamProfessors = t.getPlayers().stream().mapToInt(p -> p.getSchool().getProfessors().size()).sum();

            //find team with max professors
            //check if current team has strictly more professors than the previous teams
            //tie goes to the team with more maxTowers
            if (teamProfessors > maxProfessors) {
                maxProfessors = teamProfessors;
                teamsWithMaxProfessors.clear();
                teamsWithMaxProfessors.add(t);
            } else if (teamProfessors == maxProfessors) {
                teamsWithMaxProfessors.add(t);
            }
        }

        //return team with max professor count
        return teamsWithMaxProfessors.size() == 1 ? teamsWithMaxProfessors.get(0) : null;
    }

    /**
     * isGameFinished()
     * @return True if the game is finished, false otherwise
     */
    public boolean isGameFinished() {
        return gameFinished;
    }

    /**
     * isLastTurn()
     * @return True if it's the last turn of the game, false otherwise
     */
    public boolean isLastTurn() {
        return lastTurn;
    }

    /**
     * Sets the current player in the turn
     * @param currentPlayer Name of the player that will become the current player
     */
    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
        updateView();
    }

    /**
     * Allows a player to use an assistant if available. Sets turn order of players via assistants played.
     * @param playerName The username of a player
     * @param value Value of the assistant that the player wants to use
     * @throws IllegalMoveException When the given assistant value is less than 1 or more than 10.
     * When the player with the given name doesn't have an assistant with the given value.
     * When an assistant with the given value has already been played this turn and the player with the given name has other options
     */
    public void useAssistant(String playerName, int value) throws IllegalMoveException {

        //get player data from name
        Player player = getPlayerFromName(playerName);
        int playerPos = getPosFromName(playerName);

        //check if requested assistant index is inBounds
        if (value < 1 || value > 10) {
            throw new IllegalMoveException("The value must be between 1 and 10");
        }

        //check if player has an assistant with requested index
        if (player.getAssistantFromValue(value) == null) {
            throw new IllegalMoveException("You don't have an assistant with value " + value);
        }

        //traverse all players
        for(int i = 0; i < playerPos; ++i) {

            //see if player has played assistant
            if (playerOrder.get(i).getCurrentAssistant().getValue() == value) {

                //allocate space for assistants players
                List<Assistant> assistants = new ArrayList<>();

                //collect all played assistants
                for (int j = 0; j < playerPos; ++j) {
                    assistants.add(playerOrder.get(j).getCurrentAssistant());
                }

                //check for duplicate assistants
                if (!assistants.containsAll(player.getAssistants())) {
                    throw new IllegalMoveException("You can't use this assistant because another player already used it this turn");
                }
                break;
            }
        }

        //set turn order of players via assistants played
        getPlayerFromName(playerName).setCurrentAssistant(getPlayerFromName(playerName).getAssistantFromValue(value));
        if(getPlayerFromName(playerName).getAssistants().isEmpty()) {
            lastTurn = true;
        }
        if(getPosFromName(playerName) == playerOrder.size() - 1) {
            playerOrder.sort(Comparator.comparingInt((Player p) -> p.getCurrentAssistant().getValue()));
        }
    }

    /**
     * Moves 3 students from a player entrance to an island and/or to his tables
     * @param playerName The username of a player
     * @param islandsStudents Students that the player wants to move from his entrance to an island
     * @param tableStudents Students that the player wants to move from his entrance to his tables
     * @throws IllegalMoveException When the player wants to move a different amount of students than 3.
     * When an island index doesn't exist
     */
    public void moveStudentsToIslandsAndTable(String playerName, Map<Integer, Map<PawnColor, Integer>> islandsStudents, Map<PawnColor, Integer> tableStudents) throws IllegalMoveException {
        //Check that the player has moved exactly three students
        if (islandsStudents.values().stream().flatMap(m -> m.entrySet().stream()).mapToInt(Map.Entry::getValue).sum() + tableStudents.values().stream().mapToInt(Integer::intValue).sum() != 3) {
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
     * Updates the status of the match, and push updated match data to the View
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