package net.smyler.smylib.text;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static java.util.concurrent.TimeUnit.SECONDS;
import static net.smyler.smylib.Color.BLUE;
import static net.smyler.smylib.Color.RED;
import static net.smyler.smylib.Preconditions.checkState;
import static net.smyler.smylib.text.BooleanTextStyle.INHERIT;
import static net.smyler.smylib.text.Formatting.*;
import static net.smyler.smylib.text.TextStyle.INHERIT_COLOR;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Timeout(value = 1, unit = SECONDS)
class ImmutableTextTest {

    @Test
    void constructorPropagatesStyleToSiblings() {
        ImmutableText simple = new ImmutableText(
                new PlainTextContent("Simple"),
                new TextStyle(INHERIT_COLOR, INHERIT, INHERIT, INHERIT, INHERIT, INHERIT)
        );
        this.assertTextStyles(simple, new TextStyle(INHERIT_COLOR));
        ImmutableText red = new ImmutableText(
                new PlainTextContent("Red"),
                new TextStyle(RED, INHERIT, INHERIT, INHERIT, INHERIT, INHERIT),
                simple
        );
        this.assertTextStyles(red, new TextStyle(RED), new TextStyle(RED));
        ImmutableText resetUnderline = new ImmutableText(
                new PlainTextContent("Reset and underline"),
                new TextStyle(null, RESET, UNDERLINE),
                red
        );
        this.assertTextStyles(resetUnderline, new TextStyle(null, RESET, UNDERLINE), new TextStyle(RED, RESET, UNDERLINE), new TextStyle(RED, RESET, UNDERLINE));
        ImmutableText blueItalic = new ImmutableText(
                new PlainTextContent("Blue and italic"),
                new TextStyle(BLUE, ITALIC),
                resetUnderline
        );
        this.assertTextStyles(blueItalic, new TextStyle(BLUE, ITALIC), new TextStyle(null, RESET, UNDERLINE), new TextStyle(RED, RESET, UNDERLINE), new TextStyle(RED, RESET, UNDERLINE));
    }

    @Test
    void withStylePropagatesStyleToSiblings() {
        ImmutableText simple = new ImmutableText(
                new PlainTextContent("Simple"),
                new TextStyle(INHERIT_COLOR)
        );
        this.assertTextStyles(simple, new TextStyle(INHERIT_COLOR));
        ImmutableText red = new ImmutableText(
                new PlainTextContent("Red"),
                new TextStyle(INHERIT_COLOR),
                simple
        );
        red = red.withStyle(new TextStyle(RED));
        this.assertTextStyles(red, new TextStyle(RED), new TextStyle(RED));
        ImmutableText resetUnderline = new ImmutableText(
                new PlainTextContent("Reset and underline"),
                new TextStyle(INHERIT_COLOR),
                red
        );
        resetUnderline = resetUnderline.withStyle(new TextStyle(null, RESET, UNDERLINE));
        this.assertTextStyles(resetUnderline, new TextStyle(null, RESET, UNDERLINE), new TextStyle(RED, RESET, UNDERLINE), new TextStyle(RED, RESET, UNDERLINE));
        ImmutableText blueItalic = new ImmutableText(
                new PlainTextContent("Blue and italic"),
                new TextStyle(INHERIT_COLOR),
                resetUnderline
        );
        blueItalic = blueItalic.withStyle(new TextStyle(BLUE, ITALIC));
        this.assertTextStyles(blueItalic, new TextStyle(BLUE, ITALIC), new TextStyle(null, RESET, UNDERLINE), new TextStyle(RED, RESET, UNDERLINE), new TextStyle(RED, RESET, UNDERLINE));
    }

    @Test
    void withNewSiblingsPropagatesStyleToSiblings() {
        ImmutableText simple = new ImmutableText(
                new PlainTextContent("Simple"),
                new TextStyle(INHERIT_COLOR, INHERIT, INHERIT, INHERIT, INHERIT, INHERIT)
        );
        ImmutableText red = new ImmutableText(
                new PlainTextContent("Red"),
                new TextStyle(RED, INHERIT, INHERIT, INHERIT, INHERIT, INHERIT)
        );
        ImmutableText resetUnderline = new ImmutableText(
                new PlainTextContent("Reset and underline"),
                new TextStyle(null, RESET, UNDERLINE)
        );
        ImmutableText blueItalic = new ImmutableText(
                new PlainTextContent("Blue and italic"),
                new TextStyle(BLUE, ITALIC)
        );
        this.assertTextStyles(simple, new TextStyle(INHERIT_COLOR));
        red = red.withNewSiblings(simple);
        this.assertTextStyles(red, new TextStyle(RED), new TextStyle(RED));
        resetUnderline = resetUnderline.withNewSiblings(red);
        this.assertTextStyles(resetUnderline, new TextStyle(null, RESET, UNDERLINE), new TextStyle(RED, RESET, UNDERLINE), new TextStyle(RED, RESET, UNDERLINE));
        blueItalic = blueItalic.withNewSiblings(resetUnderline);
        this.assertTextStyles(blueItalic, new TextStyle(BLUE, ITALIC), new TextStyle(null, RESET, UNDERLINE), new TextStyle(RED, RESET, UNDERLINE), new TextStyle(RED, RESET, UNDERLINE));
    }

    private void assertTextStyles(Text text, TextStyle... styles) {
        int i = 0;
        for (Text subText: text) {
            assertEquals(styles[i++], subText.style());
        }
        checkState(i == styles.length, "Invalid number of styles supplied");
    }

}
