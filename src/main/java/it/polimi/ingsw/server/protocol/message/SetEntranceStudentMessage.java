package it.polimi.ingsw.server.protocol.message;

import it.polimi.ingsw.model.PawnColor;
import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

import java.util.Map;

public class SetEntranceStudentMessage extends Message {
    Map<Integer, Map<PawnColor, Integer>> islandStudents;
    Map<PawnColor, Integer> tableStudents;

    public SetEntranceStudentMessage(Map<Integer, Map<PawnColor, Integer>> islandStudents, Map<PawnColor, Integer> tableStudents) {
        super(MessageId.SET_ENTRANCE_STUDENT);
        this.islandStudents = islandStudents;
        this.tableStudents = tableStudents;
    }

    public Map<Integer, Map<PawnColor, Integer>> getIslandStudents() {
        return islandStudents;
    }

    public Map<PawnColor, Integer> getTableStudents() {
        return tableStudents;
    }
}
