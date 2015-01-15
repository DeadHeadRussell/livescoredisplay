package edu.cmu.mat.scores.events;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class EventTypeAdapter implements JsonSerializer<Event>,
		JsonDeserializer<Event> {
	private static final String EVENT_PACKAGE = "edu.cmu.mat.scores.events.";

	public Event deserialize(JsonElement json, Type json_type,
			JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = json.getAsJsonObject();
		String type = object.get("type").getAsString();
		JsonElement element = object.get("properties");

		try {
			return context.deserialize(element,
					Class.forName(EVENT_PACKAGE + type));
		} catch (ClassNotFoundException e) {
			throw new JsonParseException("Unknown element type: " + type, e);
		}
	}

	public JsonElement serialize(Event src, Type src_type,
			JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
		result.add("properties", context.serialize(src, src.getClass()));
		return result;
	}
}
