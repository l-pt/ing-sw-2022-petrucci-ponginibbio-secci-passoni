package it.polimi.ingsw.server.protocol.message;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

/**
 * Message sent by the server to signal end of turn
 */
public class EndTurnMessage extends Message {
    public EndTurnMessage() {
        super(MessageId.END_TURN);
    }
}
