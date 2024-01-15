package net.smyler.smylib;

/**
 * Precondition check utility methods.
 *
 * @author Smyler
 */
public final class Preconditions {

    /**
     * Checks whether an argument is valid.
     *
     * @param validity  a boolean indicating whether the argument is valid
     * @param message   the exception message in case the argument is valid
     *
     * @throws IllegalArgumentException if <code>validity</code> is <code>false</code>
     */
    public static void checkArgument(boolean validity, String message) {
        if (!validity) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Checks whether a state is valid.
     *
     * @param validity  a boolean indicating whether the state is valid
     * @param message   the exception message in case the argument is valid
     *
     * @throws IllegalStateException if <code>validity</code> is <code>false</code>
     */
    public static void checkState(boolean validity, String message) {
        if (!validity) {
            throw new IllegalStateException(message);
        }
    }

    private Preconditions() {
        throw new IllegalStateException();
    }

}
