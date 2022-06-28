package it.polimi.ingsw.server.protocol.message;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

public class WaitingMessage extends Message {
    private String message;

    public WaitingMessage(String message) {
        super(MessageId.WAITING);
        this.message = message;
    }

    public String getMessage() {
            return message;
        }

}
