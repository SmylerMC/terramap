package fr.thesmyler.terramap.gui.widgets.markers.markers.entities;

import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import fr.thesmyler.terramap.network.playersync.TerramapLocalPlayer;
import fr.thesmyler.terramap.network.playersync.TerramapPlayer;
import net.smyler.smylib.text.Text;
import net.smyler.terramap.geo.GeoPoint;
import net.minecraft.util.ResourceLocation;
import net.smyler.terramap.geo.OutOfGeoBoundsException;

public class OtherPlayerMarker extends AbstractPlayerMarker {

    protected final TerramapPlayer player;

    public OtherPlayerMarker(MarkerController<?> controller, TerramapPlayer player, int downscaleFactor) {
        super(controller, downscaleFactor);
        this.player = player;
    }

    @Override
    public void update(MapWidget map) {
        super.update(map);
        if(
                !TerramapClientContext.getContext().hasPlayer(this.player.getUUID())
                || (this.player instanceof TerramapLocalPlayer && ((TerramapLocalPlayer) this.player).getPlayer().isDead)) {
            map.scheduleBeforeNextUpdate(() -> map.removeMarker(this));
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
    public Text getDisplayName() {
        return this.player.getDisplayName();
    }

    @Override
    public String getIdentifier() {
        return this.getControllerId() + ":" + this.player.getUUID().toString();
    }

    @Override
    protected GeoPoint getActualLocation() throws OutOfGeoBoundsException {
        return this.player.getLocation();
    }

    @Override
    protected float getActualAzimuth() {
        return this.player.getAzimuth();
    }

}
