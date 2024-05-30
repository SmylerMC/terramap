package net.smyler.smylib.gui.popups;

import net.smyler.smylib.gui.widgets.buttons.TextButtonWidget;
import net.smyler.smylib.gui.widgets.text.TextAlignment;
import net.smyler.smylib.gui.widgets.text.TextWidget;
import net.smyler.smylib.text.Text;

import static net.smyler.smylib.SmyLib.getGameClient;


/**
 * A very simple popup that shows a message with an "OK" button.
 *
 * @author Smyler
 */
public class TextPopup extends Popup {

    public TextPopup(Text message) {
        super(10f, 10f);
        TextWidget text = new TextWidget(0, 0, 0, message, TextAlignment.CENTER, getGameClient().defaultFont());
        float padding = 10;
        text.setMaxWidth(300);
        text.setAnchorX(text.getWidth() / 2 + padding).setAnchorY(padding);
        TextButtonWidget button = new TextButtonWidget(
                text.getWidth() / 2 + padding - 20,
                text.getY() + text.getHeight() + padding,
                1, 40, getGameClient().translator().format("smylibgui.popup.info.ok"));
        this.resize(text.getWidth() + padding*2, button.getY() + padding + button.getHeight());
        button.setOnClick(this::close);
        button.enable();
        this.addWidget(text);
        this.addWidget(button);
    }

    public static void show(Text message) {
        getGameClient().displayPopup(new TextPopup(message));
    }

}
