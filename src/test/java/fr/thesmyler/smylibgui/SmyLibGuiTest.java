package fr.thesmyler.smylibgui;

import org.junit.jupiter.api.BeforeEach;

import static fr.thesmyler.smylibgui.SmyLibGuiContext.JUNIT;

/**
 * An abstract class that unit test classes can inherit from to be provided with a working SmyLibGui test environment.
 *
 * @author Smyler
 */
public abstract class SmyLibGuiTest {

    @BeforeEach
    public void initSmyLibGui() {
        // We are doing it before each test, so we clear devices' states
        SmyLibGui.init(null, JUNIT);
    }

}
