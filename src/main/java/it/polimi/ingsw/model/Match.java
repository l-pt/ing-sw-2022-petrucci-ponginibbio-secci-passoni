package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Match {
    private List<Team> teams;
    private List<Island> islands;
    private List<Cloud> clouds;
    private List<Student> studentBag;
    private List<Professor> professors;
    private List<Character> characters;
    private int coinsReserve;
    private boolean expert;
    private List<Player> playerOrder;
    private int id;

    public Match(List<Team> teams, List<Character> allCharacters, boolean expert, List<Player> playerOrder, int id) {
        Random randomGenerator = new Random();

        islands = new ArrayList<>();
        int motherNaturePos = randomGenerator.nextInt(12);
        for (int i = 0; i < 12; ++i) {
            islands.add(new Island(i == motherNaturePos));
        }

        clouds = new ArrayList<>();
        for (int i = 0; i < playerOrder.size(); ++i) {
            clouds.add(new Cloud());
        }

        studentBag = new ArrayList<>();
        for (PawnColor color : PawnColor.values()) {
            for (int i = 1; i <= 26; ++i) {
                studentBag.add(new Student(color));
            }
        }

        professors = new ArrayList<>();
        for (PawnColor color : PawnColor.values()) {
            professors.add(new Professor(color));
        }

        characters = new ArrayList<>(3);
        while (characters.size() != 3) {
            int randomIndex = randomGenerator.nextInt(allCharacters.size());
            if (!characters.contains(allCharacters.get(randomIndex))) {
                characters.add(allCharacters.get(randomIndex));
            }
        }

        coinsReserve = 20 - playerOrder.size();
        this.expert = expert;
        this.id = id;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void orderPlayers() {
        //TODO
    }

    public List<Island> getIslands() {
        return islands;
    }

    public List<Cloud> getClouds() {
        return clouds;
    }

    public List<Student> extractStudent(int n) {
        //TODO
        return null;
    }

    public void removeProfessor(Professor professor) {
        professors.remove(professor);
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

    public List<Player> getPlayers() {
        return playerOrder;
    }

    public int getId() {
        return id;
    }
}
