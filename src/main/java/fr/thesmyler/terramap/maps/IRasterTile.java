package fr.thesmyler.terramap.maps;

import fr.thesmyler.terramap.maps.utils.TilePosUnmutable;
import net.minecraft.util.ResourceLocation;

public interface IRasterTile {

	public boolean isTextureAvailable();
	
	public ResourceLocation getTexture() throws Throwable;
	
	public void cancelTextureLoading();
	
	public void unloadTexture();
	
	public TilePosUnmutable getPosition();
	
}
