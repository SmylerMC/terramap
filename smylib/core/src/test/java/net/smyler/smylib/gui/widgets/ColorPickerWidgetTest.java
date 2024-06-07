package net.smyler.smylib.gui.widgets;

import net.smyler.smylib.game.TestGameClient;
import net.smyler.smylib.SmyLibTest;
import net.smyler.smylib.game.Key;
import net.smyler.smylib.gui.screen.Screen;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static net.smyler.smylib.Color.BLACK;
import static net.smyler.smylib.Color.RED;
import static net.smyler.smylib.SmyLib.getGameClient;
import static org.junit.jupiter.api.Assertions.*;

class ColorPickerWidgetTest extends SmyLibTest {

    @Test
    void canManipulateColorsWithAccessors() {
        ColorPickerWidget colorPicker = new ColorPickerWidget(0, 0, 0, BLACK, getGameClient().defaultFont());
        assertEquals(BLACK, colorPicker.getColor());
        colorPicker.setColor(RED);
        assertEquals(RED, colorPicker.getColor());
    }

    @Test
    void canProperlyDetectValidColors() {
        ColorPickerWidget widget = new ColorPickerWidget(0, 0, 0, BLACK, getGameClient().defaultFont());
        assertTrue(widget.hasValidColor());
        widget.setText("#FFFFFF");
        assertTrue(widget.hasValidColor());
        widget.setText("FFFFFF");
        assertFalse(widget.hasValidColor());
        widget.setText("#AAAAAAFF");
        assertTrue(widget.hasValidColor());
        widget.setText("#0123456");
        assertFalse(widget.hasValidColor());
    }

    @Test
    void canDealWithCustomOnTextChangedCallback() {
        ColorPickerWidget colorPicker = new ColorPickerWidget(0, 0, 0, BLACK, getGameClient().defaultFont());
        AtomicBoolean flag = new AtomicBoolean(false);
        colorPicker.setOnChangeCallback(s -> flag.set(true));
        colorPicker.setColor(RED);
        assertTrue(flag.get());
    }

    @Test
    void canInputColors() throws InterruptedException {
        TestGameClient client = this.getTestGameClient();
        Screen container = client.getCurrentScreen();
        ColorPickerWidget colorPicker = new ColorPickerWidget(0, 0, 0, BLACK, getGameClient().defaultFont());
        container.addWidget(colorPicker);

        // Focus the widget and move cursor to the right
        client.moveMouse(10f, 10f, 500);
        client.click(0);
        for (int i = 0; i < 10; i++) {
            client.pressKey((char)Key.KEY_RIGHT.code, Key.KEY_RIGHT);
            client.doTick();
        }

        client.pressKey((char) Key.KEY_BACK.code, Key.KEY_BACK);
        client.doTick();
        client.pressKey((char) Key.KEY_BACK.code, Key.KEY_BACK);
        client.doTick();
        client.pressKey((char) Key.KEY_BACK.code, Key.KEY_BACK);
        client.doTick();

        assertFalse(colorPicker.hasValidColor());
        assertEquals("#000", colorPicker.getText());

        client.pressKey((char) Key.KEY_BACK.code, Key.KEY_BACK);
        client.doTick();
        client.pressKey((char) Key.KEY_BACK.code, Key.KEY_BACK);
        client.doTick();
        client.pressKey((char) Key.KEY_BACK.code, Key.KEY_BACK);
        client.doTick();

        assertFalse(colorPicker.hasValidColor());
        assertEquals("#", colorPicker.getText());

        client.pressKey('F', Key.KEY_F);
        client.doTick();
        client.pressKey('F', Key.KEY_F);
        client.doTick();
        client.pressKey('0', Key.KEY_0);
        client.doTick();
        client.pressKey('0', Key.KEY_0);
        client.doTick();
        client.pressKey('0', Key.KEY_0);
        client.doTick();
        client.pressKey('0', Key.KEY_0);
        client.doTick();

        assertTrue(colorPicker.hasValidColor());
        assertEquals(RED, colorPicker.getColor());

        client.pressKey('F', Key.KEY_F);
        client.doTick();
        client.pressKey('F', Key.KEY_F);
        client.doTick();

        assertTrue(colorPicker.hasValidColor());
        assertEquals(RED, colorPicker.getColor());
    }

}