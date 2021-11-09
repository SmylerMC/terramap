package fr.thesmyler.terramap.maps.raster;

import fr.thesmyler.terramap.util.geo.TilePosImmutable;
import net.minecraft.util.ResourceLocation;

public interface IRasterTile {

    boolean isTextureAvailable();

    ResourceLocation getTexture() throws Throwable;

    void cancelTextureLoading();

    void unloadTexture();

    TilePosImmutable getPosition();

}
