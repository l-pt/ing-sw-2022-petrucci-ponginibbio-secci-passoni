package it.polimi.ingsw.protocol.message;

import it.polimi.ingsw.model.Team;
import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.MessageId;

public class EndGameMessage extends Message {
    private Team winner;

    public EndGameMessage(Team winner) {
        super(MessageId.END_GAME);
        this.winner = winner;
    }

    private EndGameMessage() {
        super(MessageId.END_GAME);
    }

    public Team getWinner() {
        return winner;
    }
}
