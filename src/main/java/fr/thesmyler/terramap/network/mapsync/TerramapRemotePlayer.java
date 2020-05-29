package fr.thesmyler.terramap.network.mapsync;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TerramapRemotePlayer extends TerramapPlayer {
	
	protected UUID uuid;
	protected String displayName;
	protected double posX;
	protected double posZ;
	protected boolean isSpectator;
	protected NetworkPlayerInfo playerInfo;
	
	public TerramapRemotePlayer(UUID uuid, String name, double x, double z, boolean isSpectator) {
		this.uuid = uuid;
		this.displayName = name;
		this.posX = x;
		this.posZ = z;
		this.isSpectator = isSpectator;
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public double getPosX() {
		return posX;
	}

	public void setPosX(double posX) {
		this.posX = posX;
	}

	@Override
	public double getPosZ() {
		return posZ;
	}

	public void setPosZ(double posZ) {
		this.posZ = posZ;
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
