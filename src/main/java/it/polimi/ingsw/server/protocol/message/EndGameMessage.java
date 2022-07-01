package it.polimi.ingsw.server.protocol.message;

import it.polimi.ingsw.model.Team;
import it.polimi.ingsw.server.protocol.Message;
import it.polimi.ingsw.server.protocol.MessageId;

/**
 * Game Over: Message sent by the server
 */
public class EndGameMessage extends Message {
    private Team winner;

    public EndGameMessage(Team winner) {
        super(MessageId.END_GAME);
        this.winner = winner;
    }

    /**
     * getWinner()
     * @return The winning Team
     */
    public Team getWinner() {
        return winner;
    }
}
