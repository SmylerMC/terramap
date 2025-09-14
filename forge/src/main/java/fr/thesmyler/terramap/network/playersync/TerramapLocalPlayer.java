package fr.thesmyler.terramap.network.playersync;

import java.util.UUID;

import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.TerramapMod;
import net.smyler.terramap.content.Position;
import net.smyler.terramap.content.PositionImmutable;
import net.smyler.terramap.util.geo.*;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static fr.thesmyler.terramap.util.TerramapUtil.getWorldProjection;

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
    public GeoPointView getLocation() throws OutOfGeoBoundsException {
        GeoProjection projection;
        if (this.player.world.isRemote) {
            projection = TerramapClientContext.getContext().getProjection();
        } else {
            projection = getWorldProjection(this.player.world);
        }
        if (projection == null) {
            return null;
        }
        Position position = new PositionImmutable(this.player.posX, this.player.posY, this.player.posZ);
        projection.toGeo(this.location, position);
        return this.location.getReadOnlyView();
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
        GeoProjection projection;
        if (this.player.world.isRemote) {
            projection = TerramapClientContext.getContext().getProjection();
        } else {
            projection = getWorldProjection(this.player.world);
        }
        if (projection == null) {
            return Float.NaN;
        }
        Position position = new PositionImmutable(this.player.posX, this.player.posY, this.player.posZ);
        projection.toGeo(this.location, position);
        try{
            return projection.azimuth(position);
        } catch(OutOfGeoBoundsException e) {
            return 0f;
        }
    }

}
