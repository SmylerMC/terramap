package net.smyler.smylib.json;

import com.google.gson.*;
import net.smyler.smylib.Color;
import net.smyler.smylib.SmyLib;
import net.smyler.smylib.text.*;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

import static java.util.Objects.requireNonNull;
import static net.smyler.smylib.text.BooleanTextStyle.*;
import static net.smyler.smylib.text.ImmutableText.of;
import static net.smyler.smylib.text.TextStyle.INHERIT_COLOR;

public class TextJsonAdapter implements JsonSerializer<Text>, JsonDeserializer<Text> {

    @Override
    public JsonElement serialize(Text text, Type type, JsonSerializationContext context) {
        JsonObject rootObject = new JsonObject();
        this.writeStyleToObject(rootObject, text.style());
        this.writeContentToObject(rootObject, text.content());

        JsonElement root = rootObject;

        if (rootObject.size() == 1 && rootObject.has("text")) {
            root = rootObject.get("text");
        }

        if (!text.siblings().isEmpty()) {
            JsonArray array = new JsonArray();
            array.add(root);
            for (Text sibling: text.siblings()) {
                array.add(this.serialize(sibling, type, context));
            }
            root = array;
        }
        return root;
    }

    private void writeStyleToObject(JsonObject object, TextStyle style) {
        if (style.color() != INHERIT_COLOR) {
            object.add("color", new JsonPrimitive(style.color().asHtmlHexString()));
        }
        this.writeBooleanStyleToObject(object, "bold", style.isBold());
        this.writeBooleanStyleToObject(object, "italic", style.isItalic());
        this.writeBooleanStyleToObject(object, "underline", style.isUnderlined());
        this.writeBooleanStyleToObject(object, "strikethrough", style.isStrikethrough());
        this.writeBooleanStyleToObject(object, "obfuscated", style.isObfuscated());
    }

    private void writeContentToObject(JsonObject object, TextContent content) {
        if (content instanceof PlainTextContent) {
            object.add("text", new JsonPrimitive(content.toString()));
        }
        SmyLib.getLogger().warn("SmyLib is serializing text to json and encountered an unsupported content: {}", content);
    }

    private void writeBooleanStyleToObject(JsonObject object, String name, BooleanTextStyle value) {
        if (value == INHERIT) {
            return;
        }
        object.add(name, new JsonPrimitive(value == TRUE));
    }

