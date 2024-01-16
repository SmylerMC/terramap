package net.smyler.smylib.gui;

import net.smyler.smylib.gui.DummyFont;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DummyFontTest {

    @Test
    public void test() {
        DummyFont font = new DummyFont(1);
        String input = "Lorem ipsum dolor sit amet, consectetur adipiscing elit," +
                        " sed do\neiusmod tempor incididunt ut labore et dolore magna aliqua." +
                        "\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        String expected = "Lorem ipsum dolor\nsit amet,\nconsectetur\nadipiscing elit," +
                        " sed\ndo\neiusmod tempor\nincididunt ut labore\net dolore magna\naliqua." +
                        "\nAAAAAAAAAAAAAAAAAAAA\nAAAAAAAAAAAAAAAAA";
        assertEquals(expected, font.wrapFormattedStringToWidth(input, 20*9f));
    }

}
