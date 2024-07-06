package net.smyler.terramap.tilesets.raster;

import fr.thesmyler.terramap.TerramapClientContext;
import net.buildtheearth.terraplusplus.generator.TerrainPreview;
import net.smyler.smylib.Identifier;
import net.smyler.terramap.util.geo.TilePosImmutable;

import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static net.smyler.smylib.SmyLib.getGameClient;

public class TerrainPreviewTileSet extends CachingRasterTileSet {

    public static final int BASE_ZOOM_LEVEL = 16;

    public TerrainPreviewTileSet() {
        this.setUsesLowZoom(false); // Loading tiles at low zoom levels takes forever here
    }

    @Override
    protected RasterTile createNewTile(TilePosImmutable position) {
        return new TerrainPreviewTile(position);
    }

    @Override
    public String getId() {
        return "terrain_preview_debug";
    }

    @Override
    public String getLocalizedName(String localeKey) {
        return getGameClient().translator().format("terramap.maps.debug.terrain"); // This is always local
    }

    @Override
    public String getComment() {
        return "Terra++ terrain preview debug map";
    }

    @Override
    public RasterTileSetProvider getProvider() {
        return RasterTileSetProvider.INTERNAL;
    }

    @Override
    public long getProviderVersion() {
        return 0;
    }

    @Override
    public int getDisplayPriority() {
        return 0;
    }

    @Override
    public boolean isAllowedOnMinimap() {
        return true;
    }

    @Override
    public boolean isDebug() {
        return true;
    }

    @Override
    public Identifier getDefaultTileTexture() {
        return null;
    }

    @Override
    public int getMinZoom() {
        return BASE_ZOOM_LEVEL;
    }

    @Override
    public int getMaxZoom() {
        return 20;
    }

    private static class TerrainPreviewTile implements RasterTile {

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

            if(this.getPosition().getZoom() < TerrainPreviewTileSet.BASE_ZOOM_LEVEL)
                throw new IllegalArgumentException("Trying to request a terrain preview with a zoom that's too low (" + this.position.getZoom() + ")");

            if(this.getPosition().getZoom() != TerrainPreviewTileSet.BASE_ZOOM_LEVEL) return null;

            if(this.texture == null) {
                if(this.textureTask == null) {
                    TerrainPreview preview = TerramapClientContext.getContext().getTerrainPreview();
                    if(preview != null) {
                        this.textureTask = preview.tile(this.position.getX(), this.position.getY(), TerrainPreviewTileSet.BASE_ZOOM_LEVEL - this.position.getZoom());
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

}
