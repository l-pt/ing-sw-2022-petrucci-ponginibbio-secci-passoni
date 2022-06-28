package it.polimi.ingsw.server.protocol.message;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

public class AskMotherNatureMessage extends Message {
    public AskMotherNatureMessage() {
        super(MessageId.ASK_MOTHER_NATURE);
    }
}
