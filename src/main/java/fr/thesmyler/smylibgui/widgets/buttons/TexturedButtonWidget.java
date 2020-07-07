package fr.thesmyler.smylibgui.widgets.buttons;

import javax.annotation.Nullable;

import fr.thesmyler.smylibgui.screen.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class TexturedButtonWidget extends AbstractButtonWidget {
	
	protected int u;
	protected int v;
	protected int hoverU;
	protected int hoverV;
	protected int disabledU;
	protected int disabledV;
	protected ResourceLocation texture;

	public TexturedButtonWidget(int x, int y, int width, int height, int z, int u, int v, int hoverU, int hoverV, int disabledU, int disabledV, ResourceLocation texture, @Nullable Runnable onClick, @Nullable Runnable onDoubleClick) {
		super(x, y, z, width, height, onClick, onDoubleClick);
		this.u = u;
		this.v = v;
		this.hoverU = hoverU;
		this.hoverV = hoverV;
		this.disabledU = disabledU;
		this.disabledV = disabledV;
		this.texture = texture;
	}
	
	public TexturedButtonWidget(int x, int y, int width, int height, int z, int u, int v, int hoverU, int hoverV, int disabledU, int disabledV, ResourceLocation texture, Runnable onClick) {
		this(x, y, width, height, z, u, v, hoverU, hoverV, disabledU, disabledV, texture, onClick, null);
	}
	
	public TexturedButtonWidget(int x, int y, int width, int height, int z, int u, int v, int hoverU, int hoverV, int disabledU, int disabledV, ResourceLocation texture) {
		this(x, y, width, height, z, u, v, hoverU, hoverV, disabledU, disabledV, texture, null, null);
		this.disable();
	}
	
	public TexturedButtonWidget(int x, int y, int width, int height, int z, int u, int v, ResourceLocation texture, @Nullable Runnable onClick, @Nullable Runnable onDoubleClick) {
		this(x, y, z, width, height, u, v, u, v+height, u, v+height*2, texture, onClick, onDoubleClick);
	}
	
	public TexturedButtonWidget(int x, int y, int width, int height, int z, int u, int v, ResourceLocation texture, @Nullable Runnable onClick) {
		this(x, y, z, width, height, u, v, u, v+height, u, v+height*2, texture, onClick);
	}
	
	public TexturedButtonWidget(int x, int y, int width, int height, int z, int u, int v, ResourceLocation texture) {
		this(x, y, z, width, height, u, v, u, v+height, u, v+height*2, texture);
	}

	@Override
	public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean hasFocus, Screen parent) {
		Minecraft mc = Minecraft.getMinecraft();
		mc.getTextureManager().bindTexture(this.texture);
		GlStateManager.color(255, 255, 255, 255);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		int u = this.u;
		int v = this.v;
		if(!this.isEnabled()) {
			u = this.disabledU;
			v = this.disabledV;
		} else if(hovered || hasFocus) {
			u = this.hoverU;
			v = this.hoverV;
		}
		parent.drawTexturedModalRect(x, y, u, v, this.getWidth(), this.getHeight());
	}


}
