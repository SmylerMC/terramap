package net.smyler.smylib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PreconditionsTest {

    @Test
    void checkArgumentThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> Preconditions.checkArgument(false, ""));
        assertDoesNotThrow(() -> Preconditions.checkArgument(true, ""));
    }

    @Test
    void checkArgumentHasCorrectErrorMessage() {
        final String errorMessage = "errorMessage123";
        try {
            Preconditions.checkArgument(false, errorMessage);
        } catch (IllegalArgumentException e) {
            assertEquals(errorMessage, e.getMessage());
        }
    }

    @Test
    void checkStateThrowsException() {
        assertThrows(IllegalStateException.class, () -> Preconditions.checkState(false, ""));
        assertDoesNotThrow(() -> Preconditions.checkState(true, ""));
    }

    @Test
    void checkStateHasCorrectErrorMessage() {
        final String errorMessage = "errorMessage123";
        try {
            Preconditions.checkState(false, errorMessage);
        } catch (IllegalStateException e) {
            assertEquals(errorMessage, e.getMessage());
        }
    }

}