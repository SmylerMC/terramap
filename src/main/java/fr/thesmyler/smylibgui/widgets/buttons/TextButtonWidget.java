package fr.thesmyler.smylibgui.widgets.buttons;

import fr.thesmyler.smylibgui.RenderUtil;
import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.screen.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class TextButtonWidget extends AbstractButtonWidget {

	protected String str;

	public TextButtonWidget(int x, int y, int z, int width, String str, Runnable onClick, Runnable onDoubleClick) {
		super(x, y, z, width, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 11, onClick, onDoubleClick);
		this.str = str;
	}

	public TextButtonWidget(int x, int y, int z, int width, String str, Runnable onClick) {
		this(x, y, z, width, str, onClick, null);
	}

	public TextButtonWidget(int x, int y, int z, int width, String str) {
		this(x, y, z, width, str, null, null);
		this.enabled = false;
	}
	
	public TextButtonWidget(int z, String str, Runnable onClick, Runnable onDoubleClick) {
		this(0, 0, z, Minecraft.getMinecraft().fontRenderer.getStringWidth(str) + 20, str, onClick, onDoubleClick);
	}

	public TextButtonWidget(int z, String str, Runnable onClick) {
		this(z, str, onClick, null);
	}

	public TextButtonWidget(int z, String str) {
		this(z, str, null, null);
		this.enabled = false;
	}

	@Override
	public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean hasFocus, Screen parent) {
		Minecraft mc = Minecraft.getMinecraft();
		mc.getTextureManager().bindTexture(SmyLibGui.BUTTON_TEXTURES);
		GlStateManager.color(1, 1, 1, 1); //White, non transparent
		int textureDelta = 1;
		int textColor = 0xFFE0E0E0;
		if (!this.isEnabled()) {
			textColor = 0xFFA0A0A0;
			textureDelta = 0;
		}
		else if (hovered || hasFocus) {
			textColor = 0xFFFFFFA0;
			textureDelta = 2;
		}
		int leftWidth = this.width / 2;
		int rightWidth = leftWidth;
		leftWidth += this.width % 2;
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderUtil.drawTexturedModalRect(x, y, 0, 0, 46 + textureDelta * 20, leftWidth, 20);
		RenderUtil.drawTexturedModalRect(x + leftWidth, y, 0, 200 - rightWidth, 46 + textureDelta * 20, rightWidth, 20);
		parent.getFont().drawCenteredString(x + this.width / 2, y + (this.height - 8) / 2, this.getText(), textColor, true);

	}

	public String getText() {
		return str;
	}

	public void setText(String str) {
		this.str = str;
	}
	
	public TextButtonWidget setWidth(int width) {
		this.width = width;
		return this;
	}
}
