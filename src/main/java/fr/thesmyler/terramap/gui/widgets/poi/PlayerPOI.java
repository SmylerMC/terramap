package fr.thesmyler.terramap.gui.widgets.poi;

import fr.thesmyler.terramap.TerramapUtils;
import fr.thesmyler.terramap.network.mapsync.TerramapPlayer;
import io.github.terra121.projection.GeographicProjection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

@Deprecated
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
		if(this.player.isSpectator()) GlStateManager.color(1, 1, 1, 0.6f);
		else GlStateManager.color(1, 1, 1, 1);
		Gui.drawModalRectWithCustomSizedTexture(x - 8, y - 8, 16, 16, 16, 16, 128, 128);
		Gui.drawModalRectWithCustomSizedTexture(x - 8, y - 8, 80, 16, 16, 16, 128, 128);
		GlStateManager.color(1, 1, 1, 1);
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
		return player.getDisplayName().getFormattedText();
	}
	
	public TerramapPlayer getPlayer() {
		return this.player;
	}

	public void updatePosition(GeographicProjection projection) {
//		double x = this.player.getLongitude();
//		double z = this.player.getLatitude();
//		double coords[] = TerramapUtils.toGeo(projection, x, z);
//		this.longitude = coords[0];
//		this.latitude = coords[1];
	}

}
