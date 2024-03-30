package net.smyler.smylib.gui;

import net.smyler.smylib.text.ImmutableText;
import net.smyler.smylib.text.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static java.util.Arrays.stream;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.smyler.smylib.gui.DummyFont.CHAR_WIDTH;
import static net.smyler.smylib.text.Formatting.*;
import static net.smyler.smylib.text.ImmutableText.EMPTY;
import static net.smyler.smylib.text.ImmutableText.ofPlainText;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Timeout.ThreadMode.SEPARATE_THREAD;

@Timeout(value = 1, unit = SECONDS, threadMode = SEPARATE_THREAD)
class BaseFontTest {

    private DummyFont font;

    @BeforeEach
    void setupFont() {
        this.font = new DummyFont(1f, 1f);
    }

    @Test
    void canWrapString() {
        assertArrayEquals(
                new String[] {""},
                this.font.wrapToWidth("", CHAR_WIDTH)
        );
        assertArrayEquals(
                new String[] {"Two", "words"},
                this.font.wrapToWidth("Two words", CHAR_WIDTH * 5f)
        );
        assertArrayEquals(
                new String[] {"Two", "two"},
                this.font.wrapToWidth("Two two", CHAR_WIDTH * 5f)
        );
        assertArrayEquals(
                new String[] {"Two", "word", "s"},
                this.font.wrapToWidth("Two words", CHAR_WIDTH * 4f)
        );
        assertArrayEquals(
                new String[] {"Two", "words"},
                this.font.wrapToWidth("Two\nwords", CHAR_WIDTH * 50f)
        );
        assertArrayEquals(
                new String[] {"Two words"},
                this.font.wrapToWidth("Two words", CHAR_WIDTH * 50f)
        );
        assertArrayEquals(
                new String[] {"T", "w", "o"},
                this.font.wrapToWidth("Two", CHAR_WIDTH * 0.5f)
        );
        assertArrayEquals(
                new String[] {"Two", RED + "words"},
                this.font.wrapToWidth("Two " + RED + "words", CHAR_WIDTH * 5f)
        );
    }

    @Test
    void canWrapText() {
        this.assertSimpleTextSplit("", 9f, "");
        this.assertSimpleTextSplit("Two words", CHAR_WIDTH * 5,
                "Two", "words"
        );
        this.assertSimpleTextSplit("Two words", CHAR_WIDTH * 4,
                "Two", "word", "s"
        );
        this.assertSimpleTextSplit("Two two", CHAR_WIDTH * 3,
                "Two", "two"
        );
        this.assertSimpleTextSplit("Two words", CHAR_WIDTH * 50,
                "Two words"
        );
        this.assertSimpleTextSplit("Two\nwords", CHAR_WIDTH * 50,
                "Two", "words"
        );
        this.assertSimpleTextSplit("aaaaaaaa", CHAR_WIDTH * 0.5f,
                "a", "a", "a", "a", "a", "a", "a", "a"
        );
    }

    @Test
    void canComputeStringWidth() {
        assertEquals(
        CHAR_WIDTH * 0f,
                this.font.computeWidth("")
        );
        assertEquals(
                CHAR_WIDTH * 9f,
                this.font.computeWidth("Two words")
        );
        assertEquals(
                CHAR_WIDTH * 9f,
                this.font.computeWidth(RED + "Two " + BLUE + "words")
        );
        assertEquals(
                CHAR_WIDTH * 9f,
                this.font.computeWidth(RED + "Two " + PREFIX + "gwords")
        );
    }

    @Test
    void canTrimStringRight() {
        assertEquals(
                "",
                this.font.trimRight("", 50f)
        );
        assertEquals(
                "Two",
                this.font.trimRight("Two words", CHAR_WIDTH * 3)
        );
        assertEquals(
                RED + "Two",
                this.font.trimRight(RED + "Two words", CHAR_WIDTH * 3)
        );
        assertEquals(
                RED + "Tw" + BLUE + "o",
                this.font.trimRight(RED + "Tw" + BLUE + "o words", CHAR_WIDTH * 3)
        );
    }

    @Test
    void canTrimTextRight() {
        // Empty content
        this.assertRightTextTrim(
                "",
                6f * CHAR_WIDTH,
                ""
        );
        // Nothing to do
        this.assertRightTextTrim(
                "One or two words",
                60f * CHAR_WIDTH,
                "One ", "or two", " words"
        );
        // Single fragment
        this.assertRightTextTrim(
                "One or",
                6f * CHAR_WIDTH,
                "One or two words"
        );
        // Multiple fragments
        this.assertRightTextTrim(
                "One or two",
                10f * CHAR_WIDTH,
                "One or ", "two", " words"
        );
        // Limit on fragment boundary
        this.assertRightTextTrim(
                "One or",
                6f * CHAR_WIDTH,
                "One or", "two words"
        );
        // Negative width
        this.assertRightTextTrim(
                "",
                -10f * CHAR_WIDTH,
                "One or two words"
        );
    }

    private void assertRightTextTrim(String expected, float width, String... parts) {
        ImmutableText text = EMPTY.withNewSiblings(
                stream(parts).map(ImmutableText::ofPlainText).toArray(ImmutableText[]::new)
        );
        String trimmedText = this.font.trimRight(text, width).getUnformattedText();
        assertEquals(expected, trimmedText);
    }

    @Test
    void canTrimStringLeft() {
        assertEquals(
                "",
                this.font.trimLeft("", 50f)
        );
        assertEquals(
                "words",
                this.font.trimLeft("Two words", CHAR_WIDTH * 5)
        );
        assertEquals(
                "w" + RED + "ords",
                this.font.trimLeft("Two w" + RED + "ords", CHAR_WIDTH * 5)
        );
        assertEquals(
                RED + "words",
                this.font.trimLeft(RED + "Two words", CHAR_WIDTH * 5)
        );
        assertEquals(
                " " + RED + "words",
                this.font.trimLeft("Two " + RED + "words", CHAR_WIDTH * 6)
        );
    }

    @Test
    void canTrimTextLeft() {
        // Empty content
        this.assertLeftTextTrim(
                "",
                6f * CHAR_WIDTH,
                ""
        );
        // Nothing to do
        this.assertLeftTextTrim(
                "One or two words",
                60f * CHAR_WIDTH,
                "One ", "or two", " words"
        );
        // Single fragment
        this.assertLeftTextTrim(
                "words",
                5f * CHAR_WIDTH,
                "One or two words"
        );
        // Multiple fragments
        this.assertLeftTextTrim(
                " two words",
                10f * CHAR_WIDTH,
                "One or ", "two", " words"
        );
        // Limit on fragment boundary
        this.assertLeftTextTrim(
                "two words",
                9f * CHAR_WIDTH,
                "One or ", "two words"
        );
        // Negative width
        this.assertLeftTextTrim(
                "",
                -10f * CHAR_WIDTH,
                "One or two words"
        );
    }

    private void assertLeftTextTrim(String expected, float width, String... parts) {
        ImmutableText text = EMPTY.withNewSiblings(
                stream(parts).map(ImmutableText::ofPlainText).toArray(ImmutableText[]::new)
        );
        String trimmedText = this.font.trimLeft(text, width).getUnformattedText();
        assertEquals(expected, trimmedText);
    }

    private void assertSimpleTextSplit(String content, float maxWidth, String... parts) {
        Text text = ofPlainText(content);
        Text[] split = this.font.wrapToWidth(text, maxWidth);
        assertArrayEquals(parts, stream(split).map(Text::getUnformattedText).toArray(String[]::new));
    }

}