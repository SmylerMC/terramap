package net.smyler.smylib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IdentifierTest {

    @Test
    @SuppressWarnings("DataFlowIssue")
    void canConstructIdentifier() {
        Identifier identifier = new Identifier("namespace_for-resources.01", "path/to_resources-.01");
        assertEquals("namespace_for-resources.01", identifier.namespace);
        assertEquals("path/to_resources-.01", identifier.path);
        assertThrows(IllegalArgumentException.class, () -> new Identifier(null, "path"));
        assertThrows(IllegalArgumentException.class, () -> new Identifier("namespace", null));
        assertThrows(IllegalArgumentException.class, () -> new Identifier("", "path"));
        assertThrows(IllegalArgumentException.class, () -> new Identifier("namespace", ""));
        assertThrows(IllegalArgumentException.class, () -> new Identifier(" namespace", "path"));
        assertThrows(IllegalArgumentException.class, () -> new Identifier("namespace", " path"));
    }

    @Test
    void canParseIdentifier() {
        Identifier identifier = Identifier.parse("namespace:path");
        assertEquals("namespace", identifier.namespace);
        assertEquals("path", identifier.path);
        assertThrows(IllegalArgumentException.class, () -> Identifier.parse("invalid"));
        assertThrows(IllegalArgumentException.class, () -> Identifier.parse("invalid:number:of:colon"));
    }

}
