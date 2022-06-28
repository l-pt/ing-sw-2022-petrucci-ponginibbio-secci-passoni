package it.polimi.ingsw.server.protocol.message;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

public class AskAssistantMessage extends Message {
    public AskAssistantMessage() {
        super(MessageId.ASK_ASSISTANT);
    }
}
