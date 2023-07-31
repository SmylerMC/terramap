package fr.thesmyler.smylibgui.devices;

import fr.thesmyler.smylibgui.util.MinecraftServerInfo;

import java.nio.file.Path;

/**
 * A wrapper around the game window.
 *
 * @author SmylerMC
 */
public interface GameContext {

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
    MinecraftServerInfo getCurrentServerInfo();

}
