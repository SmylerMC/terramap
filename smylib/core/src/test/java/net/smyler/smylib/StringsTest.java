package net.smyler.smylib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringsTest {

    @Test
    void isNullOrEmpty() {
        assertTrue(Strings.isNullOrEmpty(null));
        assertTrue(Strings.isNullOrEmpty(""));
        assertFalse(Strings.isNullOrEmpty(" "));
        assertFalse(Strings.isNullOrEmpty("not empty"));
    }

    @Test
    void isBlank() {
        assertTrue(Strings.isBlank(""));
        assertTrue(Strings.isBlank("   "));
        assertTrue(Strings.isBlank(" \t"));
        assertTrue(Strings.isBlank(" \t\n\r"));
        assertFalse(Strings.isBlank("    a  "));
        assertFalse(Strings.isBlank("   `"));
    }

    @Test
    void isNullOrBlank() {
        assertTrue(Strings.isNullOrBlank(null));
        assertTrue(Strings.isNullOrBlank("  "));
        assertTrue(Strings.isNullOrBlank(" \t\n\r"));
        assertFalse(Strings.isNullOrBlank(" a  \n\t\r"));
    }

    @Test
    void strip() {
        assertEquals("stripped", Strings.strip("stripped"));
        assertEquals("stripped", Strings.strip(" \n\t\rstripped"));
        assertEquals("stripped", Strings.strip("stripped \n\t\r"));
        assertEquals("stripped", Strings.strip(" \n\t\rstripped \n\t\r"));
        assertEquals("stripped multi \twords", Strings.strip(" \n\t\rstripped multi \twords \n\t\r"));
    }

}