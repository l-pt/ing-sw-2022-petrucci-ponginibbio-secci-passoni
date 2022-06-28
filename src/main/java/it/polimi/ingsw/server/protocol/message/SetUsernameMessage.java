package it.polimi.ingsw.server.protocol.message;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

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