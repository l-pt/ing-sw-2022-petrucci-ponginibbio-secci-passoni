package it.polimi.ingsw.server.protocol.message.character;

import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

/**
 * Message sent by the client in case an island character will be  used
 */
public class UseCharacterIslandMessage extends Message {
    private int characterId;
    private int island;

    public UseCharacterIslandMessage(int characterId, int island) {
        super(MessageId.USE_CHARACTER_ISLAND);
        this.characterId = characterId;
        this.island = island;
    }

    public int getCharacterId() {
        return characterId;
    }

    public int getIsland() {
        return island;
    }
}
