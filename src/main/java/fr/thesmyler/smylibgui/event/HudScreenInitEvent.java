package fr.thesmyler.smylibgui.event;

import fr.thesmyler.smylibgui.screen.HudScreen;
import net.minecraftforge.fml.common.eventhandler.Event;

public class HudScreenInitEvent extends Event {
	
	private HudScreen hud;
	
	public HudScreenInitEvent(HudScreen screen) {
		this.hud = screen;
	}
	
	public HudScreen getHudScreen() {
		return this.hud;
	}

}
