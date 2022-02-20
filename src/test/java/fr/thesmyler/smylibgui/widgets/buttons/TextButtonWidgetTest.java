package fr.thesmyler.smylibgui.widgets.buttons;

import fr.thesmyler.smylibgui.SmyLibGuiTest;
import fr.thesmyler.smylibgui.container.TestingWidgetContainer;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TextButtonWidgetTest extends SmyLibGuiTest {

    @Test
    public void testActivation() throws InterruptedException {
        AtomicInteger clickCounter = new AtomicInteger();
        TestingWidgetContainer screen = new TestingWidgetContainer(30, 400, 300);
        TextButtonWidget button = new TextButtonWidget(10, 10, 0, 200, "Test button", clickCounter::incrementAndGet);
        screen.addWidget(button);

        assertNull(screen.getFocusedWidget());

        screen.moveMouse(30, 30, 500);
        screen.click(0);
        screen.doTick();
        screen.doTick(); // We are giving it a frame to lose focus
        assertEquals(1, clickCounter.get());
        assertNull(screen.getFocusedWidget());

        screen.doubleClick(0);
        screen.doTick();
        screen.doTick();
        assertEquals(3, clickCounter.get());
        assertNull(screen.getFocusedWidget());

        screen.moveMouse(1, 1, 500);
        screen.click(0);
        screen.doTick();
        screen.doTick();
        assertEquals(3, clickCounter.get());
        assertNull(screen.getFocusedWidget());

        screen.moveMouse(30, 30, 500);
        screen.click(0);
        screen.doTick();
        screen.doTick();
        assertEquals(4, clickCounter.get());
        assertNull(screen.getFocusedWidget());

        button.setEnabled(false);
        screen.click(0);
        screen.doTick();
        screen.doTick();
        assertEquals(4, clickCounter.get());
        assertNull(screen.getFocusedWidget());
    }

}
