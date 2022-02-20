package fr.thesmyler.smylibgui.event;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * An event fired when the HUD {@link fr.thesmyler.smylibgui.screen.Screen} initializes.
 *
 * @author SmylerMC
 */
public class HudScreenInitEvent extends Event {

    private final WidgetContainer content;

    public HudScreenInitEvent(WidgetContainer screen) {
        this.content = screen;
    }

    public WidgetContainer getHudScreen() {
        return this.content;
    }

}
