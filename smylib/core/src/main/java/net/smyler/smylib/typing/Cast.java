package net.smyler.smylib.typing;

/**
 * Casting utility functions.
 *
 * @author Smyler
 */
public final class Cast {

    /**
     * Casts a given value with no check and without emitting a warning.
     * <br>
     * Useful when you can prove the cast is safe but the compiler cannot.
     *
     * @param value the value to cast
     * @return the cast value
     * @param <T> the type to cast the value to
     */
    @SuppressWarnings("unchecked")
    public static <T> T uncheckedCast(Object value) {
        return (T) value;
    }

    private Cast() {
        throw new IllegalStateException("Utility class");
    }

}
