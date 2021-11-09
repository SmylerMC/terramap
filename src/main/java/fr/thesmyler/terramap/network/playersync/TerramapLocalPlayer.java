package fr.thesmyler.terramap.network.playersync;

import java.util.UUID;

import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.util.TerramapUtil;
import fr.thesmyler.terramap.util.geo.GeoPointMutable;
import fr.thesmyler.terramap.util.geo.GeoPointReadOnly;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TerramapLocalPlayer extends TerramapPlayer {

    protected final EntityPlayer player;
    private final GeoPointMutable location = new GeoPointMutable();

    public TerramapLocalPlayer(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public UUID getUUID() {
        return this.player.getPersistentID();
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.player.getDisplayName();
    }

    @Override
    public GeoPointReadOnly getLocation() throws OutOfProjectionBoundsException {
        GeographicProjection proj;
        if(this.player.world.isRemote) {
            proj = TerramapClientContext.getContext().getProjection();
        } else {
            proj = TerramapUtil.getEarthGeneratorSettingsFromWorld(this.player.world).projection();
        }
        if(proj == null) return null;
        this.location.set(proj.toGeo(this.player.posX, this.player.posZ));
        return this.location.getReadOnly();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getSkin() {
        return ((AbstractClientPlayer)this.player).getLocationSkin();
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }

    @Override
    public GameType getGamemode() {
        return TerramapMod.proxy.getGameMode(this.player);
    }

    @Override
    public float getAzimuth() {
        GeographicProjection proj;
        if(this.player.world.isRemote) {
            proj = TerramapClientContext.getContext().getProjection();
        } else {
            proj = TerramapUtil.getEarthGeneratorSettingsFromWorld(this.player.world).projection();
        }
        if(proj == null) return Float.NaN;
        try{
            return proj.azimuth(this.player.posX, this.player.posZ, this.player.rotationYaw);
        } catch(OutOfProjectionBoundsException e) {
            return Float.NaN;
        }
    }

}
