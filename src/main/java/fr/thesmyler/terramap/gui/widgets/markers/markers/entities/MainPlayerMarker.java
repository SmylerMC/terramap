package fr.thesmyler.terramap.gui.widgets.markers.markers.entities;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import fr.thesmyler.terramap.util.geo.GeoPoint;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
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

    private GeoPoint playerLocation;
    private float playerAzimuth;

    public MainPlayerMarker(MarkerController<?> controller, int downscaleFactor) {
        super(controller, null, downscaleFactor);
    }

    @Override
    public void onUpdate(float mouseX, float mouseY, WidgetContainer parent) {
        if(Minecraft.getMinecraft().player == null) {
            parent.scheduleBeforeNextUpdate(() -> parent.removeWidget(this));
            return;
        }
        if(TerramapClientContext.getContext().getProjection() == null) return;
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        try {
            GeographicProjection proj = TerramapClientContext.getContext().getProjection();
            this.playerLocation =  new GeoPoint(proj.toGeo(player.posX, player.posZ));
        } catch(OutOfProjectionBoundsException e) {
            this.playerLocation = null;
        }
        try {
            this.playerAzimuth = TerramapClientContext.getContext().getProjection().azimuth(player.posX, player.posZ, player.rotationYaw);
        } catch(OutOfProjectionBoundsException e) {
            this.playerAzimuth = Float.NaN;
        }
        super.onUpdate(mouseX, mouseY, parent);
    }

    @Override
    protected ResourceLocation getSkin() {
        return Minecraft.getMinecraft().player.getLocationSkin();
    }

    @Override
    protected GeoPoint getActualLocation() {
        return this.playerLocation;
    }

    @Override
    protected float getTransparency() {
        return 1f;
    }

    @Override
    public ITextComponent getDisplayName() {
        if(Minecraft.getMinecraft().player != null) {
            return Minecraft.getMinecraft().player.getDisplayName();
        } else {
            return new TextComponentString("Missing main player");
        }
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
    protected float getActualAzimuth() throws OutOfProjectionBoundsException {
        return this.playerAzimuth;
    }

}
