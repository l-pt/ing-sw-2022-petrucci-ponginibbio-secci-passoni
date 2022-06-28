package it.polimi.ingsw.server.protocol.message;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

/**
 * Message sent by the server to ask a clients username
 */
public class AskUsernameMessage extends Message {
    public AskUsernameMessage() {
        super(MessageId.ASK_USERNAME);
    }
}
