package fr.thesmyler.terramap.gui.widgets.markers.markers.entities;

import net.smyler.smylib.gui.containers.WidgetContainer;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import net.smyler.smylib.text.Text;
import net.smyler.terramap.Terramap;
import net.smyler.terramap.content.PositionMutable;
import net.smyler.terramap.content.Position;
import net.smyler.terramap.util.geo.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

/**
 * This class represents a marker for the actual player corresponding to this client
 * 
 * @author SmylerMC
 *
 */
public class MainPlayerMarker extends AbstractPlayerMarker {

    private final GeoPointMutable playerLocation = new GeoPointMutable();
    private float playerAzimuth;
    private boolean isOutOfBounds = false;

    public MainPlayerMarker(MarkerController<?> controller, int downscaleFactor) {
        super(controller, downscaleFactor);
    }

    @Override
    public void onUpdate(float mouseX, float mouseY, WidgetContainer parent) {
        if(Minecraft.getMinecraft().player == null) {
            parent.scheduleBeforeNextUpdate(() -> parent.removeWidget(this));
            return;
        }
        if(TerramapClientContext.getContext().getProjection() == null) return;
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        GeoProjection projection = TerramapClientContext.getContext().getProjection();
        Position position = new PositionMutable(player.posX, player.posY, player.posZ, player.cameraYaw, player.cameraPitch);
        try {
            projection.toGeo(this.playerLocation, position);
            this.playerAzimuth = projection.azimuth(position);
            this.isOutOfBounds = false;
        } catch(OutOfGeoBoundsException e) {
            this.isOutOfBounds = true;
        }
        super.onUpdate(mouseX, mouseY, parent);
    }

    @Override
    protected ResourceLocation getSkin() {
        return Minecraft.getMinecraft().player.getLocationSkin();
    }

    @Override
    protected GeoPoint getActualLocation() throws OutOfGeoBoundsException {
        if (this.isOutOfBounds) {
            throw new OutOfGeoBoundsException("Player is currently out of the projected area");
        }
        return this.playerLocation.getReadOnlyView();
    }

    @Override
    protected float getTransparency() {
        return 1f;
    }

    @Override
    public Text getDisplayName() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        ITextComponent mcName = player == null ? new TextComponentString("Missing main player"): player.getDisplayName();
        String nameJson = ITextComponent.Serializer.componentToJson(mcName);
        return Terramap.instance().gson().fromJson(nameJson, Text.class);
    }

    @Override
    public String getIdentifier() {
        String uuid = null;
        if(Minecraft.getMinecraft().player != null) {
            uuid = Minecraft.getMinecraft().player.getUniqueID().toString();
        }
        return this.getControllerId() + ":" + uuid;
    }

    @Override
    protected float getActualAzimuth() throws OutOfGeoBoundsException {
        if (this.isOutOfBounds) {
            throw new OutOfGeoBoundsException("Player is currently out of the projected area");
        }
        return this.playerAzimuth;
    }

}
