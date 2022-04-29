package it.polimi.ingsw.protocol.message;

import it.polimi.ingsw.model.Assistant;
import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;

import java.util.List;

public class InitialAssistantMessage extends Message {
    private List<Assistant> assistants;

    public InitialAssistantMessage(List<Assistant> assistants){
        super(MessageId.INITIAL_ASSISTANT);
        this.assistants = assistants;
    }

    private InitialAssistantMessage() {
        super(MessageId.INITIAL_ASSISTANT);
    }

    public List<Assistant> getAssistants() {
        return assistants;
    }
}
