package net.smyler.smylib.gui;

import net.smyler.smylib.text.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static java.util.Arrays.stream;
import static net.smyler.smylib.text.ImmutableText.ofPlainText;
import static org.junit.jupiter.api.Assertions.*;

@Timeout(1)
class BaseFontTest {

    private DummyFont font;

    @BeforeEach
    void setupFont() {
        this.font = new DummyFont(1f);
    }

    @Test
    void canSplitTextProperly() {
        this.assertSimpleSplit("", 9f, "");
        this.assertSimpleSplit("Two words", DummyFont.CHAR_WIDTH * 5,
                "Two", "words"
        );
        this.assertSimpleSplit("Two words", DummyFont.CHAR_WIDTH * 4,
                "Two", "word", "s"
        );
        this.assertSimpleSplit("Two two", DummyFont.CHAR_WIDTH * 3,
                "Two", "two"
        );
        this.assertSimpleSplit("Two words", DummyFont.CHAR_WIDTH * 50,
                "Two words"
        );
        this.assertSimpleSplit("Two\nwords", DummyFont.CHAR_WIDTH * 50,
                "Two", "words"
        );
    }

    private void assertSimpleSplit(String content, float maxWidth, String... parts) {
        Text text = ofPlainText(content);
        Text[] split = this.font.wrapToWidth(text, maxWidth);
        assertArrayEquals(parts, stream(split).map(Text::getUnformattedText).toArray(String[]::new));
    }

}