package it.polimi.ingsw.server.protocol.message.character;

import it.polimi.ingsw.model.PawnColor;
import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

import java.util.Map;

/**
 * Message sent by the client in case a student-map character will be used
 */
public class UseCharacterStudentMapMessage extends Message {
    private int characterId;
    private Map<PawnColor, Integer> inMap;
    private Map<PawnColor, Integer> outMap;

    public UseCharacterStudentMapMessage(int characterId, Map<PawnColor, Integer> inMap, Map<PawnColor, Integer> outMap) {
        super(MessageId.USE_CHARACTER_STUDENT_MAP);
        this.characterId = characterId;
        this.inMap = inMap;
        this.outMap = outMap;
    }

    /**
     * getCharacterId()
     * @return The character id
     */
    public int getCharacterId() {
        return characterId;
    }

    /**
     * getInMap()
     * @return The in-map of the students to move
     */
    public Map<PawnColor, Integer> getInMap() {
        return inMap;
    }

    /**
     * getOutMap()
     * @return The out-map of the students to move
     */
    public Map<PawnColor, Integer> getOutMap() {
        return outMap;
    }
}
