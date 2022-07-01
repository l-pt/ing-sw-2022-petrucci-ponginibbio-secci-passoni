package it.polimi.ingsw.server.protocol.message;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

/**
 * Message sent by the server to ask about mother nature
 */
public class AskMotherNatureMessage extends Message {
    public AskMotherNatureMessage() {
        super(MessageId.ASK_MOTHER_NATURE);
    }
}
