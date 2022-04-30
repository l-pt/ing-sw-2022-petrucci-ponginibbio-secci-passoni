package it.polimi.ingsw.client;


import it.polimi.ingsw.model.*;
import it.polimi.ingsw.protocol.message.UpdateViewMessage;

import java.util.List;
import java.util.stream.Collectors;

public class ViewCLI {
    private ClientCLI client;
    private List<Assistant> assistants;
    private List<Island> islands;
    private List<Player> playersOrder;
    private int posMotherNature;
    private List<Cloud> clouds;
    private List<Professor> professors;
    private int coinReserve;

    public ViewCLI(ClientCLI client) {
        this.client = client;
    }

    public void handleUpdateView(UpdateViewMessage message) {
        assistants = message.getAssistants();
        islands = message.getIslands();
        playersOrder = message.getPlayersOrder();
        posMotherNature = message.getPosMotherNature();
        clouds = message.getClouds();
        professors = message.getProfessors();
        coinReserve = message.getCoinReserve();

        print();
    }

    public void print() {
        System.out.println("--- Assistants ---");
        int i = 0;
        for (Assistant assistant : assistants) {
            System.out.println("Assistant " + i + " - Value: " + assistant.getValue() + ", Moves: " + assistant.getMoves());
            ++i;
        }

        System.out.println("--- Islands ---");
        i = 0;
        for (Island island : islands) {
            System.out.println("Island " + i + ": ");
            //TODO
            ++i;
        }

        System.out.println("--- Players ---");
        System.out.println(playersOrder.stream().map(Player::getName).collect(Collectors.joining(", ")));

        System.out.println("-- Other info ---");
        System.out.println("Mother nature is on island " + posMotherNature);
        System.out.println("Coin reserve " + coinReserve);

        //TODO all the rest
    }

    public List<Assistant> getAssistants() {
        return assistants;
    }

    public List<Island> getIslands() {
        return islands;
    }

    public List<Player> getPlayersOrder() {
        return playersOrder;
    }

    public int getPosMotherNature() {
        return posMotherNature;
    }

    public List<Cloud> getClouds() {
        return clouds;
    }

    public List<Professor> getProfessors() {
        return professors;
    }

    public int getCoinReserve() {
        return coinReserve;
    }
}
