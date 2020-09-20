package fr.thesmyler.terramap.gui.widgets.markers.markers;

import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import fr.thesmyler.terramap.network.mapsync.TerramapPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class PlayerMarker extends MovingMapMarkers {

	TerramapPlayer player;
	
	public PlayerMarker(MarkerController<?> controller, TerramapPlayer player) {
		super(controller, 16, 16);
		this.player = player;
	}

	@Override
	public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean focused, Screen parent) {
		GlStateManager.enableAlpha();
		if(hovered) Gui.drawRect(x - 7, y - 7, x + 9, y + 9, 0x50000000);
		Minecraft.getMinecraft().getTextureManager().bindTexture(this.player.getSkin());
		if(this.player.isSpectator()) GlStateManager.color(1, 1, 1, 0.6f);
		else GlStateManager.color(1, 1, 1, 1);
		Gui.drawModalRectWithCustomSizedTexture(x - 8, y - 8, 16, 16, 16, 16, 128, 128);
		Gui.drawModalRectWithCustomSizedTexture(x - 8, y - 8, 80, 16, 16, 16, 128, 128);
		GlStateManager.color(1, 1, 1, 1);
	}

	@Override
	public int getDeltaX() {
		return -8;
	}

	@Override
	public int getDeltaY() {
		return -8;
	}

}
