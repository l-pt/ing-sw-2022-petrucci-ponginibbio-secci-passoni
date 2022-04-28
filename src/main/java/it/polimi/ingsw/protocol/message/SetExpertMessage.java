package it.polimi.ingsw.protocol.message;

import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;

/**
 * Message sent by the client to activate or deactivate the
 * expert mode for the upcoming match
 */
public class SetExpertMessage extends Message {
    private boolean expert;

    private SetExpertMessage() {
        super(MessageId.SET_PLAYER_NUMBER);
    }

    public SetExpertMessage(boolean expert) {
        super(MessageId.SET_EXPERT);
        this.expert = expert;
    }

    public boolean getExpert() {
        return expert;
    }
}
