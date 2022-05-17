package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.IllegalMoveException;
import it.polimi.ingsw.model.character.impl.Character1;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.InvocationTargetException;

public class CharacterTest extends TestCase {
    @Test
    public void characterTest() throws IllegalMoveException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        for (int i = 0; i < 12; ++i) {
            assertEquals(i, Character.getClassFromId(i).getDeclaredConstructor().newInstance().getId());
        }
        Exception e = assertThrows(IllegalMoveException.class, () -> Character.getClassFromId(12));
        assertEquals("Wrong character id", e.getMessage());
    }

    @Test
    public void descriptionTest() {
        Character1 character1 = new Character1();
        assertEquals("Take 1 student from this card and place it on an island of your choice. " +
                "Then, draw a new student from the bag and place it on this card.", character1.getDescription());
    }
}
