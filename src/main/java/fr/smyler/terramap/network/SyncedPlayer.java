package fr.smyler.terramap.network;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

public class SyncedPlayer {
	
	protected UUID uuid;
	protected String displayName;
	protected double posX;
	protected double posZ;
	
	public SyncedPlayer(UUID uuid, String name, double x, double z) {
		this.uuid = uuid;
		this.displayName = name;
		this.posX = x;
		this.posZ = z;
	}
	
	public SyncedPlayer(EntityPlayer player) {
		this.uuid = player.getPersistentID();
		this.displayName = player.getDisplayNameString();
		this.posX = player.posX;
		this.posZ = player.posZ;
	}

	public UUID getUUID() {
		return uuid;
	}

	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public double getPosX() {
		return posX;
	}

	public void setPosX(double posX) {
		this.posX = posX;
	}

	public double getPosZ() {
		return posZ;
	}

	public void setPosZ(double posZ) {
		this.posZ = posZ;
	}

}
