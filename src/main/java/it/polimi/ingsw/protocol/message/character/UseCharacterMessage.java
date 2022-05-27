package it.polimi.ingsw.protocol.message.character;

import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;

/**
 * Used for characters without parameters like Character2
 */
public class UseCharacterMessage extends Message {
    private int characterId;

    public UseCharacterMessage(int characterId) {
        super(MessageId.USE_CHARACTER);
        this.characterId = characterId;
    }

    public int getCharacterId() {
        return characterId;
    }
}
