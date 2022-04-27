package it.polimi.ingsw.protocol.message;

import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;

/**
 * Message sent by the server to ask a clients username
 */
public class AskUsernameMessage extends Message {
    public AskUsernameMessage() {
        super(MessageId.ASK_USERNAME);
    }
}
