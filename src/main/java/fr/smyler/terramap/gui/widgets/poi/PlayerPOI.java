package fr.smyler.terramap.gui.widgets.poi;

import fr.smyler.terramap.network.TerramapPlayer;
import io.github.terra121.projection.GeographicProjection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

//TODO Smaller head size option
public class PlayerPOI extends PointOfInterest {

	private TerramapPlayer player;
	
	public PlayerPOI(TerramapPlayer player) {
		this.player = player;
		this.texture = this.player.getSkin();
	}

	@Override
	public void draw(int x, int y, boolean hovered) {
		GlStateManager.enableAlpha();
		if(hovered) Gui.drawRect(x - 7, y - 7, x + 9, y + 9, 0x50000000);
		Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
		GlStateManager.color(255, 255, 255, 255);
		Gui.drawModalRectWithCustomSizedTexture(x - 8, y - 8, 16, 16, 16, 16, 128, 128);
		Gui.drawModalRectWithCustomSizedTexture(x - 8, y - 8, 80, 16, 16, 16, 128, 128);
	}
	
	@Override
	public int getWidth() {
		return 16;
	}

	@Override
	public int getHeight() {
		return 16;
	}

	@Override
	public int getXOffset() {
		return -8;
	}

	@Override
	public int getYOffset() {
		return -8;
	}

	@Override
	public String getDisplayName() {
		return player.getDisplayName();
	}
	
	public TerramapPlayer getPlayer() {
		return this.player;
	}

	public void updatePosition(GeographicProjection projection) {
		double x = this.player.getPosX();
		double z = this.player.getPosZ();
		double coords[] = projection.toGeo(x, z);
		this.longitude = coords[0];
		this.latitude = coords[1];
	}

}
