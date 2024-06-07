package net.smyler.smylib.gui.screen;

import net.smyler.smylib.gui.popups.Popup;

public class TestPopupScreen extends PopupScreen {

    public final Screen background;

    public TestPopupScreen(Screen background, Popup popup) {
        super(popup);
        this.background = background;
    }

}
