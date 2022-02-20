package fr.thesmyler.smylibgui.widgets.buttons;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.SmyLibGuiTest;
import fr.thesmyler.smylibgui.container.TestingWidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

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
        screen.doTick(); // We are giving it a frame to lose focus
        assertEquals(1, clickCounter.get());
        assertNull(screen.getFocusedWidget());

        screen.doubleClick(0);
        screen.doTick();
        assertEquals(3, clickCounter.get());
        assertNull(screen.getFocusedWidget());

        screen.moveMouse(1, 1, 500);
        screen.click(0);
        screen.doTick();
        assertEquals(3, clickCounter.get());
        assertNull(screen.getFocusedWidget());

        screen.moveMouse(30, 30, 500);
        screen.click(0);
        screen.doTick();
        assertEquals(4, clickCounter.get());
        assertNull(screen.getFocusedWidget());

        button.setEnabled(false);
        screen.click(0);
        screen.doTick();
        assertEquals(4, clickCounter.get());
        assertNull(screen.getFocusedWidget());

        button.enable();
        screen.click(0);
        screen.doTick();
        assertEquals(5, clickCounter.get());
        assertNull(screen.getFocusedWidget());

        button.disable();
        screen.click(0);
        screen.doTick();
        assertEquals(5, clickCounter.get());
        assertNull(screen.getFocusedWidget());
    }

    @Test
    public void testConstructors() {
        Runnable onClick = () -> {};
        Runnable onDoubleClick = () -> {};

        // Full constructor
        TextButtonWidget button = new TextButtonWidget(
                15.8f, 45.1f, 1,
                225.3f,
                "Text",
                onClick, onDoubleClick);
        assertEquals(15.8f, button.getX());
        assertEquals(45.1f, button.getY());
        assertEquals(1, button.getZ());
        assertEquals(225.3f, button.getWidth());
        assertEquals(20f, button.getHeight());
        assertEquals("Text", button.getText());
        assertSame(onClick, button.getOnClick());
        assertSame(onDoubleClick, button.getOnDoubleClick());
        assertTrue(button.isEnabled());
        assertTrue(button.isVisible(null));

        // No double click callback
        button = new TextButtonWidget(
                15.8f, 45.1f, 1,
                225.3f,
                "Text",
                onClick);
        assertEquals(15.8f, button.getX());
        assertEquals(45.1f, button.getY());
        assertEquals(1, button.getZ());
        assertEquals(225.3f, button.getWidth());
        assertEquals(20f, button.getHeight());
        assertEquals("Text", button.getText());
        assertSame(onClick, button.getOnClick());
        assertNull(button.getOnDoubleClick());
        assertTrue(button.isEnabled());
        assertTrue(button.isVisible(null));

        // No callbacks (disabled button)
        button = new TextButtonWidget(
                15.8f, 45.1f, 1,
                225.3f,
                "Text");
        assertEquals(15.8f, button.getX());
        assertEquals(45.1f, button.getY());
        assertEquals(1, button.getZ());
        assertEquals(225.3f, button.getWidth());
        assertEquals(20f, button.getHeight());
        assertEquals("Text", button.getText());
        assertNull(button.getOnClick());
        assertNull(button.getOnDoubleClick());
        assertFalse(button.isEnabled());
        assertTrue(button.isVisible(null));

        // Ful constructor no position nor size
        button = new TextButtonWidget(
                1,
                "Text",
                onClick, onDoubleClick);
        assertEquals(1, button.getZ());
        assertEquals("Text", button.getText());
        assertSame(onClick, button.getOnClick());
        assertSame(onDoubleClick, button.getOnDoubleClick());
        assertTrue(button.isEnabled());
        assertTrue(button.isVisible(null));

        // No position nor size, no double click callback
        button = new TextButtonWidget(
                1,
                "Text",
                onClick);
        assertEquals(1, button.getZ());
        assertEquals("Text", button.getText());
        assertSame(onClick, button.getOnClick());
        assertNull(button.getOnDoubleClick());
        assertTrue(button.isEnabled());
        assertTrue(button.isVisible(null));

        // No position nor size, no callbacks (disabled)
        button = new TextButtonWidget(
                1,
                "Text");
        assertEquals(1, button.getZ());
        assertEquals("Text", button.getText());
        assertNull(button.getOnClick());
        assertNull(button.getOnDoubleClick());
        assertFalse(button.isEnabled());
        assertTrue(button.isVisible(null));

    }

    @Test
    public void testAccessors() {
        Runnable action1 = () -> {};
        Runnable action2 = () -> {};
        TextButtonWidget button = new TextButtonWidget(
                15.8f, 45.1f, 1,
                225.3f,
                "Text",
                action1, action2);

        button.setX(25.6f);
        assertEquals(25.6f, button.getX());

        button.setY(813.2f);
        assertEquals(813.2f, button.getY());

        button.setWidth(253.7f);
        assertEquals(253.7f, button.getWidth());

        button.setText("New text");
        assertEquals("New text", button.getText());

        button.setTooltip("Test tooltip");
        assertEquals("Test tooltip", button.getTooltipText());

        button.setOnClick(null);
        button.setOnDoubleClick(null);
        assertNull(button.getOnClick());
        assertNull(button.getOnDoubleClick());
        button.setOnClick(action2);
        assertSame(action2, button.getOnClick());
        button.setOnDoubleClick(action1);
        assertSame(action1, button.getOnDoubleClick());

        button.setActiveTextColor(Color.RED);
        assertEquals(Color.RED, button.getActiveTextColor());
        button.setEnabledTextColor(Color.GREEN);
        assertEquals(Color.GREEN, button.getEnabledTextColor());
        button.setDisabledTextColor(Color.BLUE);
        assertEquals(Color.BLUE, button.getDisabledTextColor());
    }

    @Test
    public void testCallbacks() throws InterruptedException {

        AtomicInteger clickCounter = new AtomicInteger();
        AtomicInteger doubleClickCounter = new AtomicInteger();
        TestingWidgetContainer screen = new TestingWidgetContainer(30, 1280, 720);
        TextButtonWidget button = new TextButtonWidget(10, 10, 0, 200, "Test button", clickCounter::incrementAndGet);
        screen.addWidget(button);
        long waitBetween = SmyLibGui.getMouse().getDoubleClickDelay() + (long)(1000d/30d); // Ensure no involuntary double click

        // Move mouse over
        screen.moveMouse(15, 15, 100);
        assertEquals(0, clickCounter.get());
        assertEquals(0, doubleClickCounter.get());

        // Single click
        screen.click(0);
        assertEquals(1, clickCounter.get());
        assertEquals(0, doubleClickCounter.get());
        screen.runFor(waitBetween);

        // Double click, no double click callback
        screen.doubleClick(0);
        assertEquals(3, clickCounter.get());
        assertEquals(0, doubleClickCounter.get());
        screen.runFor(waitBetween);

        // Double click, with double click callback
        button.setOnDoubleClick(doubleClickCounter::incrementAndGet);
        screen.doubleClick(0);
        assertEquals(4, clickCounter.get());
        assertEquals(1, doubleClickCounter.get());
        screen.runFor(waitBetween);

        // Single right click
        screen.click(1);
        assertEquals(4, clickCounter.get());
        assertEquals(1, doubleClickCounter.get());
        screen.runFor(waitBetween);

        // Double click, with no single click callback
        button.setOnClick(null);
        screen.doubleClick(0);
        assertEquals(4, clickCounter.get());
        assertEquals(2, doubleClickCounter.get());
        screen.runFor(waitBetween);

        // Double right click
        screen.click(1);
        assertEquals(4, clickCounter.get());
        assertEquals(2, doubleClickCounter.get());
        screen.runFor(waitBetween);

    }

}
