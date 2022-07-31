package fr.thesmyler.terramap.maps.raster.imp;

import fr.thesmyler.terramap.maps.raster.IRasterTile;
import fr.thesmyler.terramap.util.geo.TilePosImmutable;
import net.minecraft.util.ResourceLocation;

public class ColorTile implements IRasterTile {

    private final TilePosImmutable position;
    private final ResourceLocation texture;

    public ColorTile(TilePosImmutable position, ResourceLocation texture) {
        this.position = position;
        this.texture = texture;
    }

    @Override
    public boolean isTextureAvailable() {
        return this.texture != null;
    }

    @Override
    public ResourceLocation getTexture() {
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
