package it.polimi.ingsw.protocol.message.character;

import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;

public class AskCharacterMessage extends Message {
    private int characterId;

    public AskCharacterMessage(int characterId) {
        super(MessageId.ASK_CHARACTER);
        this.characterId = characterId;
    }

    public AskCharacterMessage() {
        super(MessageId.ASK_CHARACTER);
        characterId = -1;
    }

    public int getCharacterId() {
        return characterId;
    }
}
