package fr.thesmyler.smylibgui.widgets.text;

import fr.thesmyler.smylibgui.SmyLibGuiTest;
import fr.thesmyler.smylibgui.container.TestingScreen;
import fr.thesmyler.smylibgui.devices.Key;
import fr.thesmyler.smylibgui.util.DummyFont;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TextFieldWidgetTest extends SmyLibGuiTest {

    @Test
    public void testActivation() throws InterruptedException {

        TestingScreen screen = new TestingScreen(30, 400, 300);
        TextFieldWidget widget = new TextFieldWidget(10, 10, 0, 200, new DummyFont(1f));
        screen.addWidget(widget);

        assertNull(screen.getFocusedWidget());

        // Click the input field
        screen.moveMouse(50, 20, 250);
        screen.click(0);
        screen.doTick();

        assertEquals(widget, screen.getFocusedWidget());

        // Send input to the input field
        screen.pressKey('a', Key.KEY_A);
        screen.doTick();
        screen.pressKey('b', Key.KEY_B);
        screen.doTick();
        screen.pressKey('c', Key.KEY_C);
        screen.doTick();

        assertEquals("abc", widget.getText());

        // Click on nothing
        screen.moveMouse(1, 1, 250);
        screen.click(0);
        screen.doTick();

        assertNull(screen.getFocusedWidget());

        // Disable and click this input field
        widget.setEnabled(false);
        screen.moveMouse(50, 20, 250);
        screen.click(0);
        screen.doTick();

        assertNull(screen.getFocusedWidget());

    }

}
