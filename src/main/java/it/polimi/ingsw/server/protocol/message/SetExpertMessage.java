package it.polimi.ingsw.server.protocol.message;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

/**
 * Message sent by the client to activate or deactivate the
 * expert mode for the upcoming match
 */
public class SetExpertMessage extends Message {
    private final boolean expert;

    public SetExpertMessage(boolean expert) {
        super(MessageId.SET_EXPERT);
        this.expert = expert;
    }

    /**
     * getExpert()
     * @return The boolean expert mode
     */
    public boolean getExpert() {
        return expert;
    }
}
