package it.polimi.ingsw.server.protocol.message;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

/**
 * Message sent by client to update mother nature based on AskMotherNatureMessage
 */
public class SetMotherNatureMessage extends Message {
    private final int motherNatureMoves;

    public SetMotherNatureMessage(int motherNatureMoves) {
        super(MessageId.SET_MOTHER_NATURE);
        this.motherNatureMoves = motherNatureMoves;
    }

    /**
     * getMotherNatureMoves()
     * @return The number of mother nature moves
     */
    public int getMotherNatureMoves() {
        return motherNatureMoves;
    }
}
