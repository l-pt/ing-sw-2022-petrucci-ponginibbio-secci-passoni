package it.polimi.ingsw.protocol.message;

import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;

public class AskMotherNatureMessage extends Message {
    public AskMotherNatureMessage() {
        super(MessageId.ASK_MOTHER_NATURE);
    }
}
