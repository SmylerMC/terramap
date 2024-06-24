package fr.thesmyler.terramap.maps.raster.imp;

import fr.thesmyler.terramap.maps.raster.RasterTile;
import net.smyler.smylib.Identifier;
import net.smyler.terramap.util.geo.TilePosImmutable;

public class ColorTile implements RasterTile {

    private final TilePosImmutable position;
    private final Identifier texture;

    public ColorTile(TilePosImmutable position, Identifier texture) {
        this.position = position;
        this.texture = texture;
    }

    @Override
    public boolean isTextureAvailable() {
        return this.texture != null;
    }

    @Override
    public Identifier getTexture() {
        return this.texture;
    }

    @Override
    public void cancelTextureLoading() {
    }

    @Override
    public void unloadTexture() {
        // Nop, we don't want to do that !
    }

    @Override
    public TilePosImmutable getPosition() {
        return this.position;
    }

}
