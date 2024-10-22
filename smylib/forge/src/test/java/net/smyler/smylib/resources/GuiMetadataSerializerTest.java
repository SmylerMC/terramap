package net.smyler.smylib.resources;

import com.google.gson.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.*;

class GuiMetadataSerializerTest {

    static final String TEST_RESOURCE = "test-gui-metadata.json";

    final Gson gson = new GsonBuilder()
            .registerTypeAdapter(GuiMetadata.class, new GuiMetadataSerializer())
            .create();

    @Test
    void canParseStretch() throws IOException {
        JsonElement json = this.getTestJson("canParseStretch");
        GuiMetadata guiMetadata = gson.fromJson(json, GuiMetadata.class);
        assertInstanceOf(GuiMetadata.Stretch.class, guiMetadata.scaling());
    }

    @Test
    void canParseTile() throws IOException {
        JsonElement json = this.getTestJson("canParseTile");
        GuiMetadata guiMetadata = gson.fromJson(json, GuiMetadata.class);
        assertInstanceOf(GuiMetadata.Tile.class, guiMetadata.scaling());
        GuiMetadata.Tile tile = (GuiMetadata.Tile) guiMetadata.scaling();
        assertEquals(1337, tile.width());
        assertEquals(42, tile.height());
    }

    @Test
    void canParseNineSliceWithNumberBorder() throws IOException {
        JsonElement json = this.getTestJson("canParseNineSliceWithNumberBorder");
        GuiMetadata guiMetadata = gson.fromJson(json, GuiMetadata.class);
        assertInstanceOf(GuiMetadata.NineSlice.class, guiMetadata.scaling());
        GuiMetadata.NineSlice slice = (GuiMetadata.NineSlice) guiMetadata.scaling();
        assertEquals(1337, slice.width());
        assertEquals(42, slice.height());
        assertEquals(22, slice.borderLeft());
        assertEquals(22, slice.borderTop());
        assertEquals(22, slice.borderRight());
        assertEquals(22, slice.borderBottom());
    }

    @Test
    void canParseNineSliceWithObjectBorder() throws IOException {
        JsonElement json = this.getTestJson("canParseNineSliceWithObjectBorder");
        GuiMetadata guiMetadata = gson.fromJson(json, GuiMetadata.class);
        assertInstanceOf(GuiMetadata.NineSlice.class, guiMetadata.scaling());
        GuiMetadata.NineSlice slice = (GuiMetadata.NineSlice) guiMetadata.scaling();
        assertEquals(1337, slice.width());
        assertEquals(42, slice.height());
        assertEquals(22, slice.borderLeft());
        assertEquals(23, slice.borderTop());
        assertEquals(24, slice.borderRight());
        assertEquals(25, slice.borderBottom());
    }

    JsonElement getTestJson(String name) throws IOException {
        Class<GuiMetadata> clazz = GuiMetadata.class;
        try(InputStream in = clazz.getResourceAsStream(TEST_RESOURCE)) {
            assertNotNull(in);
            JsonElement element = new JsonParser().parse(new InputStreamReader(in));
            assertTrue(element.isJsonObject());
            JsonObject obj = element.getAsJsonObject();
            JsonElement testElement = obj.get(name);
            assertNotNull(testElement);
            return testElement;
        }
    }

}
