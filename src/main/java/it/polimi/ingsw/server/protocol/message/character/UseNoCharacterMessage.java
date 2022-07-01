package it.polimi.ingsw.server.protocol.message.character;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

/**
 * Message sent by the client in case no characters will be used
 */
public class UseNoCharacterMessage extends Message {
    public UseNoCharacterMessage() {
        super(MessageId.USE_NO_CHARACTER);
    }
}
