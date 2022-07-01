package it.polimi.ingsw.server.protocol;

/**
 * Base class for messages sent between server and clients
 * All subclasses are in the package it.polimi.ingsw.protocol.message
 */
public abstract class Message {
    private MessageId messageId;

    /**
     * Contructor for abstract Message class
     * @param messageId
     */
    public Message(MessageId messageId) {
        this.messageId = messageId;
    }

    /**
     * getMessageId()
     * @return The message id
     */
    public MessageId getMessageId() {
        return messageId;
    }
}
