package it.polimi.ingsw.protocol.message;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;

import java.util.List;

public class UpdateViewMessage extends Message {
    private List<Assistant> assistants;
    private List<Island> islands;
    private List<Player> playersOrder;
    private int posMotherNature;
    private List<Cloud> clouds;
    private List<Professor> professors;
    private int coinReserve;

    public UpdateViewMessage(
        List<Assistant> assistants,
        List<Island> islands,
        List<Player> playersOrder,
        int posMotherNature,
        List<Cloud> clouds,
        List<Professor> professors,
        int coinReserve
    ){
        super(MessageId.UPDATE_VIEW);
        this.assistants = assistants;
        this.islands = islands;
        this.playersOrder = playersOrder;
        this.posMotherNature = posMotherNature;
        this.clouds = clouds;
        this.professors = professors;
        this.coinReserve = coinReserve;
    }

    private UpdateViewMessage() {
        super(MessageId.UPDATE_VIEW);
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
