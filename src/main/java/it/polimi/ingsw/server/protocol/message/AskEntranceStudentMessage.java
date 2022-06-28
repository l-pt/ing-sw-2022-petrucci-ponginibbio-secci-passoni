package it.polimi.ingsw.server.protocol.message;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

public class AskEntranceStudentMessage extends Message {
    public AskEntranceStudentMessage() {
        super(MessageId.ASK_ENTRANCE_STUDENT);
    }
}
