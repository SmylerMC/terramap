package fr.thesmyler.terramap.network.playersync;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
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
	protected NetworkPlayerInfo playerInfo;
	
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
		if(this.playerInfo == null) {
			this.playerInfo = Minecraft.getMinecraft().getConnection().getPlayerInfo(this.getUUID());
		}
		return this.playerInfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUUID()) : this.playerInfo.getLocationSkin();
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

}
