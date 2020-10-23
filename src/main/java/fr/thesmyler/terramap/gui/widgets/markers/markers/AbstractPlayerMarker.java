package fr.thesmyler.terramap.gui.widgets.markers.markers;

import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import fr.thesmyler.terramap.network.playersync.TerramapPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public abstract class AbstractPlayerMarker extends AbstractMovingMarkers {
	
	private int downScaleFactor;

	public AbstractPlayerMarker(MarkerController<?> controller, TerramapPlayer player, int downscaleFactor) {
		super(controller, 16 / downscaleFactor, 16 / downscaleFactor);
		this.downScaleFactor = downscaleFactor;
	}

	@Override
	public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean focused, Screen parent) {
		boolean drawName = this.showName(hovered);
		boolean isMinimap = false;
		if(parent instanceof MapWidget) {
			MapWidget map = (MapWidget) parent;
			isMinimap = map.getContext().equals(MapContext.MINIMAP);
			drawName = drawName && !isMinimap;
		}
		int textureSize = 128 / this.downScaleFactor;
		GlStateManager.enableAlpha();
		if(hovered) Gui.drawRect(x +1, y +1, x + this.getWidth() + 1, y + this.getHeight() + 1, 0x50000000);
		Minecraft.getMinecraft().getTextureManager().bindTexture(this.getSkin());
		GlStateManager.color(1, 1, 1, this.getTransparency());
		Gui.drawModalRectWithCustomSizedTexture(x, y, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight(), textureSize, textureSize);
		Gui.drawModalRectWithCustomSizedTexture(x, y, 80 / this.downScaleFactor, this.getHeight(), this.getWidth(), this.getHeight(), textureSize, textureSize);

		if(drawName) {
			int halfSize = this.width / 2;
			String name = this.getDisplayName().getFormattedText();
			int strWidth = parent.getFont().getStringWidth(name);
			int nameY = y - parent.getFont().FONT_HEIGHT - 2;
			Gui.drawRect(x + halfSize - strWidth / 2 - 2, y - parent.getFont().FONT_HEIGHT - 4, x + strWidth / 2 + halfSize + 2, y - 1, 0x50000000);
			parent.getFont().drawCenteredString(x + halfSize, nameY, name, 0xFFFFFFFF, true);
		}
		
		GlStateManager.color(1, 1, 1, 1);
	}

	protected abstract ResourceLocation getSkin();

	protected abstract float getTransparency();

	protected abstract boolean showName(boolean hovered);
	
	@Override
	public int getDeltaX() {
		return - this.getWidth() / 2;
	}

	@Override
	public int getDeltaY() {
		return - this.getHeight() / 2;
	}
	
	@Override
	public boolean canBeTracked() {
		return true;
	}

}
