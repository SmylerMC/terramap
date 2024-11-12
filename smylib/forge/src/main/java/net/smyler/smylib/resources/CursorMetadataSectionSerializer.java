package net.smyler.smylib.resources;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.resources.data.BaseMetadataSectionSerializer;
import net.minecraft.util.JsonUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class CursorMetadataSectionSerializer extends BaseMetadataSectionSerializer<CursorMetadataSection> {

    @Override
    public @NotNull String getSectionName() {
        return "cursor";
    }

    @Override
    public CursorMetadataSection deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject section = JsonUtils.getJsonObject(jsonElement, "metadata section");
        int hotspotX = JsonUtils.getInt(section, "hotspotX");
        int hotspotY = JsonUtils.getInt(section, "hotspotY");
        return new CursorMetadataSection(hotspotX, hotspotY);
    }

}
