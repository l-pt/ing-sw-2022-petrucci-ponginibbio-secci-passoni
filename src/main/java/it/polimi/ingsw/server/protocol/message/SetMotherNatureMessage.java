package it.polimi.ingsw.server.protocol.message;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

public class SetMotherNatureMessage extends Message {
    private int motherNatureMoves;

    public SetMotherNatureMessage(int motherNatureMoves) {
        super(MessageId.SET_MOTHER_NATURE);
        this.motherNatureMoves = motherNatureMoves;
    }

    public int getMotherNatureMoves() {
        return motherNatureMoves;
    }
}
