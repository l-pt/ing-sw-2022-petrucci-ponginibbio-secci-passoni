package it.polimi.ingsw.protocol.message;

import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;

public class AskAssistantMessage extends Message {
    public AskAssistantMessage() {
        super(MessageId.ASK_ASSISTANT);
    }
}
