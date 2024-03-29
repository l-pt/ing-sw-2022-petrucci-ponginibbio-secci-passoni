package it.polimi.ingsw.server.protocol.message.character;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

/**
 * Message sent by the server to ask about playing characters
 */
public class AskCharacterMessage extends Message {
    private final int characterId;

    public AskCharacterMessage(int characterId) {
        super(MessageId.ASK_CHARACTER);
        this.characterId = characterId;
    }

    public AskCharacterMessage() {
        super(MessageId.ASK_CHARACTER);
        characterId = -1;
    }

    /**
     * getCharacterId()
     * @return The character id
     */
    public int getCharacterId() {
        return characterId;
    }
}
