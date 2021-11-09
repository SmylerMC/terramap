package fr.thesmyler.smylibgui.widgets.text;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.TestingScreen;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TextFieldWidgetTest {

    @BeforeAll
    static void beforeAll() {
        SmyLibGui.debug = true;
    }

    @Test
    public void testActivation() throws InterruptedException {

        TestingScreen screen = new TestingScreen(30, 400, 300);
        TextFieldWidget widget = new TextFieldWidget(10, 10, 0, 200, new MockFont(1f));
        screen.addWidget(widget);

        assertNull(screen.getFocusedWidget());

        // Click the input field
        screen.moveMouse(50, 20, 250);
        screen.click(0);
        screen.doTick();

        assertEquals(widget, screen.getFocusedWidget());

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

        //TODO this fails: assertNull(screen.getFocusedWidget());
    }

}
