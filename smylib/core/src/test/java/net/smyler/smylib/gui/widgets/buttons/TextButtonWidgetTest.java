package net.smyler.smylib.gui.widgets.buttons;


import net.smyler.smylib.SmyLibTest;
import net.smyler.smylib.game.TestGameClient;
import net.smyler.smylib.gui.screen.Screen;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TextButtonWidgetTest extends SmyLibTest {

    @Test
    public void testActivation() throws InterruptedException {
        AtomicInteger clickCounter = new AtomicInteger();
        TestGameClient client = this.getTestGameClient();
        Screen screen = this.getTestGameClient().getCurrentScreen();
        TextButtonWidget button = new TextButtonWidget(10, 10, 0, 200, "Test button", clickCounter::incrementAndGet);
        screen.addWidget(button);

        assertNull(screen.getFocusedWidget());

        client.moveMouse(30, 30, 500);
        client.click(0);
        client.doTick();
        client.doTick(); // We are giving it a frame to lose focus
        assertEquals(1, clickCounter.get());
        assertNull(screen.getFocusedWidget());

        client.doubleClick(0);
        client.doTick();
        client.doTick();
        assertEquals(3, clickCounter.get());
        assertNull(screen.getFocusedWidget());

        client.moveMouse(1, 1, 500);
        client.click(0);
        client.doTick();
        client.doTick();
        assertEquals(3, clickCounter.get());
        assertNull(screen.getFocusedWidget());

        client.moveMouse(30, 30, 500);
        client.click(0);
        client.doTick();
        client.doTick();
        assertEquals(4, clickCounter.get());
        assertNull(screen.getFocusedWidget());

        button.setEnabled(false);
        client.click(0);
        client.doTick();
        client.doTick();
        assertEquals(4, clickCounter.get());
        assertNull(screen.getFocusedWidget());
    }

}
