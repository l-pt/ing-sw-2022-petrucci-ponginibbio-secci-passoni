package it.polimi.ingsw.protocol.message.character;

import it.polimi.ingsw.model.PawnColor;
import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;

/**
 * Only used for Character1
 */
public class UseCharacterColorIslandMessage extends Message {
    private PawnColor color;
    private int island;

    public UseCharacterColorIslandMessage(PawnColor color, int island) {
        super(MessageId.USE_CHARACTER_COLOR_ISLAND);
        this.color = color;
        this.island = island;
    }

    private UseCharacterColorIslandMessage() {
        super(MessageId.USE_CHARACTER_COLOR_ISLAND);
    }

    public PawnColor getColor() {
        return color;
    }

    public int getIsland() {
        return island;
    }
}
