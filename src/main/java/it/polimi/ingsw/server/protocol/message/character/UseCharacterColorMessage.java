package it.polimi.ingsw.server.protocol.message.character;

import it.polimi.ingsw.model.PawnColor;
import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

/**
 * Message sent by the client in case a color character will be used
 */
public class UseCharacterColorMessage extends Message {
    private final int characterId;
    private final PawnColor color;

    public UseCharacterColorMessage(int characterId, PawnColor color) {
        super(MessageId.USE_CHARACTER_COLOR);
        this.characterId = characterId;
        this.color = color;
    }

    /**
     * getCharacterId()
     * @return The character id
     */
    public int getCharacterId() {
        return characterId;
    }

    /**
     * getColor()
     * @return The student color
     */
    public PawnColor getColor() {
        return color;
    }
}
