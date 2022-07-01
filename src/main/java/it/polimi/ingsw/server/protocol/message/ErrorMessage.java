package it.polimi.ingsw.server.protocol.message;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

/**
 * Signal an error to the client
 */
public class ErrorMessage extends Message {
    private String error;

    public ErrorMessage(String error) {
        super(MessageId.ERROR);
        this.error = error;
    }

    /**
     * getError()
     * @return The error string
     */
    public String getError() {
        return error;
    }
}
