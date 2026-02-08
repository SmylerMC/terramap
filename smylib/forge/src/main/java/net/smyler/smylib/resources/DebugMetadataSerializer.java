package net.smyler.smylib.resources;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.client.resources.data.BaseMetadataSectionSerializer;
import net.minecraft.util.JsonUtils;
import org.jetbrains.annotations.NotNull;

public class DebugMetadataSerializer extends BaseMetadataSectionSerializer<DebugMetadataSection> {

    @Override
    public @NotNull String getSectionName() {
        return "debug";
    }

    @Override
    public DebugMetadataSection deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject section = JsonUtils.getJsonObject(jsonElement, "metadata section");
        String value = JsonUtils.getString(section, "value");
        return new DebugMetadataSection(value);
    }

}
