package fr.thesmyler.terramap.util.json;

import com.google.gson.*;
import net.buildtheearth.terraplusplus.generator.EarthGeneratorSettings;

import java.lang.reflect.Type;

public class EarthGeneratorSettingsAdapter implements JsonSerializer<EarthGeneratorSettings>, JsonDeserializer<EarthGeneratorSettings> {

    private static final Gson GSON = new GsonBuilder().create();

    @Override
    public EarthGeneratorSettings deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String jsonString = json.toString(); // We go back to JSON
        return EarthGeneratorSettings.parse(jsonString); // And we parse again with Jackson! Ain't that ugly ?
    }

    @Override
    public JsonElement serialize(EarthGeneratorSettings src, Type typeOfSrc, JsonSerializationContext context) {
        String jsonString = src.toString(); // We get it as JSON using Jackson
        return GSON.fromJson(jsonString, JsonElement.class); // And we deserialize again with Gson
    }

}
