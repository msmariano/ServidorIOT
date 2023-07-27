package br.com.neuverse.entity;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PropertyMarshallerAbstractTask implements JsonSerializer<Object>, JsonDeserializer<Object> {

    private static final String CLASS_TYPE = "CLASS_TYPE";

    @Override
    public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {

        //JsonObject jsonObj = jsonElement.getAsJsonObject();
        String className = "Dispositivo";

        try {
            Class<?> clz = Class.forName(className);
            return jsonDeserializationContext.deserialize(jsonElement, clz);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }

    @Override
    public JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {

        Gson gson = new Gson(); // without this line it will not work
        gson.toJson(object, object.getClass()); // and this one
        JsonElement jsonElement = gson.toJsonTree(object); // it needs to replace to another method...toJsonTree
        jsonElement.getAsJsonObject().addProperty(CLASS_TYPE, object.getClass().getCanonicalName());
        return jsonElement;
    }

}