package it.polimi.ingsw.server.protocol.message.character;

import it.polimi.ingsw.model.PawnColor;
import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

/**
 * Message sent by the client in case a color character will be used
 */
public class UseCharacterColorMessage extends Message {
    private int characterId;
    private PawnColor color;

    public UseCharacterColorMessage(int characterId, PawnColor color) {
        super(MessageId.USE_CHARACTER_COLOR);
        this.characterId = characterId;
        this.color = color;
    }

    public int getCharacterId() {
        return characterId;
    }

    public PawnColor getColor() {
        return color;
    }
}
