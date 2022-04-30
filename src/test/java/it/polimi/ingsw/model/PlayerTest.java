package it.polimi.ingsw.model;

import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlayerTest extends TestCase {
    @Test
    public void assistantFromValueTest() {
        Player player = new Player("test", TowerColor.BLACK, Wizard.BLUE);
        assertNull(player.getAssistantFromValue(11));

        Assistant assistant = player.getAssistantFromValue(1);
        assertNotNull(assistant);
        assertEquals(assistant.getValue(), 1);
    }

    @Test
    public void currentAssistantTest() {
        Player player = new Player("test", TowerColor.BLACK, Wizard.BLUE);

        Assistant assistant = player.getAssistantFromValue(1);
        player.setCurrentAssistant(assistant);
        assertEquals(player.getCurrentAssistant(), assistant);

        assertNull(player.getAssistantFromValue(1));

        player.setCurrentAssistant(player.getAssistantFromValue(2));
        assertEquals(player.getDiscardPile(), assistant);
    }

    @Test
    public void coinsTest() {
        Player player = new Player("test", TowerColor.BLACK, Wizard.BLUE);

        assertEquals(player.getCoins(), 0);

        player.addCoin();
        assertEquals(player.getCoins(), 1);

        player.removeCoins(1);
        assertEquals(player.getCoins(), 0);

        Exception e = assertThrows(IllegalArgumentException.class, () -> player.removeCoins(1));
        assertEquals(e.getMessage(), "Player " + player.getName() + "does not have enough coins");
    }
}
