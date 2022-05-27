package it.polimi.ingsw.model;

import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlayerTest extends TestCase {
    @Test
    public void assistantFromValueTest() {
        Player player = new Player("test", TowerColor.BLACK, Wizard.BLUE);
        Assertions.assertNull(player.getAssistantFromValue(11));
        Assertions.assertEquals(Wizard.BLUE, player.getWizard());

        Assistant assistant = player.getAssistantFromValue(1);
        Assertions.assertNotNull(assistant);
        Assertions.assertEquals(assistant.getValue(), 1);
        Assertions.assertEquals(1, assistant.getMoves());
    }

    @Test
    public void currentAssistantTest() {
        Player player = new Player("test", TowerColor.BLACK, Wizard.BLUE);

        Assistant assistant = player.getAssistantFromValue(1);
        player.setCurrentAssistant(assistant);
        Assertions.assertEquals(player.getCurrentAssistant(), assistant);

        Assertions.assertNull(player.getAssistantFromValue(1));

        player.setCurrentAssistant(player.getAssistantFromValue(2));
        Assertions.assertEquals(player.getDiscardPile(), assistant);
    }

    @Test
    public void coinsTest() {
        Player player = new Player("test", TowerColor.BLACK, Wizard.BLUE);

        Assertions.assertEquals(player.getCoins(), 0);

        player.addCoin();
        Assertions.assertEquals(player.getCoins(), 1);

        player.removeCoins(1);
        Assertions.assertEquals(player.getCoins(), 0);

        Exception e = assertThrows(IllegalArgumentException.class, () -> player.removeCoins(1));
        Assertions.assertEquals(e.getMessage(), "Player " + player.getName() + "does not have enough coins");
    }
}
