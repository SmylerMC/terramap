package fr.thesmyler.smylibgui.widgets.sliders;

import fr.thesmyler.smylibgui.SmyLibGuiTest;
import fr.thesmyler.smylibgui.container.TestingWidgetContainer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OptionSliderWidgetTest extends SmyLibGuiTest {

    @Test
    public void testActivation() throws InterruptedException {
        TestingWidgetContainer screen = new TestingWidgetContainer(30, 1080, 720);
        OptionSliderWidget<String> slider = new OptionSliderWidget<>(
                100, 100, 0, 200, 20f,
                new String[] {"Option1", "Option2", "Option3"}, 2,
                s -> {});
        screen.addWidget(slider);

        // Starting point: option 3, not focused
        assertNull(screen.getFocusedWidget());
        assertEquals("Option3", slider.getCurrentOption());

        // Select option 1 and focus
        screen.moveMouse(110f, 110f, 100);
        screen.click(0);
        screen.doTick();
        assertSame(slider, screen.getFocusedWidget());
        assertEquals("Option1", slider.getCurrentOption());

        // Lose focus
        screen.moveMouse(10f, 10f, 100);
        screen.click(0);
        screen.doTick();
        assertNull(screen.getFocusedWidget());
        assertEquals("Option1", slider.getCurrentOption());

        // Disable and try to select option 2
        slider.setEnabled(false);
        screen.moveMouse(210f, 110f, 100);
        screen.click(0);
        screen.doTick();
        assertFalse(slider.isEnabled());
        assertNull(screen.getFocusedWidget());
        assertEquals("Option1", slider.getCurrentOption());

    }

}
