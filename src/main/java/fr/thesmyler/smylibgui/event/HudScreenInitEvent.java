package fr.thesmyler.smylibgui.event;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import net.minecraftforge.fml.common.eventhandler.Event;

public class HudScreenInitEvent extends Event {

    private WidgetContainer content;

    public HudScreenInitEvent(WidgetContainer screen) {
        this.content = screen;
    }

    public WidgetContainer getHudScreen() {
        return this.content;
    }

}
