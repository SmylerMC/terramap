package fr.thesmyler.terramap.network.playersync;

import java.util.UUID;

import fr.thesmyler.terramap.util.geo.GeoPoint;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TerramapPlayer {

    public abstract UUID getUUID();

    public abstract ITextComponent getDisplayName();

    public abstract GeoPoint<?> getLocation() throws OutOfProjectionBoundsException;

    public abstract float getAzimuth();

    public abstract GameType getGamemode();

    public boolean isSpectator() {
        return this.getGamemode().equals(GameType.SPECTATOR);
    }

    @SideOnly(Side.CLIENT)
    public abstract ResourceLocation getSkin();

}
