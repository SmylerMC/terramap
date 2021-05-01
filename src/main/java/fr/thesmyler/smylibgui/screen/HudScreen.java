package fr.thesmyler.smylibgui.screen;

import java.lang.reflect.Field;
import java.util.List;

import org.lwjgl.input.Mouse;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.event.HudScreenInitEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

//TODO Mouse interaction with the HUD
public final class HudScreen {

	private static int renderWidth = 0;
	private static int renderHeight = 0;
	private static GuiScreen lastTickScreen = null;
	
	private static HudScreenContainer container = new HudScreenContainer(0);
	
	private static final Field NEW_CHAT_DRAW_CHAT_LINES_FIELD = ObfuscationReflectionHelper.findField(GuiNewChat.class, "field_146253_i");
	private static final Field GUI_INGAME_UPDATE_COUNTER_FIELD = ObfuscationReflectionHelper.findField(GuiIngame.class, "field_73837_f");
	
	private HudScreen() {}
	
	@SubscribeEvent
	public static void onRenderHUD(RenderGameOverlayEvent.Pre e) {
		if(!e.getType().equals(ElementType.HOTBAR)) return;
		boolean chatOpen = Minecraft.getMinecraft().currentScreen instanceof GuiChat;
		ScaledResolution res = e.getResolution();
		int width = res.getScaledWidth();
		int height = res.getScaledHeight();
		if(width != renderWidth || height != renderHeight) {
			renderWidth = width;
			renderHeight = height;
			init();
		}
        float mouseX = (float)Mouse.getX() / res.getScaleFactor();
        float mouseY = height - (float)Mouse.getY() / res.getScaleFactor() - 1;
		container.onUpdate(null);
		container.draw(0, 0, mouseX, mouseY, chatOpen && !isOverChat(mouseX, mouseY), false, null);
		GlStateManager.enableAlpha();
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
	
	public static boolean isOverChat(float x, float y) {
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.ingameGUI == null) return false;
		boolean chatOpen = mc.currentScreen instanceof GuiChat;
        
		// Check if we are on top of the text box
        if(chatOpen) {
        	float chatLeft = 2;
        	float chatRight = renderWidth - 2;
        	float chatTop = renderHeight - 14;
        	float chatBottom = renderHeight - 2;
        	if(x >= chatLeft && x <= chatRight && y >= chatTop && y <= chatBottom) return true;
        }
        
        GuiNewChat chat = mc.ingameGUI.getChatGUI();
        float chatLeft = 0;
        float chatBottom = renderHeight - chat.getChatHeight();
        float chatRight = chatLeft + chat.getChatWidth() + 6;
        float chatTop = renderHeight - 40;
        float scale = chat.getChatScale();
        try {
        	int updateCounter = GUI_INGAME_UPDATE_COUNTER_FIELD.getInt(mc.ingameGUI);
        	@SuppressWarnings("unchecked") // Taken care of in the catch block
			List<ChatLine> lines = (List<ChatLine>)NEW_CHAT_DRAW_CHAT_LINES_FIELD.get(chat);
        	int visibleChatLines = 0;
        	for(ChatLine line: lines) {
        		if(chatOpen || (line != null && updateCounter - line.getUpdatedCounter() < 200)) visibleChatLines++;
        	}
        	chatBottom = renderHeight - 40;
        	chatTop = chatBottom - Math.min(visibleChatLines * 9 / scale, chat.getChatHeight());
        } catch (ClassCastException | IllegalArgumentException | IllegalAccessException e) {
        	if(SmyLibGui.debug) {
        		SmyLibGui.logger.warn("Reflection fail when checking chat position!");
        		SmyLibGui.logger.catching(e);
        	}
        }
        return x >= chatLeft && x <= chatRight && y <= chatBottom && y >= chatTop;
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
