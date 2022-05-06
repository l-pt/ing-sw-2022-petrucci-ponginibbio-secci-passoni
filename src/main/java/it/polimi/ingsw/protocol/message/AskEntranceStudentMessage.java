package it.polimi.ingsw.protocol.message;

import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;

public class AskEntranceStudentMessage extends Message {
    public AskEntranceStudentMessage() {
        super(MessageId.ASK_ENTRANCE_STUDENT);
    }
}
