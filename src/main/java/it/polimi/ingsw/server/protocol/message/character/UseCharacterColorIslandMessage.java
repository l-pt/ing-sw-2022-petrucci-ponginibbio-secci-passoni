package it.polimi.ingsw.server.protocol.message.character;

import it.polimi.ingsw.model.PawnColor;
import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

/**
 * Only used for Character1
 */
public class UseCharacterColorIslandMessage extends Message {
    private final PawnColor color;
    private final int island;

    public UseCharacterColorIslandMessage(PawnColor color, int island) {
        super(MessageId.USE_CHARACTER_COLOR_ISLAND);
        this.color = color;
        this.island = island;
    }

    /**
     * getColor()
     * @return The student color
     */
    public PawnColor getColor() {
        return color;
    }

    /**
     * getIsland()
     * @return The index of the island
     */
    public int getIsland() {
        return island;
    }
}
