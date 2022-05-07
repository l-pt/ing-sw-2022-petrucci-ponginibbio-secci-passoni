package it.polimi.ingsw.protocol.message;

import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;

public class SetMotherNatureMessage extends Message {
    private int motherNatureMoves;

    public SetMotherNatureMessage(int motherNatureMoves) {
        super(MessageId.SET_MOTHER_NATURE);
        this.motherNatureMoves = motherNatureMoves;
    }

    private SetMotherNatureMessage() {
        super(MessageId.SET_MOTHER_NATURE);
    }

    public int getMotherNatureMoves() {
        return motherNatureMoves;
    }
}
