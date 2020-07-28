package fr.thesmyler.smylibgui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HudScreen extends Screen {

	private int lastRenderWidth = 0;
	private int lastRenderHeight = 0;
	
	public HudScreen() {
		super(BackgroundType.NONE);
	}
	
	@SubscribeEvent
	public void onRenderHUD(RenderGameOverlayEvent.Post e) {
		ScaledResolution res = e.getResolution();
		int width = res.getScaledWidth();
		int height = res.getScaledHeight();
		if(width != this.lastRenderWidth || height != this.lastRenderHeight) {
			this.setWorldAndResolution(Minecraft.getMinecraft(), width, height);
		}
		this.onUpdate(null);
		this.drawScreen(res.getScaledWidth()/2, res.getScaledHeight()/2, e.getPartialTicks());
		this.lastRenderWidth = width;
		this.lastRenderHeight = height;
	}
	
}
