package it.polimi.ingsw.server.protocol.message;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

/**
 * Message sent by client to update cloud based on AskCloudMessage
 */
public class SetCloudMessage extends Message {
    private int cloud;

    public SetCloudMessage(int cloud) {
        super(MessageId.SET_CLOUD);
        this.cloud = cloud;
    }

    public int getCloud() {
        return cloud;
    }
}
