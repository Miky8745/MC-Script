package net.euport.mcscript.gsonadapters;

import com.google.gson.*;

import java.lang.reflect.Type;

public class GsonClass implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {


    @Override
    public Class<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return Class.forName(json.getAsString());
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }

    @Override
    public com.google.gson.JsonElement serialize(Class<?> src, Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
        return context.serialize(src.getName());
    }
}
