package it.polimi.ingsw.server.protocol.message;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

/**
 * Message sent by client to update assistant based on AskAssistantMessage
 */
public class SetAssistantMessage extends Message {
    private final int assistant;

    public SetAssistantMessage(int assistant) {
        super(MessageId.SET_ASSISTANT);
        this.assistant = assistant;
    }

    /**
     * getAssistant()
     * @return The assistant value
     */
    public int getAssistant() {
        return assistant;
    }
}
