package net.smyler.smylib.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.smyler.smylib.text.PlainTextContent;
import net.smyler.smylib.text.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextJsonAdapterTest {

    private Gson gson;

    @BeforeEach
    void setupGson() {
        TextJsonAdapter adapter = new TextJsonAdapter();
        this.gson = new GsonBuilder().registerTypeAdapter(Text.class, adapter).create();
    }

    @Test
    void canParseTextFromString() {
        Text text = this.gson.fromJson("\"Hello world\"", Text.class);
        assertEquals(0, text.siblings().size());
        assertEquals(new PlainTextContent("Hello world"), text.content());
    }

    @Test
    void canParseTextFromArray() {
        Text text = this.gson.fromJson("[\"Hello world\"]", Text.class);
        assertEquals(0, text.siblings().size());
        assertEquals(new PlainTextContent("Hello world"), text.content());

        text = this.gson.fromJson("[\"Hello\", \" \", \"world\"]", Text.class);
        assertEquals(2, text.siblings().size());
        assertEquals(new PlainTextContent("Hello"), text.content());
        assertEquals(new PlainTextContent(" "), text.siblings().get(0).content());
        assertEquals(new PlainTextContent("world"), text.siblings().get(1).content());

        assertThrows(JsonParseException.class, () -> this.gson.fromJson("[]", Text.class));
    }

}