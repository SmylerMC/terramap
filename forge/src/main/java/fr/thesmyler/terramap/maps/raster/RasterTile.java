package fr.thesmyler.terramap.maps.raster;

import net.smyler.terramap.util.geo.TilePosImmutable;
import net.minecraft.util.ResourceLocation;

public interface RasterTile {

    boolean isTextureAvailable();

    ResourceLocation getTexture() throws Throwable;

    void cancelTextureLoading();

    void unloadTexture();

    TilePosImmutable getPosition();

}
