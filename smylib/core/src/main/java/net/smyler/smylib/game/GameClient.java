package net.smyler.smylib.game;

import net.smyler.smylib.gui.Font;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * A wrapper around the main game object.
 *
 * @author Smyler
 */
public interface GameClient {

    /**
     * @return the effective width of the window when scaling is taken into account
     */
    float windowWidth();

    /**
     * @return the effective height of the window when scaling is taken into account
     */
    float windowHeight();

    /**
     * @return the width of the native window
     */
    int nativeWindowWidth();

    /**
     * @return the height of the native window
     */
    int nativeWindowHeight();

    /**
     * @return the scale factor applied
     */
    int scaleFactor();

    /**
     * Gets the current language the game uses.
     *
     * @return the language identifier (e.g. "en-us")
     */
    String language();

    /**
     * MacOS does weird things, we may want to account for them.
     *
     * @return whether Minecraft has detected that the underlying system is macOS
     */
    boolean isMac();

    /**
     * The game directory is where all game files are saved (e.g. ~/.minecraft on unix based OS).
     *
     * @return the game directory
     */
    Path gameDirectory();

    /**
     * Get information on the server which is currently being played on.
     *
     * @return the server info, or null if not currently playing on a server
     */
    @Nullable
    MinecraftServerInfo currentServerInfo();

    Mouse mouse();

    Keyboard keyboard();

    Clipboard clipboard();

    SoundSystem soundSystem();

    Translator translator();

    Font defaultFont();

    default Font smallestFont() {
        return this.defaultFont().withScale(1f / this.scaleFactor());
    }

    boolean isGlAvailabale();

}
