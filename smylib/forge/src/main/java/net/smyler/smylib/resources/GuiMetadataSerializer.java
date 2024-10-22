package net.smyler.smylib.resources;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.resources.data.BaseMetadataSectionSerializer;
import net.minecraft.util.JsonUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class GuiMetadataSerializer extends BaseMetadataSectionSerializer<GuiMetadataSection> {

    @Override
    public @NotNull String getSectionName() {
        return "gui";
    }

    @Override
    public GuiMetadataSection deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject section = JsonUtils.getJsonObject(jsonElement, "section");
        JsonObject scalingObject = JsonUtils.getJsonObject(section, "scaling");
        String scalingType = JsonUtils.getString(scalingObject, "type");
        GuiMetadata.Scaling scaling;
        int width;
        int height;
        switch (scalingType) {
            case "stretch":
                scaling = new GuiMetadata.Stretch();
                break;
            case "tile":
                width = JsonUtils.getInt(scalingObject, "width");
                height = JsonUtils.getInt(scalingObject, "height");
                scaling = new GuiMetadata.Tile(width, height);
                break;
            case "nine_slice":
                width = JsonUtils.getInt(scalingObject, "width");
                height = JsonUtils.getInt(scalingObject, "height");
                JsonElement border = scalingObject.get("border");
                int borderLeft, borderRight, borderTop, borderBottom;
                if (border.isJsonPrimitive() && border.getAsJsonPrimitive().isNumber()) {
                    borderLeft = borderRight = borderTop = borderBottom = JsonUtils.getInt(border, "border");
                } else if (border.isJsonObject()) {
                    JsonObject borderObject = border.getAsJsonObject();
                    borderLeft = JsonUtils.getInt(borderObject, "left");
                    borderRight = JsonUtils.getInt(borderObject, "right");
                    borderTop = JsonUtils.getInt(borderObject, "top");
                    borderBottom = JsonUtils.getInt(borderObject, "bottom");
                } else {
                    throw new JsonParseException("Invalid border format");
                }
                scaling = new GuiMetadata.NineSlice(width, height, borderLeft, borderRight, borderTop, borderBottom);
                break;
            default:
                throw new JsonParseException("Invalid gui scaling value, expecting 'stretch', 'tile' or 'nine_slice'");
        }
        return new GuiMetadataSection(scaling);
    }

}
