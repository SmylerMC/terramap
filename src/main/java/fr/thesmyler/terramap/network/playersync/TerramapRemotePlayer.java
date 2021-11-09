package fr.thesmyler.terramap.network.playersync;

import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import fr.thesmyler.terramap.util.geo.GeoPoint;
import fr.thesmyler.terramap.util.geo.GeoPointMutable;
import fr.thesmyler.terramap.util.geo.GeoPointReadOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TerramapRemotePlayer extends TerramapPlayer {

    protected final UUID uuid;
    protected ITextComponent displayName;
    protected final GeoPointMutable location = new GeoPointMutable();
    protected float azimuth;
    protected GameType gamemode;
    protected ResourceLocation texture;
    protected boolean texureRequested = false;

    public TerramapRemotePlayer(UUID uuid, ITextComponent name, GeoPoint<?> location, float azimuth, GameType gameMode) {
        this.uuid = uuid;
        this.displayName = name;
        this.location.set(location);
        this.azimuth = azimuth;
        this.gamemode = gameMode;
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
    public GeoPointReadOnly getLocation() {
        return this.location.getReadOnly();
    }
    
    public void setLocation(GeoPoint<?> location) {
        this.location.set(location);
    }

    @Override
    public float getAzimuth() {
        return this.azimuth;
    }

    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
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
