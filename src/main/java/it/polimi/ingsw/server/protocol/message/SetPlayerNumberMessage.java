package it.polimi.ingsw.server.protocol.message;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

/**
 * Message sent by the client to set the number of
 * players for the upcoming match
 */
public class SetPlayerNumberMessage extends Message {
    private int playersNumber;

    public SetPlayerNumberMessage(int playersNumber) {
        super(MessageId.SET_PLAYER_NUMBER);
        this.playersNumber = playersNumber;
    }

    public int getPlayersNumber() {
        return playersNumber;
    }
}
