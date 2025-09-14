package fr.thesmyler.terramap.network.playersync;

import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.smyler.terramap.util.geo.GeoPoint;
import net.smyler.terramap.util.geo.GeoPointMutable;
import net.smyler.terramap.util.geo.GeoPointView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smyler.terramap.util.geo.OutOfGeoBoundsException;

public class TerramapRemotePlayer extends TerramapPlayer {

    protected final UUID uuid;
    protected ITextComponent displayName;
    protected final GeoPointMutable location = new GeoPointMutable();
    protected float azimuth;
    protected boolean outOfProjection = true;
    protected GameType gamemode = GameType.NOT_SET;
    protected ResourceLocation texture;
    protected boolean texureRequested = false;

    public TerramapRemotePlayer(UUID uuid, ITextComponent name) {
        this.uuid = uuid;
        this.displayName = name;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public ITextComponent getDisplayName() {
        return displayName;
    }

    public void setDisplayName(ITextComponent displayName) {
        this.displayName = displayName;
    }
    
    @Override
    public GeoPointView getLocation() throws OutOfGeoBoundsException {
        if (this.outOfProjection) {
            throw new OutOfGeoBoundsException("Player out of projection");
        }
        return this.location.getReadOnlyView();
    }
    
    public void setLocationAndAzimuth(GeoPoint location, float azimuth) {
        this.location.set(location);
        this.azimuth = azimuth;
        this.outOfProjection = false;
    }

    @Override
    public float getAzimuth() {
        return this.azimuth;
    }

    public void setOutOfProjection() {
        this.outOfProjection = true;
    }

    public boolean isOutOfProjection() {
        return this.outOfProjection;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getSkin() {
        if(this.texture == null && !this.texureRequested) {
            GameProfile profile = new GameProfile(this.getUUID(), null);
            new Thread(() -> {
                Minecraft.getMinecraft().getSessionService().fillProfileProperties(profile, true);
                Minecraft.getMinecraft().getSkinManager().loadProfileTextures(profile, this::skinAvailable, false);
            }).start();
            this.texureRequested = true;
        }
        return this.texture == null ? DefaultPlayerSkin.getDefaultSkin(this.getUUID()) : this.texture;
    }

    public void setGamemode(GameType mode) {
        this.gamemode = mode;
    }

    @Override
    public GameType getGamemode() {
        return this.gamemode;
    }

    private void skinAvailable(Type type, ResourceLocation location, MinecraftProfileTexture profileTexture) {
        if(type.equals(Type.SKIN)) {
            this.texture = location;
        }
    }

}
