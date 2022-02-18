package fr.thesmyler.smylibgui;

import org.junit.jupiter.api.BeforeEach;

import static fr.thesmyler.smylibgui.SmyLibGuiContext.JUNIT;

public abstract class SmyLibGuiTest {

    @BeforeEach
    public void initSmyLibGui() {
        SmyLibGui.init(null, JUNIT);
    }

}
