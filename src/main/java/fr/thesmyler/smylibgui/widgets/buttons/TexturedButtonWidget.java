package fr.thesmyler.smylibgui.widgets.buttons;

import javax.annotation.Nullable;

import fr.thesmyler.smylibgui.SmyLibGui;
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

	public TexturedButtonWidget(int x, int y, int z, int width, int height, int u, int v, int hoverU, int hoverV, int disabledU, int disabledV, ResourceLocation texture, @Nullable Runnable onClick, @Nullable Runnable onDoubleClick) {
		super(x, y, z, width, height, onClick, onDoubleClick);
		this.u = u;
		this.v = v;
		this.hoverU = hoverU;
		this.hoverV = hoverV;
		this.disabledU = disabledU;
		this.disabledV = disabledV;
		this.texture = texture;
	}
	
	public TexturedButtonWidget(int x, int y, int z, int width, int height, int u, int v, int hoverU, int hoverV, int disabledU, int disabledV, ResourceLocation texture, Runnable onClick) {
		this(x, y, z, width, height, u, v, hoverU, hoverV, disabledU, disabledV, texture, onClick, onClick);
	}
	
	public TexturedButtonWidget(int x, int y, int z,int width, int height, int u, int v, int hoverU, int hoverV, int disabledU, int disabledV, ResourceLocation texture) {
		this(x, y, z, width, height, u, v, hoverU, hoverV, disabledU, disabledV, texture, null);
		this.disable();
	}
	
	public TexturedButtonWidget(int x, int y, int z, int width, int height, int u, int v, ResourceLocation texture, @Nullable Runnable onClick, @Nullable Runnable onDoubleClick) {
		this(x, y, z, width, height, u, v, u, v+height, u, v+height*2, texture, onClick, onDoubleClick);
	}
	
	public TexturedButtonWidget(int x, int y, int z, int width, int height, int u, int v, ResourceLocation texture, @Nullable Runnable onClick) {
		this(x, y, z, width, height, u, v, u, v+height, u, v+height*2, texture, onClick);
	}
	
	public TexturedButtonWidget(int x, int y, int z, int width, int height, int u, int v, ResourceLocation texture) {
		this(x, y, z, width, height, u, v, u, v+height, u, v+height*2, texture);
	}
	
	public TexturedButtonWidget(int x, int y, int z, IncludedTexturedButtons properties, @Nullable Runnable onClick, @Nullable Runnable onDoubleClick) {
		this(x, y, z, properties.width, properties.height, properties.u, properties.v, properties.hoverU, properties.hoverV, properties.disabledU, properties.disabledV, properties.texture, onClick, onDoubleClick);
	}
	
	public TexturedButtonWidget(int x, int y, int z, IncludedTexturedButtons properties, @Nullable Runnable onClick) {
		this(x, y, z, properties, onClick, onClick);
	}
	
	public TexturedButtonWidget(int x, int y, int z, IncludedTexturedButtons properties) {
		this(x, y, z, properties, null);
		this.disable();
	}
	
	public TexturedButtonWidget(int z, int width, int height, int u, int v, int hoverU, int hoverV, int disabledU, int disabledV, ResourceLocation texture, @Nullable Runnable onClick, @Nullable Runnable onDoubleClick) {
		this(0, 0, z, width, height, u, v, hoverU, hoverV, disabledU, disabledV, texture, onClick, onDoubleClick);
	}
	
	public TexturedButtonWidget(int z, int width, int height, int u, int v, int hoverU, int hoverV, int disabledU, int disabledV, ResourceLocation texture, Runnable onClick) {
		this(z, width, height, u, v, hoverU, hoverV, disabledU, disabledV, texture, onClick, onClick);
	}
	
	public TexturedButtonWidget(int z,int width, int height, int u, int v, int hoverU, int hoverV, int disabledU, int disabledV, ResourceLocation texture) {
		this(z, width, height, u, v, hoverU, hoverV, disabledU, disabledV, texture, null);
		this.disable();
	}
	
	public TexturedButtonWidget(int z, int width, int height, int u, int v, ResourceLocation texture, @Nullable Runnable onClick, @Nullable Runnable onDoubleClick) {
		this(z, width, height, u, v, u, v+height, u, v+height*2, texture, onClick, onDoubleClick);
	}
	
	public TexturedButtonWidget(int z, int width, int height, int u, int v, ResourceLocation texture, @Nullable Runnable onClick) {
		this(z, width, height, u, v, u, v+height, u, v+height*2, texture, onClick);
	}
	
	public TexturedButtonWidget(int z, int width, int height, int u, int v, ResourceLocation texture) {
		this(z, width, height, u, v, u, v+height, u, v+height*2, texture);
	}
	
	public TexturedButtonWidget(int z, IncludedTexturedButtons properties, @Nullable Runnable onClick, @Nullable Runnable onDoubleClick) {
		this(z, properties.width, properties.height, properties.u, properties.v, properties.hoverU, properties.hoverV, properties.disabledU, properties.disabledV, properties.texture, onClick, onDoubleClick);
	}
	
	public TexturedButtonWidget(int z, IncludedTexturedButtons properties, @Nullable Runnable onClick) {
		this(z, properties, onClick, onClick);
	}
	
	public TexturedButtonWidget(int z, IncludedTexturedButtons properties) {
		this(z, properties, null);
		this.disable();
	}

	@Override
	public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean hasFocus, Screen parent) {
		Minecraft mc = Minecraft.getMinecraft();
		mc.getTextureManager().bindTexture(this.texture);
		GlStateManager.color(255, 255, 255, 255);
		int u = this.u;
		int v = this.v;
		if(!this.isEnabled()) {
			u = this.disabledU;
			v = this.disabledV;
		} else if(hovered || hasFocus) {
			u = this.hoverU;
			v = this.hoverV;
		}
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		parent.drawTexturedModalRect(x, y, u, v, this.getWidth(), this.getHeight());
	}
	
	public static enum IncludedTexturedButtons {
		
		// 15x15
		BLANK_15(15, 15, 60, 0, 60, 15, 60, 30, SmyLibGui.WIDGET_TEXTURES),
		PLUS(15, 15, 75, 0, 75, 15, 75, 30, SmyLibGui.WIDGET_TEXTURES),
		MINUS(15, 15, 90, 0, 90, 15, 90, 30, SmyLibGui.WIDGET_TEXTURES),
		LEFT(15, 15, 105, 0, 105, 15, 105, 30, SmyLibGui.WIDGET_TEXTURES),
		RIGHT(15, 15, 120, 0, 120, 15, 120, 30, SmyLibGui.WIDGET_TEXTURES),
		UP(15, 15, 135, 0, 135, 15, 135, 30, SmyLibGui.WIDGET_TEXTURES),
		DOWN(15, 15, 150, 0, 150, 15, 150, 30, SmyLibGui.WIDGET_TEXTURES),
		CROSS(15, 15, 165, 0, 165, 15, 165, 30, SmyLibGui.WIDGET_TEXTURES),
		CENTER(15, 15, 180, 0, 180, 15, 180, 30, SmyLibGui.WIDGET_TEXTURES),
		PAPER(15, 15, 195, 0, 195, 15, 195, 30, SmyLibGui.WIDGET_TEXTURES),
		WRENCH(15, 15, 210, 0, 210, 15, 210, 30, SmyLibGui.WIDGET_TEXTURES),
		OPTIONS1_15(15, 15, 225, 0, 225, 15, 225, 30, SmyLibGui.WIDGET_TEXTURES),
		OPTIONS2_15(15, 15, 240, 0, 240, 15, 240, 30, SmyLibGui.WIDGET_TEXTURES),
		
		// 20x20
		BLANK_20(20, 20, 60, 164, 60, 184, 60, 204, SmyLibGui.WIDGET_TEXTURES),
		OPTIONS_20(20, 20, 80, 164, 80, 184, 80, 204, SmyLibGui.WIDGET_TEXTURES),

		// 21x21
		BLANK_21(21, 21, 60, 45, 60, 66, 60, 87, SmyLibGui.WIDGET_TEXTURES),
		SEARCH(21, 21, 81, 45, 81, 66, 81, 87, SmyLibGui.WIDGET_TEXTURES);
		
		int width, height, u, v, hoverU, hoverV, disabledU, disabledV;
		ResourceLocation texture;
		
		private IncludedTexturedButtons(
				int width,
				int height,
				int u,
				int v,
				int hoverU,
				int hoverV,
				int disabledU,
				int disabledV,
				ResourceLocation texture) {
			this.width = width;
			this.height = height;
			this.u = u;
			this.v = v;
			this.hoverU = hoverU;
			this.hoverV = hoverV;
			this.disabledU = disabledU;
			this.disabledV = disabledV;
			this.texture = texture;
		}
		
		
		
	}

}
