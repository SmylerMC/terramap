package fr.thesmyler.smylibgui.devices;

/**
 * A wrapper around whatever keyboard is being used internally.
 *
 * @author SmylerMC
 */
public interface Keyboard {

    /**
     * Indicates whether a key is currently pressed on the keyboard
     *
     * @param key   the key to check
     * @return whether a given key is pressed
     */
    boolean isKeyPressed(Key key);

    /**
     * Sets the behavior to follow when a key is hold pressed.
     * If true, events are repeated.
     * If false, only one event is fired.
     *
     * @param repeat    whether to repeat key pressed events when a key is hold down
     */
    void setRepeatEvents(boolean repeat);

    /**
     * Indicates  the behavior to follow when a key is hold pressed.
     * If true, events are repeated.
     * If false, only one event is fired.
     *
     * @return whether key pressed events are repeated when a key is hold down
     */
    boolean isRepeatingEvents();

}
