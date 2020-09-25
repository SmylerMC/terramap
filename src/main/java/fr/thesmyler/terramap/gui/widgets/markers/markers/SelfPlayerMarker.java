package fr.thesmyler.terramap.gui.widgets.markers.markers;

import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.terramap.TerramapServer;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ResourceLocation;

/**
 * This class represents a marker for the actual player corresponding to this client
 * 
 * @author SmylerMC
 *
 */
public class SelfPlayerMarker extends AbstractPlayerMarker {
	
	private double playerLongitude, playerLatitude;

	public SelfPlayerMarker(MarkerController<?> controller) {
		super(controller, null);
	}
	
	@Override
	public void onUpdate(Screen parent) {
		if(Minecraft.getMinecraft().player == null) {
			parent.scheduleForNextScreenUpdate(() -> parent.removeWidget(this));
			return;
		}
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		double[] lola = TerramapServer.getServer().getProjection().toGeo(player.posX, player.posZ);
		this.playerLongitude = lola[0];
		this.playerLatitude = lola[1];
		super.onUpdate(parent);
	}
	
	@Override
	protected ResourceLocation getSkin() {
		return Minecraft.getMinecraft().player.getLocationSkin();
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
		return this.playerLongitude;
	}

	@Override
	protected double getActualLatitude() {
		return this.playerLatitude;
	}

	@Override
	protected float getTransparency() {
		return 1f;
	}

	@Override
	protected String getName() {
		return Minecraft.getMinecraft().player.getDisplayNameString();
	}

	@Override
	protected boolean showName(boolean hovered) {
		return true;
	}
	
}
