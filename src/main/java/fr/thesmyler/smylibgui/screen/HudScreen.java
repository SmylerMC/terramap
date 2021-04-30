package fr.thesmyler.smylibgui.screen;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.event.HudScreenInitEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

//TODO Mose interaction with the HUD
public final class HudScreen {

	private static int renderWidth = 0;
	private static int renderHeight = 0;
	private static GuiScreen lastTickScreen = null;
	
	private static HudScreenContainer container = new HudScreenContainer(0);
	
	private HudScreen() {}
	
	@SubscribeEvent
	public static void onRenderHUD(RenderGameOverlayEvent.Pre e) {
		if(!e.getType().equals(ElementType.HOTBAR)) return;
		ScaledResolution res = e.getResolution();
		int width = res.getScaledWidth();
		int height = res.getScaledHeight();
		if(width != renderWidth || height != renderHeight) {
			renderWidth = width;
			renderHeight = height;
			init();
		}
		container.onUpdate(null);
		container.draw(0, 0, res.getScaledWidth()/2, res.getScaledHeight()/2, false, false, null);
		GlStateManager.color(1f, 1f, 1f, .5f); // Reset color to what it was
	}
	
	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event) {
		if(event.phase.equals(Phase.START)) {
			GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
			if(Minecraft.getMinecraft().world != null && (lastTickScreen != null || currentScreen != null) && (lastTickScreen == null || !lastTickScreen.equals(currentScreen))) {
				init();
			}
			lastTickScreen = currentScreen;
		}
	}

	private static void init() {
		MinecraftForge.EVENT_BUS.post(new HudScreenInitEvent(container));
		container.init();
	}
	
	public static WidgetContainer getContent() {
		return container;
	}
	
	private static class HudScreenContainer extends WidgetContainer {

		public HudScreenContainer(int z) {
			super(z);
		}

		@Override
		public float getX() {
			return 0;
		}

		@Override
		public float getY() {
			return 0;
		}

		@Override
		public float getWidth() {
			return renderWidth;
		}

		@Override
		public float getHeight() {
			return renderHeight;
		}
		
	}
	
}
