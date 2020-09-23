package fr.thesmyler.terramap.gui.widgets.markers.markers;

import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.terramap.TerramapServer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import fr.thesmyler.terramap.network.mapsync.TerramapLocalPlayer;
import fr.thesmyler.terramap.network.mapsync.TerramapPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class PlayerMarker extends MovingMapMarkers {

	protected TerramapPlayer player;
	
	public PlayerMarker(MarkerController<?> controller, TerramapPlayer player) {
		super(controller, 16, 16);
		this.player = player;
	}

	@Override
	public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean focused, Screen parent) {
		GlStateManager.enableAlpha();
		if(hovered) Gui.drawRect(x +1, y +1, x + 17, y + 17, 0x50000000);
		Minecraft.getMinecraft().getTextureManager().bindTexture(this.player.getSkin());
		if(this.player.isSpectator()) GlStateManager.color(1, 1, 1, 0.6f);
		else GlStateManager.color(1, 1, 1, 1);
		Gui.drawModalRectWithCustomSizedTexture(x, y, 16, 16, 16, 16, 128, 128);
		Gui.drawModalRectWithCustomSizedTexture(x, y, 80, 16, 16, 16, 128, 128);
		GlStateManager.color(1, 1, 1, 1);
	}
	
	@Override
	public void update(MapWidget map) {
		super.update(map);
		if(
				!TerramapServer.getServer().hasPlayer(this.player.getUUID())
			  || (this.player instanceof TerramapLocalPlayer && ((TerramapLocalPlayer) this.player).getPlayer().isDead)) {
			map.scheduleForNextScreenUpdate(() -> map.removeWidget(this));
		}
	}
	
	public TerramapPlayer getPlayer() {
		return this.player;
	}

	@Override
	public int getDeltaX() {
		return -8;
	}

	@Override
	public int getDeltaY() {
		return -8;
	}

	@Override
	protected double getActualLongitude() {
		return this.player.getLongitude();
	}

	@Override
	protected double getActualLatitude() {
		return this.player.getLatitude();
	}

}
