package it.polimi.ingsw.server.protocol;

import com.google.gson.*;
import it.polimi.ingsw.model.IllegalMoveException;
import it.polimi.ingsw.model.character.Character;

import java.lang.reflect.Type;

public class CharacterTypeAdapter implements JsonDeserializer<Character>, JsonSerializer<Character> {
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
        try {
            characterClass = Character.getClassFromId(characterId);
        } catch (IllegalMoveException e) {
            throw new JsonParseException("Invalid character id");
        }
        return jsonDeserializationContext.deserialize(jsonElement, characterClass);
    }

    @Override
    public JsonElement serialize(Character character, Type type, JsonSerializationContext jsonSerializationContext) {
        Class<? extends Character> characterClass;
        try {
            characterClass = Character.getClassFromId(character.getId());
        } catch (IllegalMoveException e) {
            throw new JsonParseException("Invalid character id");
        }
        return jsonSerializationContext.serialize(character, characterClass);
    }
}
