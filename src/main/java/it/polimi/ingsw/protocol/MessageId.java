package it.polimi.ingsw.protocol;

import it.polimi.ingsw.protocol.message.*;

/**
 * An enum that contains all message ids and classes
 */
public enum MessageId {
    ERROR(ErrorMessage.class),
    ASK_USERNAME(AskUsernameMessage.class),
    SET_USERNAME(SetUsernameMessage.class),
    ASK_PLAYER_NUMBER(AskPlayerNumberMessage.class),
    SET_PLAYER_NUMBER(SetPlayerNumberMessage.class),
    ASK_EXPERT(AskExpertMessage.class),
    SET_EXPERT(SetExpertMessage.class),
    UPDATE_VIEW(UpdateViewMessage.class),
    ASK_ASSISTANT(AskAssistantMessage.class),
    SET_ASSISTANT(SetAssistantMessage.class),
    ASK_ENTRANCE_STUDENT(AskEntranceStudentMessage.class);

    private Class<? extends Message> messageClass;

    MessageId(Class<? extends Message> messageClass) {
        this.messageClass = messageClass;
    }

    public Class<? extends Message> getMessageClass() {
        return messageClass;
    }
}
