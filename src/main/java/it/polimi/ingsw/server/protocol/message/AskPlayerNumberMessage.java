package it.polimi.ingsw.server.protocol.message;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

/**
 * Message sent by the server to ask the number of players for
 * the current match
 */
public class AskPlayerNumberMessage extends Message {
    public AskPlayerNumberMessage() {
        super(MessageId.ASK_PLAYER_NUMBER);
    }
}
