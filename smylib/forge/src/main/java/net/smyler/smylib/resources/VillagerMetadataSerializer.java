package net.smyler.smylib.resources;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.resources.data.BaseMetadataSectionSerializer;
import net.minecraft.util.JsonUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

import static net.smyler.smylib.Objects.requireNonNullElse;


public class VillagerMetadataSerializer extends BaseMetadataSectionSerializer<VillagerMetadataSection> {

    @Override
    public @NotNull String getSectionName() {
        return "villager";
    }

    @Override
    public VillagerMetadataSection deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject section = JsonUtils.getJsonObject(jsonElement, "metadata section");
        String value = JsonUtils.getString(section, "hat", "none");
        VillagerMetadata.HatType hat = requireNonNullElse(
                VillagerMetadata.HatType.fromValue(value),
                VillagerMetadata.HatType.NONE
        );
        return new VillagerMetadataSection(hat);
    }

}
