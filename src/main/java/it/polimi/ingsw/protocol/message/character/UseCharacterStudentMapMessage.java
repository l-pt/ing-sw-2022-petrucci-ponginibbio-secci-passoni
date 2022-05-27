package it.polimi.ingsw.protocol.message.character;

import it.polimi.ingsw.model.PawnColor;
import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;

import java.util.Map;

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

    public int getCharacterId() {
        return characterId;
    }

    public Map<PawnColor, Integer> getInMap() {
        return inMap;
    }

    public Map<PawnColor, Integer> getOutMap() {
        return outMap;
    }
}
