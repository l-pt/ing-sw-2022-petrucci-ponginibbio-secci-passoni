package it.polimi.ingsw.protocol.message;

import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;

public class WaitingMessage extends Message{
    private String message;

    public WaitingMessage(String message) {
        super(MessageId.WAITING);
        this.message = message;
    }

    public String getMessage() {
            return message;
        }

}
