package fr.thesmyler.terramap.network.mapsync;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TerramapRemotePlayer extends TerramapPlayer {
	
	protected UUID uuid;
	protected ITextComponent displayName;
	protected double longitude;
	protected double latitude;
	protected boolean isSpectator;
	protected NetworkPlayerInfo playerInfo;
	
	public TerramapRemotePlayer(UUID uuid, ITextComponent name, double longitude, double latitude, boolean isSpectator) {
		this.uuid = uuid;
		this.displayName = name;
		this.longitude = longitude;
		this.latitude = latitude;
		this.isSpectator = isSpectator;
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
	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@Override
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getSkin() {
		if(this.playerInfo == null) {
			this.playerInfo = Minecraft.getMinecraft().getConnection().getPlayerInfo(this.getUUID());
		}
		return this.playerInfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUUID()) : this.playerInfo.getLocationSkin();
	}

	@Override
	public boolean isSpectator() {
		return this.isSpectator;
	}
	
	public void setIsSpectator(boolean yesNo) {
		this.isSpectator = yesNo;
	}

}
