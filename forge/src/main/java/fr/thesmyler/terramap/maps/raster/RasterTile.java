package fr.thesmyler.terramap.maps.raster;

import net.smyler.smylib.Identifier;
import net.smyler.terramap.util.geo.TilePosImmutable;

public interface RasterTile {

    boolean isTextureAvailable();

    Identifier getTexture() throws Throwable;

    void cancelTextureLoading();

    void unloadTexture();

    TilePosImmutable getPosition();

}
