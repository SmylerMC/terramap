package fr.thesmyler.smylibgui.widgets;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import fr.thesmyler.smylibgui.screen.Screen;
import net.minecraft.client.Minecraft;

public class TextFieldWidget implements IWidget {

	private String text = "test";
	private int x, y, width, height, z;
	private int cursorIndex, deltaIndex; //TODO Actually implement
	
	public TextFieldWidget(int x, int y, int width, int z) {
		this(x, y, width, z, "");
	}
	
	public TextFieldWidget(int x, int y, int width, int z, String defaultText) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.height = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 10;
		this.text = defaultText;
		this.cursorIndex = this.text.length();
	}
	
	@Override
	public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean focused, Screen parent) {
		int height = this.getHeight();
		int width = this.getWidth();
		Screen.drawRect(x, y, x + width, y + height, 0x80000000);
		int borderColor = focused? 0xFFDFDFDF: 0xFF808080;
		int textColor = focused? 0xFFCFCFCF: 0xFF606060;
		Screen.drawRect(x, y - 1, x + width, y, borderColor);
		Screen.drawRect(x, y + height, x + width, y + height + 1, borderColor);
		Screen.drawRect(x + width, y, x + width + 1, y + height, borderColor);
		Screen.drawRect(x-1, y, x, y + height, borderColor);
		parent.getFont().drawString(this.text, x + 5, y + 5, textColor);
	}
	
	@Override
	public boolean onClick(int mouseX, int mouseY, int mouseButton, @Nullable Screen parent) {
		return false;
	}
	
	@Override
	public boolean onDoubleClick(int mouseX, int mouseY, int mouseButton, @Nullable Screen parent) {
		return false;
	}
	
	@Override
	public void onKeyTyped(char typedChar, int keyCode, @Nullable Screen parent) {
		switch(keyCode) {
		case Keyboard.KEY_BACK:
			this.text = this.text.substring(0, this.text.length() - 1);
			break;
		case Keyboard.KEY_LEFT:
			break; //TODO
		case Keyboard.KEY_RIGHT:
			break; //TODO
		default:
			this.text += typedChar;
		}
	}
	
	@Override
	public int getX() {
		return this.x;
	}

	@Override
	public int getY() {
		return this.y;
	}

	@Override
	public int getZ() {
		return this.z;
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return this.height;
	}

}
