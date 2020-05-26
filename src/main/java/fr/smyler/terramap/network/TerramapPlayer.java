package fr.smyler.terramap.network;

import java.util.UUID;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TerramapPlayer {

	public abstract UUID getUUID();

	public abstract String getDisplayName();

	public abstract double getPosX();

	public abstract double getPosZ();
	
	@SideOnly(Side.CLIENT)
	public abstract ResourceLocation getSkin();

}
