package it.polimi.ingsw.protocol.message.character;

import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;

public class UseNoCharacterMessage extends Message {
    public UseNoCharacterMessage() {
        super(MessageId.USE_NO_CHARACTER);
    }
}
