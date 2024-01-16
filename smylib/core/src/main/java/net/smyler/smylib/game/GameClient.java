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
    float getWindowWidth();

    /**
     * @return the effective height of the window when scaling is taken into account
     */
    float getWindowHeight();

    /**
     * @return the width of the native window
     */
    int getNativeWindowWidth();

    /**
     * @return the height of the native window
     */
    int getNativeWindowHeight();

    /**
     * @return the scale factor applied
     */
    int getScaleFactor();

    /**
     * Gets the current language the game uses.
     *
     * @return the language identifier (e.g. "en-us")
     */
    String getLanguage();

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
    Path getGameDirectory();

    /**
     * Get information on the server which is currently being played on.
     *
     * @return the server info, or null if not currently playing on a server
     */
    @Nullable
    MinecraftServerInfo getCurrentServerInfo();

    Mouse getMouse();

    Keyboard getKeyboard();

    Clipboard getClipboard();

    SoundSystem getSoundSystem();

    Translator getTranslator();

    Font getDefaultFont();

    default Font getSmallestFont() {
        return this.getDefaultFont().withScale(1f / this.getScaleFactor());
    }

    boolean isGlAvailabale();

}