    @Override
    public Text deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonNull()) {
            return null;
        }

        // String -> plain text content with no style
        if (json.isJsonPrimitive()) {
            JsonPrimitive primitive = json.getAsJsonPrimitive();
            if (primitive.isString()) {
                String content = primitive.getAsString();
                return ImmutableText.ofPlainText(content);
            }
        }

        // Array -> First element is parent, following elements are siblings
        if (json.isJsonArray()) {
            return this.parseTextFromJsonArray(json, type, context);
        }

        // Object -> complex text object
        if (json.isJsonObject()) {
            return this.parseTextFromJsonObject(json, type, context);
        }

        throw new JsonParseException("Unexpected json element when parsing json text: " + json);
    }

    private Text parseTextFromJsonArray(JsonElement json, Type type, JsonDeserializationContext context) {
        JsonArray array = json.getAsJsonArray();
        if (array.isEmpty()) {
            throw new JsonParseException("Json texts represented as arrays need at least one element to be valid");
        }
        ImmutableText parent = null;
        ImmutableText[] siblings = new ImmutableText[array.size() - 1];
        int i = 0;
        for (JsonElement element: array) {
            ImmutableText text = of(this.deserialize(element, type, context));
            if (parent == null) {
                parent = text;
            } else {
                siblings[i++] = text;
            }
        }
        return requireNonNull(parent).withNewSiblings(siblings);
    }

    private Text parseTextFromJsonObject(JsonElement json, Type type, JsonDeserializationContext context) {
        JsonObject object = json.getAsJsonObject();
        TextStyle style = this.parseStyleFromObject(object);
        TextContent content = this.parseContentFromObject(object);
        ImmutableText[] siblings = this.parseSiblingsFromObject(object, type, context);
        return new ImmutableText(
                content,
                style,
                siblings
        );
    }

    private TextStyle parseStyleFromObject(JsonObject object) {
        if (object.has("font")) {
            SmyLib.getLogger().warn(
                    "SmyLib is trying to deserialize a text from json with a specific font. Fonts are not yet supported and will be ignored."
            );
        }
        return new TextStyle(
                this.getColorFromObject(object),
                this.getBooleanStyleFromObject(object, "bold"),
                this.getBooleanStyleFromObject(object, "italic"),
                this.getBooleanStyleFromObject(object, "strikethrough"),
                this.getBooleanStyleFromObject(object, "underline"),
                this.getBooleanStyleFromObject(object, "obfuscated")
        );
    }

    private Color getColorFromObject(JsonObject object) {
        String color = getAsStringOrNull(object.get("color"));
        if (color == null) {
            return INHERIT_COLOR;
        }
        if (color.startsWith("#") && Color.isValidHexColorCode(color)) {
            return Color.fromHtmlHexString(color);
        }
        for (Formatting formatting: Formatting.COLORS) {
            if (formatting.name().equalsIgnoreCase(color)) {
                return formatting.color;
            }
        }
        return INHERIT_COLOR;
    }

    private BooleanTextStyle getBooleanStyleFromObject(JsonObject object, String style) {
        JsonElement element = object.get(style);
        if (element == null || !element.isJsonPrimitive()) {
            return INHERIT;
        }
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        if (!primitive.isBoolean()) {
            return INHERIT;
        }
        return primitive.getAsBoolean() ? TRUE: FALSE;
    }

    private TextContent parseContentFromObject(JsonObject object) {
        TextContent content = this.parseContentFromExplicitType(object);
        if (content == null) {
            content = this.parseContentImplicit(object);
        }
        if (content == null) {
            throw new JsonParseException("Failed to parse json object as Text, content is not valid: " + object);
        }
        return content;
    }

    @Nullable
    private TextContent parseContentFromExplicitType(JsonObject object) {
        JsonElement contentTypeElement = object.get("type");
        if (contentTypeElement == null || !contentTypeElement.isJsonPrimitive()) {
            return null;
        }
        JsonPrimitive primitive = contentTypeElement.getAsJsonPrimitive();
        if (!primitive.isString()) {
            return null;
        }
        String contentType = primitive.getAsString();

        if ("text".equals(contentType) && object.has("text")) {
            return this.parsePlainTextContent(object);
        }
        if ("translatable".equals(contentType) && object.has("translate")) {
            return this.parseTranslatableContent(object);
        }
        if ("score".equals(contentType) && object.has("score")) {
            return this.parseScoreContent(object);
        }
        if ("selector".equals(contentType) && object.has("selector")) {
            return this.parseSelectorContent(object);
        }
        if ("keybind".equals(contentType) && object.has("keybind")) {
            return this.parseKeyBindingContent(object);
        }
        if ("nbt".equals(contentType) && object.has("nbt") &&
                (object.has("block") || object.has("entity") || object.has("storage"))
        ) {
            return this.parseNbtContent(object);
        }

        return null;
    }

    private TextContent parseContentImplicit(JsonObject object) {
        TextContent content = this.parsePlainTextContent(object);
        if (content != null) {
            return content;
        }
        content = this.parseTranslatableContent(object);
        if (content != null) {
            return content;
        }
        content = this.parseScoreContent(object);
        if (content != null) {
            return content;
        }
        content = this.parseSelectorContent(object);
        if (content != null) {
            return content;
        }
        content = this.parseKeyBindingContent(object);
        if (content != null) {
            return content;
        }
        content = this.parseNbtContent(object);
        return content;
    }

    @Nullable
    private TextContent parsePlainTextContent(JsonObject object) {
        String text = getAsStringOrNull(object.get("text"));
        if (text == null) {
            return null;
        }
        return new PlainTextContent(text);
    }

    @Nullable
    private TextContent parseTranslatableContent(JsonObject object) {
        String translate = getAsStringOrNull(object.get("translate"));
        if (translate == null) {
            return null;
        }
        SmyLib.getLogger().warn(
                "SmyLib is trying to deserialize a translatable text from JSON. " +
                "This is not yet supported. Will use the translation key as plain text."
        );
        //TODO support parsing translatable text contents
        return new PlainTextContent(translate);
    }

    @Nullable
    private TextContent parseScoreContent(JsonObject object) {
        JsonElement scoreElement = object.get("score");
        if (scoreElement == null || !scoreElement.isJsonObject()) {
            return null;
        }
        JsonObject scoreObject = scoreElement.getAsJsonObject();
        String name = getAsStringOrNull(scoreObject.get("name"));
        String objective = getAsStringOrNull(scoreObject.get("objective"));
        if (name == null || objective == null) {
            return null;
        }
        SmyLib.getLogger().warn(
                "SmyLib is trying to deserialize a score text from JSON. " +
                        "This is not yet supported. Will use the objective name as plain text."
        );
        //TODO support parsing score text contents
        return new PlainTextContent(objective);
    }

    @Nullable
    private TextContent parseSelectorContent(JsonObject object) {
        String selector = getAsStringOrNull(object.get("selector"));
        if (selector == null) {
            return null;
        }
        SmyLib.getLogger().warn(
                "SmyLib is trying to deserialize a selector text from JSON. " +
                        "This is not yet supported. Will use the selector as plain text."
        );
        //TODO support parsing selector text contents
        return new PlainTextContent(selector);
    }

    @Nullable
    private TextContent parseKeyBindingContent(JsonObject object) {
        String keybind = getAsStringOrNull(object.get("keybind"));
        if (keybind == null) {
            return null;
        }
        SmyLib.getLogger().warn(
                "SmyLib is trying to deserialize a key binding text from JSON. " +
                        "This is not yet supported. Will use the binding name as plain text."
        );
        //TODO support parsing key bindings text contents
        return new PlainTextContent(keybind);
    }

    @Nullable
    private TextContent parseNbtContent(JsonObject object) {
        String source = getAsStringOrNull(object.get("source"));
        String block = getAsStringOrNull(object.get("block"));
        String entity = getAsStringOrNull(object.get("entity"));
        String storage = getAsStringOrNull(object.get("storage"));
        String nbt = getAsStringOrNull(object.get("nbt"));
        if (nbt == null) {
            return null;
        }

        String displayedSource = null;

        if ("block".equals(source) && block != null) {
            displayedSource = "Block[" + block + "]";
        } else if ("entity".equals(source) && entity != null) {
            displayedSource = entity;
        } else if ("storage".equals(source) && storage != null) {
            displayedSource = storage;
        } else if (block != null) {
            displayedSource = "Block[" + block + "]";
        } else if (entity != null) {
            displayedSource = entity;
        } else if (storage != null) {
            displayedSource = storage;
        }

        if (displayedSource == null) {
            return null;
        }

        SmyLib.getLogger().warn(
                "SmyLib is trying to deserialize a nbt text from JSON. " +
                        "This is not yet supported. Will use the nbt source as plain text."
        );
        //TODO support parsing nbt text contents
        return new PlainTextContent(displayedSource + "." + nbt);
    }

    private static final ImmutableText[] EMPTY_EXTRA = new ImmutableText[0];
    private ImmutableText[] parseSiblingsFromObject(JsonObject object, Type type, JsonDeserializationContext context) {
        JsonElement extraElement = object.get("extra");
        if (extraElement == null) {
            return EMPTY_EXTRA;
        }
        if (!extraElement.isJsonArray()) {
            throw new JsonParseException("Json text 'extra' must be an array");
        }
        JsonArray array = extraElement.getAsJsonArray();
        ImmutableText[] siblings = new ImmutableText[array.size()];
        int i = 0;
        for (JsonElement element: array) {
            ImmutableText text = of(this.deserialize(element, type, context));
            siblings[i++] = text;
        }
        return siblings;
    }

    @Nullable
    private static String getAsStringOrNull(JsonElement element) {
        if (element == null) {
            return null;
        }
        if (element.isJsonNull()) {
            element = new JsonPrimitive("null");
        }
        if (!element.isJsonPrimitive()) {
            return null;
        }
        return element.getAsString();
    }

}
