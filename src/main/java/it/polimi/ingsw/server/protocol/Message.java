package it.polimi.ingsw.server.protocol;

/**
 * Base class for messages sent between server and clients
 * All subclasses are in the package it.polimi.ingsw.protocol.message
 */
public abstract class Message {
    private MessageId messageId;

    public Message(MessageId messageId) {
        this.messageId = messageId;
    }

    public MessageId getMessageId() {
        return messageId;
    }
}
