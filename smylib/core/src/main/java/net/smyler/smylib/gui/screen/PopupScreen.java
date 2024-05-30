package net.smyler.smylib.gui.screen;

import net.smyler.smylib.game.GameClient;
import net.smyler.smylib.gui.popups.Popup;


/**
 * An instance of {@link PopupScreen} is returned by {@link GameClient#getCurrentScreen()}
 * when a popup is currently being displayed.
 * You should not need to extend this class, and its implementation are considered internals.
 *
 * @author Smyler
 */
public abstract class PopupScreen extends Screen {

    private final Popup popup;

    public PopupScreen(Popup popup) {
        super(BackgroundOption.NONE);
        this.popup = popup;
        this.addWidget(popup);
    }

    public Popup getPopup() {
        return this.popup;
    }

}
