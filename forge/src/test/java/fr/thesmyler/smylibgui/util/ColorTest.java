package fr.thesmyler.smylibgui.util;

import fr.thesmyler.smylibgui.SmyLibGuiTest;
import org.junit.jupiter.api.Test;

import static fr.thesmyler.smylibgui.util.Color.*;
import static org.junit.jupiter.api.Assertions.*;

class ColorTest extends SmyLibGuiTest {

    @Test
    void hasHtmlHexString() {
        assertEquals("#000000", BLACK.asHtmlHexString());
        assertEquals("#FF0000", RED.asHtmlHexString());
        assertEquals("#FFFFFF00", TRANSPARENT.asHtmlHexString());
    }

}