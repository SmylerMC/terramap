package fr.thesmyler.terramap.network.mapsync;

import java.util.UUID;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TerramapPlayer {

	public abstract UUID getUUID();

	public abstract ITextComponent getDisplayName();

	public abstract double getLongitude();

	public abstract double getLatitude();
	
	public abstract boolean isSpectator();
	
	@SideOnly(Side.CLIENT)
	public abstract ResourceLocation getSkin();

}
