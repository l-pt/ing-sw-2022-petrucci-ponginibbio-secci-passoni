package it.polimi.ingsw.server.protocol.message;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

/**
 * Message sent by the server to ask whether to activate expert mode
 */
public class AskExpertMessage extends Message {
    public AskExpertMessage() {
        super(MessageId.ASK_EXPERT);
    }
}
