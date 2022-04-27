package it.polimi.ingsw.protocol.message;

import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;

/**
 * Message sent by the server to ask whether to activate expert mode
 */
public class AskExpertMessage extends Message {
    public AskExpertMessage() {
        super(MessageId.ASK_EXPERT);
    }
}
