package fr.smyler.terramap.network;

import java.util.Map;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.client.Minecraft;
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
		GameProfile profile = new GameProfile(this.getUUID(), displayName);
		Minecraft minecraft = Minecraft.getMinecraft();
        Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profile);
        if (map.containsKey(Type.SKIN)) return minecraft.getSkinManager().loadSkin(map.get(Type.SKIN), Type.SKIN);
        else return DefaultPlayerSkin.getDefaultSkin(this.getUUID());
	}

	@Override
	public boolean isSpectator() {
		return this.isSpectator;
	}

}
