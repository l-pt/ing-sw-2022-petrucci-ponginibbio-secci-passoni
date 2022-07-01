package it.polimi.ingsw.server.protocol.message.character;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

/**
 * Used for characters without parameters like Character2
 */
public class UseCharacterMessage extends Message {
    private int characterId;

    public UseCharacterMessage(int characterId) {
        super(MessageId.USE_CHARACTER);
        this.characterId = characterId;
    }

    /**
     * getCharacterId()
     * @return The character id
     */
    public int getCharacterId() {
        return characterId;
    }
}
