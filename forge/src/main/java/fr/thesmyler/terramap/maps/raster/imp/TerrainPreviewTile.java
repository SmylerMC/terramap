package fr.thesmyler.terramap.maps.raster.imp;

import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.maps.raster.RasterTile;
import net.smyler.smylib.Identifier;
import net.smyler.terramap.util.geo.TilePosImmutable;
import net.buildtheearth.terraplusplus.generator.TerrainPreview;

import static net.smyler.smylib.SmyLib.getGameClient;

public class TerrainPreviewTile implements RasterTile {

    private final TilePosImmutable position;
    private Identifier texture;
    private CompletableFuture<BufferedImage> textureTask;

    public TerrainPreviewTile(TilePosImmutable position) {
        this.position = position;
    }

    @Override
    public boolean isTextureAvailable() {
        try {
            this.tryLoadingTexture();
        } catch (Throwable e) {
            return false;
        }
        return this.texture != null;
    }

    @Override
    public Identifier getTexture() throws Throwable {

        if(this.getPosition().getZoom() < TerrainPreviewMap.BASE_ZOOM_LEVEL)
            throw new IllegalArgumentException("Trying to request a terrain preview with a zoom that's too low (" + this.position.getZoom() + ")");

        if(this.getPosition().getZoom() != TerrainPreviewMap.BASE_ZOOM_LEVEL) return null;

        if(this.texture == null) {
            if(this.textureTask == null) {
                TerrainPreview preview = TerramapClientContext.getContext().getTerrainPreview();
                if(preview != null) {
                    this.textureTask = preview.tile(this.position.getX(), this.position.getY(), TerrainPreviewMap.BASE_ZOOM_LEVEL - this.position.getZoom());
                }
            } else this.tryLoadingTexture();
        }
        return this.texture;
    }

    @Override
    public void cancelTextureLoading() {
    }

    @Override
    public void unloadTexture() {
        this.cancelTextureLoading();
        if(this.texture != null) {
            getGameClient().guiDrawContext().unloadDynamicTexture(this.texture);
            this.texture = null;
        }
    }

    @Override
    public TilePosImmutable getPosition() {
        return this.position;
    }

    private void tryLoadingTexture() throws Throwable {
        if(this.textureTask != null && this.textureTask.isDone()){
            if(this.textureTask.isCompletedExceptionally()) {
                if(!this.textureTask.isCancelled()) {
                    try {
                        this.textureTask.get(); // That will throw an exception
                    } catch(ExecutionException e) {
                        this.textureTask = null;
                        throw e.getCause();
                    }
                }
                this.textureTask = null;
                return;
            }
            BufferedImage image = this.textureTask.get();
            this.texture = getGameClient().guiDrawContext().loadDynamicTexture(image);
            this.textureTask = null;
        }
    }

}
