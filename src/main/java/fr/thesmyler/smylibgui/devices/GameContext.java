package fr.thesmyler.smylibgui.devices;

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

}
