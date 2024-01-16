package net.smyler.smylib;

import org.junit.jupiter.api.Test;

import static net.smyler.smylib.Color.*;
import static org.junit.jupiter.api.Assertions.*;

class ColorTest extends SmyLibTest {

    @Test
    void hasHtmlHexString() {
        assertEquals("#000000", BLACK.asHtmlHexString());
        assertEquals("#FF0000", RED.asHtmlHexString());
        assertEquals("#FFFFFF00", TRANSPARENT.asHtmlHexString());
    }

}