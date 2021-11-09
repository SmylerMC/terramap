package fr.thesmyler.smylibgui.widgets.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MockFontTest {

    @Test
    public void test() {
        MockFont font = new MockFont(1);
        String input = "Lorem ipsum dolor sit amet, consectetur adipiscing elit," +
                        " sed do\neiusmod tempor incididunt ut labore et dolore magna aliqua." +
                        "\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        String expected = "Lorem ipsum dolor\nsit amet,\nconsectetur\nadipiscing elit," +
                        " sed\ndo\neiusmod tempor\nincididunt ut labore\net dolore magna\naliqua." +
                        "\nAAAAAAAAAAAAAAAAAAAA\nAAAAAAAAAAAAAAAAA";
        assertEquals(expected, font.wrapFormattedStringToWidth(input, 20*9f));
    }

}
