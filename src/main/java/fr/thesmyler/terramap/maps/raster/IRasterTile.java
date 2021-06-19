package fr.thesmyler.terramap.maps.raster;

import fr.thesmyler.terramap.util.TilePosUnmutable;
import net.minecraft.util.ResourceLocation;

public interface IRasterTile {

    public boolean isTextureAvailable();

    public ResourceLocation getTexture() throws Throwable;

    public void cancelTextureLoading();

    public void unloadTexture();

    public TilePosUnmutable getPosition();

}
