package it.polimi.ingsw.server.protocol.message;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

public class AskCloudMessage extends Message {
    public AskCloudMessage() {
        super(MessageId.ASK_CLOUD);
    }
}
