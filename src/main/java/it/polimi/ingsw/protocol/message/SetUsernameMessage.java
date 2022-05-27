package it.polimi.ingsw.protocol.message;

import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;

/**
 * Message sent by the client to tell its username
 */
public class SetUsernameMessage extends Message {
    private String username;

    public SetUsernameMessage(String username) {
        super(MessageId.SET_USERNAME);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
