package it.polimi.ingsw.server.protocol.message;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

/**
 * Message sent by client to update assistant based on AskAssistantMessage
 */
public class SetAssistantMessage extends Message {
    private int assistant;

    public SetAssistantMessage(int assistant) {
        super(MessageId.SET_ASSISTANT);
        this.assistant = assistant;
    }

    public int getAssistant() {
        return assistant;
    }
}
