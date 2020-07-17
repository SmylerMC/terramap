package fr.thesmyler.smylibgui.widgets.buttons;

import fr.thesmyler.smylibgui.screen.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class TextButtonWidget extends AbstractButtonWidget {

	//Vanilla texture
	protected static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("textures/gui/widgets.png");

	private String str;

	public TextButtonWidget(int x, int y, int width, String str, int z, Runnable onClick, Runnable onDoubleClick) {
		super(x, y, width, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 11, z, onClick, onDoubleClick);
		this.str = str;
	}

	public TextButtonWidget(int x, int y, int width, String str, int z, Runnable onClick) {
		this(x, y, width, str, z, onClick, null);
	}

	public TextButtonWidget(int x, int y, int width, String str, int z) {
		this(x, y, width, str, z, null, null);
		this.enabled = false;
	}

	@Override
	public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean hasFocus, Screen parent) {

		Minecraft mc = Minecraft.getMinecraft();
		mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
		GlStateManager.color(1, 1, 1, 1); //White, non transparent

		int textureDelta = 1;
		int textColor = 0xFFE0E0E0;
		if (!this.isEnabled()) {
			textureDelta = 0;
			textColor = 0xFFA0A0A0;
		}
		else if (hovered || hasFocus) {
			textureDelta = 2;
			textColor = 0xFFFFFFA0;
		}

		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		parent.drawTexturedModalRect(x, y, 0, 46 + textureDelta * 20, this.width / 2, this.height);
		parent.drawTexturedModalRect(x + this.width / 2, y, 200 - this.width / 2, 46 + textureDelta * 20, this.width / 2, this.height);

		parent.getFont().drawCenteredString(x + this.width / 2, y + (this.height - 8) / 2, this.str, textColor, true);

	}

	public String getText() {
		return str;
	}

	public void setText(String str) {
		this.str = str;
	}
	
	
}
