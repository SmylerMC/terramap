package fr.thesmyler.smylibgui.widgets.text;

import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import fr.thesmyler.smylibgui.Animation;
import fr.thesmyler.smylibgui.Animation.AnimationState;
import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.smylibgui.widgets.MenuWidget;
import fr.thesmyler.terramap.TerramapMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

/**
 * 
 * Heavily inspired by the 1.15 vanilla class
 * We can't support all input methods as it would require GLFW and it is not present in LWJGL 2
 * 
 * @author SmylerMC
 *
 */
public class TextFieldWidget implements IWidget {

	private String text;
	private int x, y, width, height, z;
	private int selectionStart, selectionEnd, firstCharacterIndex, maxLength;
	private int enabledTextColor, disabledTextColor;
	private boolean hasBackground, selecting;
	private boolean enabled, visible, menuEnabled;
	private Animation cursorAnimation = new Animation(600);
	private FontRendererContainer font;
	private Predicate<String> textValidator, onPressEnterCallback;
	private Consumer<String> onChangeCallback;
	private MenuWidget rightClickMenu;
	private boolean isSearchBar;

	public TextFieldWidget(int x, int y, int z, int width, String defaultText,
			Consumer<String> onChange, Predicate<String> onPressEnter, Predicate<String> textValidator,
			int maxTextLength,
			int enabledTextColor, int disabledTextColor,
			FontRendererContainer font) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.font = font;
		this.height = font.FONT_HEIGHT + 10;
		this.text = defaultText;
		this.maxLength = maxTextLength;
		this.textValidator = textValidator;
		this.onChangeCallback = onChange;
		this.onPressEnterCallback = onPressEnter;
		this.enabledTextColor = enabledTextColor;
		this.disabledTextColor = disabledTextColor;
		this.hasBackground = true;
		this.cursorAnimation.start(AnimationState.FLASH);
		this.enabled = true;
		this.visible = true;
		this.menuEnabled = true;
		this.rightClickMenu = new MenuWidget(5000, this.font);
		this.rightClickMenu.addEntry("Copy", () -> {this.copySelectionToClipboard();});
		this.rightClickMenu.addEntry("Cut", () -> {this.cutSelectionToClipboard();});
		this.rightClickMenu.addEntry("Paste", () -> {this.pasteIn();});
		this.rightClickMenu.addSeparator();
		this.rightClickMenu.addEntry("Select all", () -> {this.selectAll();});
		this.setCursorToEnd();
	}

	public TextFieldWidget(int x, int y, int z, int width, FontRendererContainer font) {
		this(x, y, z, width, "", str -> {}, (str) -> false, (str) -> true, Integer.MAX_VALUE, 0xFFE0E0E0, 0xFF707070, font);
	}

	public TextFieldWidget(int z, String defaultText, FontRendererContainer font) {
		this(0, 0, z, 50, font);
	}

	public TextFieldWidget(int z, FontRendererContainer font) {
		this(z, "", font);
	}

	@Override
	public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean focused, Screen parent) {

		this.cursorAnimation.update();

		int backgroundColor = 0x80000000;
		int borderColor = 0xFFA0A0A0;
		int textColor = this.disabledTextColor;

		if(this.isEnabled()) {
			if(hovered) borderColor = 0xFFE0E0E0;
			if(focused) textColor = this.enabledTextColor; else textColor = 0xFFB0B0B0;
		}

		if(this.hasBackground) {
			Gui.drawRect(x, y, x + this.width, y + this.height, backgroundColor);
			Gui.drawRect(x - 1, y - 1, x + this.width + 1, y, borderColor);
			Gui.drawRect(x - 1, y + this.height, x + this.width + 1, y + this.height + 1, borderColor);
			Gui.drawRect(x - 1, y - 1, x, y + this.height + 1, borderColor);
			Gui.drawRect(x + this.width, y - 1, x + this.width + 1, y + this.height + 1, borderColor);
		}
		
		if(this.isSearchBar) {
			GlStateManager.color(1, 1, 1, 1);
			Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(TerramapMod.MODID, "textures/gui/mapwidgets.png"));
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GuiScreen.drawModalRectWithCustomSizedTexture(x + this.width - 17, y + 2, 131, 0, 15, 15, 256, 256);
		}


		int cursorColor = this.cursorAnimation.fadeColor(textColor);
		int displaySelectionStart = this.selectionStart - this.firstCharacterIndex;
		int displaySelectionEnd = this.selectionEnd - this.firstCharacterIndex;
		String string = this.getVisibleText();
		boolean displayCursor = displaySelectionStart >= 0 && displaySelectionStart <= string.length();
		int textRenderX = this.hasBackground ? x + 4 : x;
		int textRenderY = this.hasBackground ? y + (this.height - 8) / 2 : y;
		int startDrawAfterCursorX = textRenderX;
		displaySelectionEnd = Math.min(displaySelectionEnd, string.length());

		if(!string.isEmpty()) {
			String textBeforeCursor = displayCursor ? string.substring(0, displaySelectionStart) : string;
			startDrawAfterCursorX = this.font.drawStringWithShadow(textBeforeCursor, textRenderX, textRenderY, textColor);
		}

		boolean isCursorAtEndOfText = this.selectionStart < this.text.length() || this.text.length() >= this.getMaxTextLength();
		int cursorX = startDrawAfterCursorX;
		if(!displayCursor) {
			cursorX = displaySelectionStart > 0 ? textRenderX + this.getEffectiveWidth() : textRenderX;
		} else if(isCursorAtEndOfText) {
			cursorX = --startDrawAfterCursorX;
		}

		if(!string.isEmpty() && displayCursor && displaySelectionStart < string.length()) {
			this.font.drawStringWithShadow(string.substring(displaySelectionStart), startDrawAfterCursorX, textRenderY, textColor);
		}

		if(focused && this.isEnabled()) {
			if (isCursorAtEndOfText) {
				GuiScreen.drawRect(cursorX, textRenderY - 1, cursorX+1, textRenderY+1 + 9, cursorColor);
			} else {
				this.font.drawStringWithShadow("_", cursorX, textRenderY, cursorColor);
			}
		}


		if (displaySelectionEnd != displaySelectionStart) {
			int selectionBoxRenderRight = textRenderX + this.font.getStringWidth(string.substring(0, displaySelectionEnd));
			this.drawSelectionHighlight(x, y, cursorX, textRenderY - 1, selectionBoxRenderRight - 1, textRenderY + 1 + 9);
		}


	}

	private void drawSelectionHighlight(int x, int y, int x1, int y1, int x2, int y2) {
		int dispX1 = Math.max(x1, x2);
		int dispY1 = Math.max(y1, y2);
		int dispX2 = Math.min(x1, x2);
		int dispY2 = Math.min(y1, y2);
		dispX2 = Math.min(dispX2, x + this.getEffectiveWidth());
		dispX1 = Math.min(dispX1, x + this.getEffectiveWidth());
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		GlStateManager.color(0, 0, 1, 1);
		GlStateManager.disableTexture2D();
		GlStateManager.enableColorLogic();
		GlStateManager.colorLogicOp(GlStateManager.LogicOp.OR_REVERSE);
		bufferBuilder.begin(7, DefaultVertexFormats.POSITION);
		bufferBuilder.pos(dispX1, dispY2, 0.0D).endVertex();
		bufferBuilder.pos(dispX2, dispY2, 0.0D).endVertex();
		bufferBuilder.pos(dispX2, dispY1, 0.0D).endVertex();
		bufferBuilder.pos(dispX1, dispY1, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.disableColorLogic();
		GlStateManager.enableTexture2D();
	}

	private int getEffectiveWidth() {
		return this.hasBackground? this.getWidth() - 8: this.getWidth();
	}

	@Override
	public boolean onClick(int mouseX, int mouseY, int mouseButton, Screen parent) {
		if(!this.isEnabled()) return false;
		if (mouseButton == 0) {
			int mPos = mouseX;
			if (this.hasBackground) mPos -= 4;
			String string = this.getVisibleText();
			this.setCursor(this.font.trimStringToWidth(string, mPos).length() + this.firstCharacterIndex);
		} else if(mouseButton == 1 && this.menuEnabled) {
			parent.showMenu(mouseX + this.x, mouseY + this.y, this.rightClickMenu);
		}
		return false;
	}

	@Override
	public boolean onDoubleClick(int mouseX, int mouseY, int mouseButton, @Nullable Screen parent) {
		if(!this.isEnabled()) return false;
		if(mouseButton == 0) {
			this.setSelectionStart(this.getWordSkipPosition(-1, this.getCursor(), false));
			this.setSelectionEnd(this.getWordSkipPosition(1, this.getCursor(), false));
		}
		return false;
	}

	@Override
	public void onKeyTyped(char typedChar, int keyCode, @Nullable Screen parent) {
		if(!this.isEnabled()) return;
		this.selecting = GuiScreen.isShiftKeyDown();
		if (Screen.isKeyComboCtrlA(keyCode)) {
			this.selectAll();
		} else if (Screen.isKeyComboCtrlC(keyCode)) {
			this.copySelectionToClipboard();
		} else if (Screen.isKeyComboCtrlV(keyCode)) {
			this.pasteIn();
		} else if (Screen.isKeyComboCtrlX(keyCode)) {
			this.cutSelectionToClipboard();
		} else {
			switch(keyCode) {
			case Keyboard.KEY_BACK:
				this.selecting = false;
				this.erase(-1);
				this.selecting = GuiScreen.isShiftKeyDown();
				break;
			case Keyboard.KEY_DELETE:
				this.selecting = false;
				this.erase(1);
				this.selecting = GuiScreen.isShiftKeyDown();
				break;
			case Keyboard.KEY_RIGHT:
				if (GuiScreen.isCtrlKeyDown()) {
					this.setCursor(this.getWordSkipPosition(1));
				} else {
					this.moveCursor(1);
				}
				break;
			case Keyboard.KEY_LEFT:
				if (Screen.isCtrlKeyDown()) {
					this.setCursor(this.getWordSkipPosition(-1));
				} else {
					this.moveCursor(-1);
				}
				break;
			case Keyboard.KEY_HOME: //This is the start key
				this.setCursorToStart();
				break;
			case Keyboard.KEY_END:
				this.setCursorToEnd();
				break;
			case Keyboard.KEY_RETURN: //This is the enter key
			case Keyboard.KEY_NUMPADENTER:
				if(this.onPressEnterCallback.test(this.text)) {
					parent.setFocus(null);
				}
				break;
			default:
				if (TextFieldWidget.isValidChar(typedChar))
					this.write(Character.toString(typedChar));
				break;
			}
		}
	}

	@Override
	public void onMouseDragged(int mouseX, int mouseY, int dX, int dY, int mouseButton, @Nullable Screen parent) {
		if(!this.isEnabled()) return;
		if (mouseButton == 0) {
			int mPos = mouseX;
			if (this.hasBackground) mPos -= 4;
			String string = this.getVisibleText();
			this.setSelectionEnd(this.font.trimStringToWidth(string, mPos).length() + this.firstCharacterIndex);
		}
	}

	public void moveCursor(int offset) {
		this.setCursor(this.selectionStart + offset);
	}

	public void setCursorToStart() {
		this.setCursor(0);
	}

	public void write(String text) {

		String newText = "";
		String typedText = TextFieldWidget.stripInvalidChars(text);

		int start = Math.min(this.selectionStart, this.selectionEnd);
		int end = Math.max(this.selectionStart, this.selectionEnd);
		int availableTextSpace = this.maxLength - this.text.length() - (start - end);

		if (!this.text.isEmpty()) newText = this.text.substring(0, start);

		int endOfText;
		if (availableTextSpace < typedText.length()) {
			newText = newText + typedText.substring(0, availableTextSpace);
			endOfText = availableTextSpace;
		} else {
			newText = newText + typedText;
			endOfText = typedText.length();
		}

		if (!this.text.isEmpty() && end < this.text.length()) {
			newText = newText + this.text.substring(end);
		}

		if (this.textValidator.test(newText)) {
			this.text = newText;
			this.setSelectionStart(start + endOfText);
			this.setSelectionEnd(this.selectionStart);
			this.onChange();
		}
	}

	private void erase(int count) {
		if (GuiScreen.isCtrlKeyDown()) this.eraseWords(count);
		else this.eraseCharacters(count);
	}

	public void eraseWords(int wordCount) {
		if (!this.text.isEmpty()) {
			if (this.selectionEnd != this.selectionStart) {
				this.write("");
			} else {
				this.eraseCharacters(this.getWordSkipPosition(wordCount) - this.selectionStart);
			}
		}
	}

	public void eraseCharacters(int charCount) {
		if (!this.text.isEmpty()) {
			if (this.selectionEnd != this.selectionStart) {
				this.write("");
			} else {
				boolean backward = charCount < 0;
				int start = backward ? this.selectionStart + charCount : this.selectionStart;
				int end = backward ? this.selectionStart : this.selectionStart + charCount;
				String string = "";
				if (start >= 0) {
					string = this.text.substring(0, start);
				}

				if (end < this.text.length()) {
					string = string + this.text.substring(end);
				}

				if (this.textValidator.test(string)) {
					this.text = string;
					if (backward) this.moveCursor(charCount);
					this.onChange();
				}
			}
		}
	}

	public int getWordSkipPosition(int wordCount) {
		return this.getWordSkipPosition(wordCount, this.getCursor());
	}

	private int getWordSkipPosition(int wordCount, int startFromPos) {
		return this.getWordSkipPosition(wordCount, startFromPos, true);
	}

	private String getVisibleText() {
		return this.font.trimStringToWidth(this.text.substring(this.firstCharacterIndex), this.getEffectiveWidth());
	}

	private int getWordSkipPosition(int wordCount, int startFromPos, boolean includeSpaces) {
		int pos = startFromPos;
		for(int i = 0; i < Math.abs(wordCount); ++i) {
			if (wordCount > 0) {
				int textLength = this.text.length();
				pos = this.text.indexOf(' ', pos);
				if (pos == -1) pos = textLength;
				else if(includeSpaces) {
					while(pos < textLength && this.text.charAt(pos) == ' ') {
						++pos;
					}
				}
			} else {
				while(includeSpaces && pos > 0 && this.text.charAt(pos - 1) == ' ') --pos;
				while(pos > 0 && this.text.charAt(pos - 1) != ' ') --pos;
			}
		}

		return pos;
	}

	public int getCursor() {
		return this.selectionStart;
	}

	public void setCursorToEnd() {
		this.setCursor(this.text.length());
	}

	public void setCursor(int cursor) {
		this.setSelectionStart(cursor);
		if (!this.selecting) this.setSelectionEnd(this.selectionStart);
		this.onChange();
	}

	public void setSelectionStart(int cursor) {
		this.selectionStart = MathHelper.clamp(cursor, 0, this.text.length());
	}

	public void setSelectionEnd(int pos) {
		int txtLength = this.text.length();
		this.selectionEnd = MathHelper.clamp(pos, 0, txtLength);
		this.firstCharacterIndex = Math.min(this.firstCharacterIndex, txtLength);

		int effectiveWidth = this.getEffectiveWidth();
		String displayedText = this.font.trimStringToWidth(this.text.substring(this.firstCharacterIndex), effectiveWidth);
		int displayEndPos = displayedText.length() + this.firstCharacterIndex;
		if (this.selectionEnd == this.firstCharacterIndex) {
			this.firstCharacterIndex -= this.font.trimStringToWidth(this.text, effectiveWidth, true).length();
		}

		if (this.selectionEnd > displayEndPos) {
			this.firstCharacterIndex += this.selectionEnd - displayEndPos;
		} else if (this.selectionEnd <= this.firstCharacterIndex) {
			this.firstCharacterIndex -= this.firstCharacterIndex - this.selectionEnd;
		}

		this.firstCharacterIndex = MathHelper.clamp(this.firstCharacterIndex, 0, txtLength);
	}

	private void onChange() {
		this.onChangeCallback.accept(this.text);
	}

	public String getSelectedText() {
		int startIndex = Math.min(this.selectionStart, this.selectionEnd);
		int endIndex = Math.max(this.selectionStart, this.selectionEnd);
		return this.text.substring(startIndex, endIndex);
	}

	public void selectAll() {
		this.setCursorToEnd();
		this.setSelectionEnd(0);
	}

	public void copySelectionToClipboard() {
		GuiScreen.setClipboardString(this.getSelectedText());
	}

	public void cutSelectionToClipboard() {
		this.copySelectionToClipboard();
		this.write("");
	}

	public void pasteIn() {
		this.write(GuiScreen.getClipboardString());
	}

	public int getMaxTextLength() {
		return this.maxLength;
	}

	public boolean hasBackground() {
		return this.hasBackground;
	}

	public void setHasBackground(boolean yesNo) {
		this.hasBackground = yesNo;
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

	public String getText() {
		return text;
	}

	public TextFieldWidget setText(String text) {
		this.text = text;
		this.setCursorToEnd();
		return this;
	}

	public TextFieldWidget setX(int x) {
		this.x = x;
		return this;
	}

	public TextFieldWidget setY(int y) {
		this.y = y;
		return this;
	}

	public TextFieldWidget setWidth(int width) {
		this.width = width;
		return this;
	}

	public TextFieldWidget setMaxTextLength(int maxLength) {
		this.maxLength = maxLength;
		return this;
	}

	public int getEnabledTextColor() {
		return enabledTextColor;
	}

	public TextFieldWidget setEnabledTextColor(int enabledTextColor) {
		this.enabledTextColor = enabledTextColor;
		return this;
	}
	
	public boolean isSearchBar() {
		return this.isSearchBar;
	}
	
	public TextFieldWidget setIsSearchBar(boolean yesNo) {
		this.isSearchBar = yesNo;
		return this;
	}

	public int getDisabledTextColor() {
		return disabledTextColor;
	}

	public TextFieldWidget setDisabledTextColor(int disabledTextColor) {
		this.disabledTextColor = disabledTextColor;
		return this;
	}

	public Predicate<String> getTextValidator() {
		return textValidator;
	}

	public TextFieldWidget setTextValidator(Predicate<String> textValidator) {
		this.textValidator = textValidator;
		return this;
	}

	public Consumer<String> getOnChangeCallback() {
		return onChangeCallback;
	}

	public TextFieldWidget setOnChangeCallback(Consumer<String> onChangeCallback) {
		this.onChangeCallback = onChangeCallback;
		return this;
	}

	public Predicate<String> getOnPressEnterCallback() {
		return onPressEnterCallback;
	}

	public TextFieldWidget setOnPressEnterCallback(Predicate<String> onPressEnterCallback) {
		this.onPressEnterCallback = onPressEnterCallback;
		return this;
	}

	public boolean isHasBackground() {
		return hasBackground;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public TextFieldWidget setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}
	
	public TextFieldWidget enable() {
		return this.setEnabled(true);
	}
	
	public TextFieldWidget disable() {
		return this.setEnabled(false);
	}

	@Override
	public boolean isVisible(Screen parent) {
		return this.visible;
	}

	public TextFieldWidget setVisibility(boolean visible) {
		this.visible = visible;
		return this;
	}
	
	public TextFieldWidget show() {
		return this.setVisibility(true);
	}
	
	public TextFieldWidget hide() {
		return this.setVisibility(false);
	}
	
	public boolean isRightClickMenuEnabled() {
		return this.menuEnabled;
	}
	
	public TextFieldWidget setRightClickMenuEnabled(boolean yesNo) {
		this.menuEnabled = yesNo;
		return this;
	}
	
	public TextFieldWidget enableRightClickMenu() {
		return this.setRightClickMenuEnabled(true);
	}
	
	public TextFieldWidget disableRightClickMenu() {
		return this.setRightClickMenuEnabled(false);
	}
	
	public static boolean isValidChar(char chr) {
		return chr != '\u00a7' && chr >= ' ' && chr != 127;
	}

	public static String stripInvalidChars(String str) {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < str.length(); ++i) {
			char c = str.charAt(i);
			if (isValidChar(c)) builder.append(c);
		}
		return builder.toString();
	}

}
