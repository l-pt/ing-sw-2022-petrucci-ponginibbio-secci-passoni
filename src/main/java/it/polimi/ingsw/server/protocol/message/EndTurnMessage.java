package it.polimi.ingsw.server.protocol.message;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

public class EndTurnMessage extends Message {
    public EndTurnMessage() {
        super(MessageId.END_TURN);
    }
}
