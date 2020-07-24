package fr.thesmyler.smylibgui.widgets.text;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import fr.thesmyler.smylibgui.Animation;
import fr.thesmyler.smylibgui.Animation.AnimationState;
import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.widgets.IWidget;
import net.minecraft.client.Minecraft;

//TODO Selection
//TODO Copy/Cut/Paste
//TODO Support alternate input methods
public class TextFieldWidget implements IWidget {

	private String text;
	private int x, y, width, height, z;
	private int cursorIndex, deltaIndex;
	private Animation cursorAnimation = new Animation(750);

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
		this.cursorAnimation.start(AnimationState.FLASH);
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
		String textToDraw = this.getDisplayedText(parent.getFont());
		parent.getFont().drawString(textToDraw, x + 5, y + 5, textColor);
		if(focused) {
			this.cursorAnimation.update();
			int lengthBeforeCursor = parent.getFont().getStringWidth(this.text.substring(this.deltaIndex, this.cursorIndex));
			int cursorX = x + lengthBeforeCursor + 4;
			Screen.drawRect(cursorX, y+5, cursorX+1, y+5 + parent.getFont().FONT_HEIGHT, this.cursorAnimation.fadeColor(textColor));
		}
	}

	@Override
	public boolean onClick(int mouseX, int mouseY, int mouseButton, Screen parent) {
		String displayedString = parent.getFont().trimStringToWidth(this.text.substring(this.deltaIndex), this.width);
		int start = 0;
		int end = displayedString.length() + 1;
		while(end - start > 1) {
			int middle = (end + start) / 2;
			int length = parent.getFont().getStringWidth(displayedString.substring(0, middle));
			if(length >= mouseX) {
				end = middle;
			} else {
				start = middle;
			}
		}
		this.cursorIndex = start + this.deltaIndex;
		return false;
	}

	@Override
	public boolean onDoubleClick(int mouseX, int mouseY, int mouseButton, @Nullable Screen parent) {
		return false;
	}

	@Override
	public void onKeyTyped(char typedChar, int keyCode, @Nullable Screen parent) {
		boolean ignore = false;

		switch(keyCode) {
		case Keyboard.KEY_BACK:
			this.removeText(this.cursorIndex-1, this.cursorIndex);
			this.moveCursor(-1, parent.getFont());
			break;
		case Keyboard.KEY_DELETE:
			this.removeText(this.cursorIndex, this.cursorIndex+1);
			break;
		case Keyboard.KEY_LEFT:
			if(Screen.isCtrlKeyDown()) {
				//TODO control move in text field
			} else {
				this.moveCursor(-1, parent.getFont());
			}
			break;
		case Keyboard.KEY_RIGHT:
			if(Screen.isCtrlKeyDown()) {
				//TODO control move in text field
			} else {
				this.moveCursor(1, parent.getFont());
			}
			break;
		default:
			if(!Character.isISOControl(typedChar)) {
				this.text = this.text.substring(0, this.cursorIndex) + typedChar + this.text.substring(this.cursorIndex);
				this.moveCursor(1, parent.getFont());
			} else {
				ignore = true;
			}
			break;
		}

		if(!ignore) {
			this.cursorAnimation.start(AnimationState.FLASH);
		}
	}

	/**
	 * Moves the cursor relative to it's current position
	 * 
	 * @param amount
	 * @param font the font to use to update the delta relative to the new cursor position
	 */
	private void moveCursor(int amount, @Nullable FontRendererContainer font) {
		int newPos = this.cursorIndex + amount;
		newPos = Math.max(newPos, 0);
		newPos = Math.min(newPos, this.text.length());
		this.cursorIndex = newPos;
		if(font != null) {
			String t = this.getDisplayedText(font);
			if(this.cursorIndex < this.deltaIndex) {
				this.deltaIndex = Math.max(0, this.cursorIndex - 5);
			} else if(this.cursorIndex > this.deltaIndex + t.length()) {
				this.deltaIndex = Math.min(this.text.length() - 1, this.cursorIndex - t.length() + 5);
			}
		}
	}

	private void removeText(int startIndex, int endIndex) {
		if(startIndex >= 0 && endIndex <= this.text.length()) {
			this.text = this.text.substring(0, startIndex) + this.text.substring(endIndex, this.text.length());
		}
	}

	private String getDisplayedText(FontRendererContainer font) {
		return font.trimStringToWidth(this.text.substring(this.deltaIndex), this.getWidth() - 10);
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
