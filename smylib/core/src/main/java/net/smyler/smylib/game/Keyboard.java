package net.smyler.smylib.game;

import static net.smyler.smylib.SmyLib.getGameClient;
import static net.smyler.smylib.game.Key.*;

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

    default boolean isShiftPressed() {
        return this.isKeyPressed(KEY_RSHIFT) || this.isKeyPressed(KEY_LSHIFT);
    }

    default boolean isControlPressed() {
        if (getGameClient().isMac()) {
            return this.isKeyPressed(KEY_LMETA);
        }
        return this.isKeyPressed(KEY_RCONTROL) || this.isKeyPressed(KEY_LCONTROL);
    }

    default boolean isAltPressed() {
        return this.isKeyPressed(KEY_RMENU) || this.isKeyPressed(KEY_LMENU);
    }

}
