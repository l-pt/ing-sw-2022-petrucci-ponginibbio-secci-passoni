package it.polimi.ingsw.server.protocol;

import com.google.gson.*;
import it.polimi.ingsw.model.IllegalMoveException;
import it.polimi.ingsw.model.character.Character;

import java.lang.reflect.Type;

/**
 * CharacterTypeAdapter Class
 */
public class CharacterTypeAdapter implements JsonDeserializer<Character>, JsonSerializer<Character> {

    /**
     * Deserializes data that is received in JSON format and casts into our Character.java object.
     * @param jsonElement input data
     * @param type casting type
     * @param jsonDeserializationContext Used for deserialization
     * @return Character
     * @throws JsonParseException when input data is not formatted properly as JSON
     */
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

    /**
     * Serializes data from Character.java object and casts it into the outgoing JSON format.
     * @param character Character.java
     * @param type casting type
     * @param jsonSerializationContext Used for serialization
     * @return JsonElement, which is a JSON encoded version of the Character
     */
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
