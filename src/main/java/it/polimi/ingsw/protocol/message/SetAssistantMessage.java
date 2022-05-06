package it.polimi.ingsw.protocol.message;

import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;

public class SetAssistantMessage extends Message {
    private int assisant;

    private SetAssistantMessage() {
        super(MessageId.SET_ASSISTANT);
    }

    public SetAssistantMessage(int assistant) {
        super(MessageId.SET_ASSISTANT);
        this.assisant = assistant;
    }

    public int getAssisant() {
        return assisant;
    }
}
