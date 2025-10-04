package net.smyler.terramap.tilesets.raster;

import net.smyler.smylib.Identifier;
import net.smyler.terramap.geo.TilePosImmutable;

public interface RasterTile {

    boolean isTextureAvailable();

    Identifier getTexture() throws Throwable;

    void cancelTextureLoading();

    void unloadTexture();

    TilePosImmutable getPosition();

}
