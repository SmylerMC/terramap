package net.smyler.smylib.gui.screen;

/**
 * The sole purpose of this class is to expose package-visible methods
 * of {@link Screen} for testing purposes.
 */
public final class TestScreenProxy {

    public static void setScreenResolution(float width, float height, Screen screen) {
        screen.width = width;
        screen.height = height;
        screen.init();
    }

}
