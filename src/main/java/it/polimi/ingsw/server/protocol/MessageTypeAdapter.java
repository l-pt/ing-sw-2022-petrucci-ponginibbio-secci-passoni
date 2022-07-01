package it.polimi.ingsw.server.protocol;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Adapter class needed by GSON library to deserialize JSON-encoded messages
 * when the MessageId is not known in advance
 *
 * Documentation:
 * https://www.javadoc.io/static/com.google.code.gson/gson/2.9.0/com.google.gson/com/google/gson/GsonBuilder.html#registerTypeAdapter(java.lang.reflect.Type,java.lang.Object)
 * https://www.javadoc.io/static/com.google.code.gson/gson/2.9.0/com.google.gson/com/google/gson/JsonDeserializer.html
 */
public class MessageTypeAdapter implements JsonDeserializer<Message> {

    /**
     * Casts data formatted as JSON into a Message object
     * @param jsonElement input data must be in JSON format
     * @param type java type to cast our JSON data into
     * @param jsonDeserializationContext jdc: explained in javadocs link
     * @return Message.java object
     * @throws JsonParseException If there is a serious issue that occurs during parsing of a Json string
     */
    @Override
    public Message deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        MessageId messageId;
        try {
            messageId = MessageId.valueOf(obj.get("messageId").getAsString());
        } catch (IllegalArgumentException e) {
            throw new JsonParseException(e);
        }
        Class<? extends Message> messageClass = messageId.getMessageClass();
        return jsonDeserializationContext.deserialize(jsonElement, messageClass);
    }
}
