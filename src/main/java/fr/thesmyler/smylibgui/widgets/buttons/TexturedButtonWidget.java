package fr.thesmyler.smylibgui.widgets.buttons;

import javax.annotation.Nullable;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.screen.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
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
		Gui.drawRect(x, y, x + this.getWidth(), y + this.getHeight(), 0xFFFFFFFF); //TODO REMOVE DEBUG CODE
		parent.drawTexturedModalRect(x, y, u, v, this.getWidth(), this.getHeight());
//		Gui.drawScaledCustomSizeModalRect(x, y, u, v, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight(), 32, 32);
	}
	
	public static enum IncludedTexturedButtons {
		
		BLANK(15, 15, 60, 0, 60, 15, 60, 30, SmyLibGui.WIDGET_TEXTURES),
		PLUS(15, 15, 75, 0, 75, 15, 75, 30, SmyLibGui.WIDGET_TEXTURES),
		MINUS(15, 15, 90, 0, 90, 15, 90, 30, SmyLibGui.WIDGET_TEXTURES),
		LEFT(15, 15, 105, 0, 105, 15, 105, 30, SmyLibGui.WIDGET_TEXTURES),
		RIGHT(15, 15, 120, 0, 120, 15, 120, 30, SmyLibGui.WIDGET_TEXTURES),
		UP(15, 15, 135, 0, 135, 15, 135, 30, SmyLibGui.WIDGET_TEXTURES),
		DOWN(15, 15, 150, 0, 150, 15, 150, 30, SmyLibGui.WIDGET_TEXTURES),
		CROSS(15, 15, 165, 0, 165, 15, 165, 30, SmyLibGui.WIDGET_TEXTURES);
		
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