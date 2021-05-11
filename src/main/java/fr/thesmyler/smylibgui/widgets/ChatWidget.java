package fr.thesmyler.smylibgui.widgets;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.lwjgl.input.Keyboard;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.screen.HudScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ITabCompleter;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

/**
 * Kinda hacky widget to add the chat to a custom screen.
 * Due to the way tab completion is implemented in the game,
 * it will not work unless you make the GuiScreen that contains this widget implent {@link ITabCompleter},
 * and call this widget's {@link #setCompletions} from there
 * @author SmylerMC
 *
 */
public class ChatWidget implements IWidget, ITabCompleter {
	
	private float lineSectionX, lineSectionY, lineSectionWidth, LineSectionHeight;
	private final int z;
	private boolean open;
	private GuiChat guiChat = new GuiChat();

	private static final Method GUI_CHAT_KEYPRESSED_METHOD = ObfuscationReflectionHelper.findMethod(GuiChat.class, "func_73869_a", Void.TYPE, Character.TYPE, Integer.TYPE);
	private static final Method GUI_CHAT_MOUSE_CLICKED_METHOD = ObfuscationReflectionHelper.findMethod(GuiChat.class, "func_73864_a", Void.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
	private static final Field GUI_CHAT_INPUTFIELD_FIELD = ObfuscationReflectionHelper.findField(GuiChat.class, "field_146415_a");
	
	public ChatWidget(int z) {
		this.z = z;
	}
	
	@Override
	public void onUpdate(WidgetContainer parent) {
		float[] bbox = HudScreen.getChatLinesBoundingBox();
		this.lineSectionX = bbox[0];
		this.lineSectionY = bbox[1];
		this.lineSectionWidth = bbox[2] - bbox[0];
		this.LineSectionHeight = bbox[3] - bbox[1];
		WidgetContainer hud = HudScreen.getContent();
		int width = (int) hud.getWidth();
		int height = (int) hud.getHeight();
		if(this.guiChat.mc == null || this.guiChat.width != width || this.guiChat.height != height) {
			this.guiChat.setWorldAndResolution(Minecraft.getMinecraft(), (int)hud.getWidth(), (int)hud.getHeight());
		}
		if(parent != null && this.open) parent.setFocus(this);
		if(parent != null && this.equals(parent.getFocusedWidget()) && !this.open) parent.setFocus(null);
		this.guiChat.updateScreen();
	}

	@Override
	public float getX() {
		return this.open ? 0: this.lineSectionX;
	}

	@Override
	public float getY() {
		return this.open ? 0: this.lineSectionY;
	}

	@Override
	public int getZ() {
		return this.z;
	}

	@Override
	public float getWidth() {
		return this.open ? HudScreen.getContent().getWidth(): this.lineSectionWidth;
	}

	@Override
	public float getHeight() {
		return this.open ? HudScreen.getContent().getHeight(): this.LineSectionHeight;
	}

	@Override
	public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
		Minecraft mc = Minecraft.getMinecraft();
		GuiScreen previousScreen =  mc.currentScreen;
		if(this.open) {
			mc.currentScreen = this.guiChat;
			this.guiChat.drawScreen(Math.round(mouseX), Math.round(mouseY), 0);
		}
		int updateCounter = Integer.MIN_VALUE;
		try {
			updateCounter = HudScreen.getChatUpdateCounter();
		} catch (IllegalArgumentException | IllegalAccessException e) {
			if(SmyLibGui.debug) {
				SmyLibGui.logger.warn("Failed to do reflection stuff for ChatWidget");
				SmyLibGui.logger.catching(e);
			}
		}
		GlStateManager.pushMatrix();
		GlStateManager.translate(0f, HudScreen.getContent().getHeight() - 48f, 0f);
		mc.ingameGUI.getChatGUI().drawChat(updateCounter);
		GlStateManager.popMatrix();
		if(this.open) {
			mc.currentScreen = previousScreen;
		}
	}

	@Override
	public boolean isVisible(WidgetContainer parent) {
		return this.LineSectionHeight > 0 || this.open;
	}
	
	@Override
	public boolean takesInputs() {
		return true;
	}

	@Override
	public boolean onClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
		try {
			GUI_CHAT_MOUSE_CLICKED_METHOD.invoke(this.guiChat, Math.round(mouseX), Math.round(mouseY), mouseButton);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			if(SmyLibGui.debug) {
				SmyLibGui.logger.debug("Failed some reflection in ChatWidget#onCLick");
				SmyLibGui.logger.catching(e);
			}
		}
		return false;
	}

	@Override
	public boolean onDoubleClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
		return false;
	}

	@Override
	public boolean onMouseWheeled(float mouseX, float mouseY, int amount, WidgetContainer parent) {
		return false;
	}
	
	@Override
	public void onKeyTyped(char typedChar, int keyCode, WidgetContainer parent) {
		if(keyCode == Keyboard.KEY_ESCAPE) {
			this.setOpen(false);
			return;
		}
		try {
			if(keyCode == Keyboard.KEY_RETURN) {
				// We have to do it this way or this.guiChat would close the parent screen
				GuiTextField textField = (GuiTextField)GUI_CHAT_INPUTFIELD_FIELD.get(this.guiChat);
				this.guiChat.sendChatMessage(textField.getText().trim());
				this.setOpen(false);
			} else {
				// We need to swap the current screen temporarily so Forge client commands are tab completable
				GuiScreen screen = Minecraft.getMinecraft().currentScreen;
				Minecraft.getMinecraft().currentScreen = this.guiChat;
				GUI_CHAT_KEYPRESSED_METHOD.invoke(this.guiChat, typedChar, keyCode);
				Minecraft.getMinecraft().currentScreen = screen;
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			if(SmyLibGui.debug) {
				SmyLibGui.logger.error("Failed to call reflected method in ChatWidget!");
				SmyLibGui.logger.catching(e);
			}
		}
	}

	public boolean isOpen() {
		return this.open;
	}
	
	public void setOpen(boolean open) {
		if(open) this.guiChat.initGui(); // Reset chat content
		this.open = open;
	}

	@Override
	public void setCompletions(String... newCompletions) {
		this.guiChat.setCompletions(newCompletions);
	}
	
}
