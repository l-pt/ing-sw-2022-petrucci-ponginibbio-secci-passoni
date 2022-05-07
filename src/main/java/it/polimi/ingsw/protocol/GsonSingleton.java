package it.polimi.ingsw.protocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.model.character.Character;

public final class GsonSingleton {
    private static Gson gson = null;

    private GsonSingleton() {
    }

    public static Gson get() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(Message.class, new MessageTypeAdapter())
                    .registerTypeAdapter(Character.class, new CharacterTypeAdapter())
                    .create();
        }
        return gson;
    }
}
