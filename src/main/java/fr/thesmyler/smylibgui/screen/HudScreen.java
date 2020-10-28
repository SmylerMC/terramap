package fr.thesmyler.smylibgui.screen;

import fr.thesmyler.smylibgui.event.HudScreenInitEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class HudScreen extends Screen {

	private int lastRenderWidth = 0;
	private int lastRenderHeight = 0;
	private GuiScreen lastTickScreen = null;
	
	public HudScreen() {
		super(BackgroundType.NONE);
	}
	
	@SubscribeEvent
	public void onRenderHUD(RenderGameOverlayEvent.Pre e) {
		if(!e.getType().equals(ElementType.HOTBAR)) return;
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
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		if(event.phase.equals(Phase.START)) {
			GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
			if(Minecraft.getMinecraft().world != null && (this.lastTickScreen != null || currentScreen != null) && (this.lastTickScreen == null || !this.lastTickScreen.equals(currentScreen))) {
				this.initScreen();
			}
			this.lastTickScreen = currentScreen;
		}
	}

	@Override
	public void initScreen() {
		MinecraftForge.EVENT_BUS.post(new HudScreenInitEvent(this));
		super.initScreen();
	}
	
	
	
}
