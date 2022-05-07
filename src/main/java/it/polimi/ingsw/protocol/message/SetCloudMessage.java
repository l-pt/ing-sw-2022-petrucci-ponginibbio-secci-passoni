package it.polimi.ingsw.protocol.message;

import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;

public class SetCloudMessage extends Message {
    private int cloud;

    public SetCloudMessage(int cloud) {
        super(MessageId.SET_CLOUD);
        this.cloud = cloud;
    }

    private SetCloudMessage() {
        super(MessageId.SET_CLOUD);
    }

    public int getCloud() {
        return cloud;
    }
}
