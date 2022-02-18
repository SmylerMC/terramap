package fr.thesmyler.smylibgui;

/**
 * The different context SmyLibGui can operate in.
 */
public enum SmyLibGuiContext {

    /** Normal game operation */
    LWJGL2,

    /** Running JUnit tests, the devices are dummies */
    JUNIT,

    /** Running inside the game, but displaying the test screen on startup */
    LWJGL2_TEST_SCREEN;

}
