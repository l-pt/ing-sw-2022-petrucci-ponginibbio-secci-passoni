package it.polimi.ingsw.server.protocol;

import it.polimi.ingsw.server.protocol.message.*;
import it.polimi.ingsw.server.protocol.message.character.*;

/**
 * An ENUM that contains all message ids and classes
 */
public enum MessageId {
    ERROR(ErrorMessage.class),
    WAITING(WaitingMessage.class),
    ASK_USERNAME(AskUsernameMessage.class),
    SET_USERNAME(SetUsernameMessage.class),
    ASK_PLAYER_NUMBER(AskPlayerNumberMessage.class),
    SET_PLAYER_NUMBER(SetPlayerNumberMessage.class),
    ASK_EXPERT(AskExpertMessage.class),
    SET_EXPERT(SetExpertMessage.class),
    UPDATE_VIEW(UpdateViewMessage.class),
    ASK_ASSISTANT(AskAssistantMessage.class),
    SET_ASSISTANT(SetAssistantMessage.class),
    ASK_ENTRANCE_STUDENT(AskEntranceStudentMessage.class),
    SET_ENTRANCE_STUDENT(SetEntranceStudentMessage.class),
    ASK_MOTHER_NATURE(AskMotherNatureMessage.class),
    SET_MOTHER_NATURE(SetMotherNatureMessage.class),
    ASK_CLOUD(AskCloudMessage.class),
    SET_CLOUD(SetCloudMessage.class),
    END_TURN(EndTurnMessage.class),
    USE_CHARACTER_COLOR_ISLAND(UseCharacterColorIslandMessage.class),
    USE_CHARACTER(UseCharacterMessage.class),
    USE_CHARACTER_ISLAND(UseCharacterIslandMessage.class),
    USE_CHARACTER_STUDENT_MAP(UseCharacterStudentMapMessage.class),
    USE_CHARACTER_COLOR(UseCharacterColorMessage.class),
    END_GAME(EndGameMessage.class),
    ASK_CHARACTER(AskCharacterMessage.class),
    USE_NO_CHARACTER(UseNoCharacterMessage.class);

    private Class<? extends Message> messageClass;

    /**
     * Contructor that takes in messageClass to generate unique MessageId
     * @param messageClass java metatype for Message
     */
    MessageId(Class<? extends Message> messageClass) {
        this.messageClass = messageClass;
    }

    /**
     * Getter for this MessageId.messageClass
     * @return this.messageClass
     */
    public Class<? extends Message> getMessageClass() {
        return messageClass;
    }
}
