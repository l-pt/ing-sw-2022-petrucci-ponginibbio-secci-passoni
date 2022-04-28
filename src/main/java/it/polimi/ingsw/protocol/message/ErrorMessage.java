package it.polimi.ingsw.protocol.message;

import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;

/**
 * Signal an error to the client
 */
public class ErrorMessage extends Message {
    private String error;

    private ErrorMessage() {
        super(MessageId.ERROR);
    }

    public ErrorMessage(String error) {
        super(MessageId.ERROR);
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
