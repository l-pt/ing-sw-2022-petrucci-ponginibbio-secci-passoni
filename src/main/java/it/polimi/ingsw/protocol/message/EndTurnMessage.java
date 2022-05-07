package it.polimi.ingsw.protocol.message;

import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;

public class EndTurnMessage extends Message {
    public EndTurnMessage() {
        super(MessageId.END_TURN);
    }
}
