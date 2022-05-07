package it.polimi.ingsw.protocol;

import com.google.gson.*;
import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.model.character.impl.*;

import java.lang.reflect.Type;

public class CharacterTypeAdapter implements JsonDeserializer<Character> {
    @Override
    public Character deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        int characterId;
        try {
            characterId = obj.get("id").getAsInt();
        } catch (IllegalArgumentException e) {
            throw new JsonParseException(e);
        }
        Class<? extends Character> characterClass;
        switch (characterId) {
            case 0 -> characterClass = Character1.class;
            case 1 -> characterClass = Character2.class;
            case 2 -> characterClass = Character3.class;
            case 3 -> characterClass = Character4.class;
            case 4 -> characterClass = Character5.class;
            case 5 -> characterClass = Character6.class;
            case 6 -> characterClass = Character7.class;
            case 7 -> characterClass = Character8.class;
            case 8 -> characterClass = Character9.class;
            case 9 -> characterClass = Character10.class;
            case 10 -> characterClass = Character11.class;
            case 11 -> characterClass = Character12.class;
            default -> throw new JsonParseException("Invalid character id");
        }
        return jsonDeserializationContext.deserialize(jsonElement, characterClass);
    }
}
