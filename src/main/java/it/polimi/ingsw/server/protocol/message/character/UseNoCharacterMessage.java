package it.polimi.ingsw.server.protocol.message.character;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

public class UseNoCharacterMessage extends Message {
    public UseNoCharacterMessage() {
        super(MessageId.USE_NO_CHARACTER);
    }
}
