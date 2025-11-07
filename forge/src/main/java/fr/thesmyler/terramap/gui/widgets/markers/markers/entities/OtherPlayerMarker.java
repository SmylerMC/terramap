package fr.thesmyler.terramap.gui.widgets.markers.markers.entities;

import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import net.smyler.smylib.Identifier;
import net.smyler.terramap.entity.player.PlayerClientside;
import net.smyler.terramap.entity.player.PlayerServersideForge;
import net.smyler.terramap.entity.player.Player;
import net.smyler.smylib.text.Text;
import net.smyler.terramap.geo.GeoPoint;
import net.minecraft.util.ResourceLocation;
import net.smyler.terramap.geo.OutOfGeoBoundsException;

public class OtherPlayerMarker extends AbstractPlayerMarker {

    protected final PlayerClientside player;

    public OtherPlayerMarker(MarkerController<?> controller, PlayerClientside player, int downscaleFactor) {
        super(controller, downscaleFactor);
        this.player = player;
    }

    @Override
    public void update(MapWidget map) {
        super.update(map);
        if(
                !TerramapClientContext.getContext().hasPlayer(this.player.uuid())
                || (this.player instanceof PlayerServersideForge && ((PlayerServersideForge) this.player).getPlayer().isDead)) {
            map.scheduleBeforeNextUpdate(() -> map.removeMarker(this));
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    @Override
    protected ResourceLocation getSkin() {
        Identifier textureId = this.player.skin();
        return new ResourceLocation(textureId.namespace, textureId.path);
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
        return this.player.displayName();
    }

    @Override
    public String getIdentifier() {
        return this.getControllerId() + ":" + this.player.uuid().toString();
    }

    @Override
    protected GeoPoint getActualLocation() throws OutOfGeoBoundsException {
        return this.player.location();
    }

    @Override
    protected float getActualAzimuth() {
        return this.player.azimuth();
    }

}
