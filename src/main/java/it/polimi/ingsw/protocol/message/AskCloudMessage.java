package it.polimi.ingsw.protocol.message;

import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;

public class AskCloudMessage extends Message {
    public AskCloudMessage() {
        super(MessageId.ASK_CLOUD);
    }
}
