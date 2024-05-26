package net.smyler.smylib.gui.screen;

/**
 * The different options for a SmyLibGui {@link Screen} background.
 *
 * @author SmylerMC
 */
public enum BackgroundOption {

    /**
     * No background at all, will be transparent.
     */
    NONE,

    /**
     * The classic Minecraft dirt background.
     */
    DIRT,

    /**
     * The dark transparent background the game uses for in-game GUIs.
     */
    OVERLAY,

    /**
     * Let the game choose.
     * Usually, this will result in {@link #DIRT} when no world is loaded and
     * {@link #OVERLAY} the rest of the time.
     */
    DEFAULT

}
