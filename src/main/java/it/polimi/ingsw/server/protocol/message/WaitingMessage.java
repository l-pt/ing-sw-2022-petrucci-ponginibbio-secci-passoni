package it.polimi.ingsw.server.protocol.message;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

/**
 * Message sent to notify the client that the game is in a waiting state.
 */
public class WaitingMessage extends Message {
    private final String message;

    public WaitingMessage(String message) {
        super(MessageId.WAITING);
        this.message = message;
    }

    /**
     * getMessage()
     * @return The message string
     */
    public String getMessage() {
            return message;
        }

}
