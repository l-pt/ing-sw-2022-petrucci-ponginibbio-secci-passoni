package it.polimi.ingsw.server.protocol.message;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

/**
 * Message sent by the server to ask about clouds
 */
public class AskCloudMessage extends Message {
    public AskCloudMessage() {
        super(MessageId.ASK_CLOUD);
    }
}
