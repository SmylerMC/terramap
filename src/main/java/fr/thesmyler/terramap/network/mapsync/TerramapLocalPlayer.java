package fr.thesmyler.terramap.network.mapsync;

import java.util.UUID;

import fr.thesmyler.terramap.TerramapServer;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
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
	public ITextComponent getDisplayName() {
		return this.player.getDisplayName();
	}

	//TODO This could be optimized
	@Override
	public double getLongitude() {
		return TerramapServer.getServer().getProjection().toGeo(this.player.posX, this.player.posZ)[0];
	}

	@Override
	public double getLatitude() {
		return TerramapServer.getServer().getProjection().toGeo(this.player.posX, this.player.posZ)[1];
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
	
	public EntityPlayer getPlayer() {
		return this.player;
	}

}
