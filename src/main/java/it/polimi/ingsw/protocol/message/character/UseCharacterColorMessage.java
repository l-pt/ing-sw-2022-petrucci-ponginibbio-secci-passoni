package it.polimi.ingsw.protocol.message.character;

import it.polimi.ingsw.model.PawnColor;
import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;

public class UseCharacterColorMessage extends Message {
    private int characterId;
    private PawnColor color;

    public UseCharacterColorMessage(int characterId, PawnColor color) {
        super(MessageId.USE_CHARACTER_COLOR);
        this.characterId = characterId;
        this.color = color;
    }

    private UseCharacterColorMessage() {
        super(MessageId.USE_CHARACTER_COLOR);
    }

    public int getCharacterId() {
        return characterId;
    }

    public PawnColor getColor() {
        return color;
    }
}
