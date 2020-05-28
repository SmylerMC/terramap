package fr.thesmyler.terramap.network.mapsync;

import java.util.UUID;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TerramapLocalPlayer extends TerramapPlayer {

	protected EntityPlayer player;
	
	public TerramapLocalPlayer(EntityPlayer player) {
		this.player = player;
	}
	
	@Override
	public UUID getUUID() {
		return this.player.getPersistentID();
	}

	@Override
	public String getDisplayName() {
		return this.player.getDisplayNameString();
	}

	@Override
	public double getPosX() {
		return this.player.posX;
	}

	@Override
	public double getPosZ() {
		return this.player.posZ;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getSkin() {
		return ((AbstractClientPlayer)this.player).getLocationSkin();
	}

	@Override
	public boolean isSpectator() {
		return this.player.isSpectator();
	}

}
