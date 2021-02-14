package fr.thesmyler.terramap.gui.widgets.markers.markers.entities;

import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import fr.thesmyler.terramap.network.playersync.TerramapLocalPlayer;
import fr.thesmyler.terramap.network.playersync.TerramapPlayer;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class OtherPlayerMarker extends AbstractPlayerMarker {
	
	protected TerramapPlayer player;

	public OtherPlayerMarker(MarkerController<?> controller, TerramapPlayer player, int downscaleFactor) {
		super(controller, null, downscaleFactor);
		this.player = player;
	}
	
	@Override
	public void update(MapWidget map) {
		super.update(map);
		if(
				!TerramapClientContext.getContext().hasPlayer(this.player.getUUID())
			  || (this.player instanceof TerramapLocalPlayer && ((TerramapLocalPlayer) this.player).getPlayer().isDead)) {
			map.scheduleForNextScreenUpdate(() -> map.removeWidget(this));
		}
	}
	
	public TerramapPlayer getPlayer() {
		return this.player;
	}
	
	@Override
	protected ResourceLocation getSkin() {
		return this.player.getSkin();
	}

	@Override
	protected float getTransparency() {
		return this.player.isSpectator() ? 0.6f: 1f;
	}

	@Override
	protected boolean showName(boolean hovered) {
		return super.showName(hovered) && !this.player.isSpectator() || hovered;
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return this.player.getDisplayName();
	}

	@Override
	public String getIdentifier() {
		return this.getControllerId() + ":" + this.player.getUUID().toString();
	}

	@Override
	protected double[] getActualCoordinates() throws OutOfProjectionBoundsException {
		return this.player.getGeoCoordinates();
	}

	@Override
	protected float getActualAzimuth() throws OutOfProjectionBoundsException {
		return this.player.getAzimut();
	}

}
