package fr.thesmyler.terramap.network.playersync;

import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TerramapRemotePlayer extends TerramapPlayer {

	protected UUID uuid;
	protected ITextComponent displayName;
	protected double longitude;
	protected double latitude;
	protected float azimut;
	protected GameType gamemode;
	protected ResourceLocation texture;
	protected boolean texureRequested = false;

	public TerramapRemotePlayer(UUID uuid, ITextComponent name, double longitude, double latitude, float azimut, GameType gameMode) {
		this.uuid = uuid;
		this.displayName = name;
		this.longitude = longitude;
		this.latitude = latitude;
		this.azimut = azimut;
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

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@Override
	public float getAzimut() {
		return this.azimut;
	}

	public void setAzimut(float azimut) {
		this.azimut = azimut;
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
	public double[] getGeoCoordinates() {
		return new double[] {this.longitude, this.latitude};
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
