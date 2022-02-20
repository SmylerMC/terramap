package fr.thesmyler.smylibgui.widgets.text;

import fr.thesmyler.smylibgui.SmyLibGuiTest;
import fr.thesmyler.smylibgui.container.TestingWidgetContainer;
import fr.thesmyler.smylibgui.devices.Key;
import fr.thesmyler.smylibgui.util.DummyFont;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TextFieldWidgetTest extends SmyLibGuiTest {

    @Test
    public void testActivation() throws InterruptedException {

        TestingWidgetContainer screen = new TestingWidgetContainer(30, 400, 300);
        TextFieldWidget widget = new TextFieldWidget(10, 10, 0, 200, new DummyFont(1f));
        screen.addWidget(widget);

        assertNull(screen.getFocusedWidget());

        // Click the input field
        screen.moveMouse(50, 20, 250);
        screen.click(0);

        assertSame(widget, screen.getFocusedWidget());

        // Send input to the input field
        screen.pressKey('a', Key.KEY_A);
        screen.pressKey('b', Key.KEY_B);
        screen.pressKey('c', Key.KEY_C);

        assertEquals("abc", widget.getText());

        // Click on nothing
        screen.moveMouse(1, 1, 250);
        screen.click(0);

        assertNull(screen.getFocusedWidget());

        // Disable and click this input field
        widget.setEnabled(false);
        screen.moveMouse(50, 20, 250);
        screen.click(0);

        assertNull(screen.getFocusedWidget());

    }

}
