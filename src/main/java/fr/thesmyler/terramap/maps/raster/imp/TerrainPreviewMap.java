package fr.thesmyler.terramap.maps.raster.imp;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.terramap.maps.raster.CachingRasterTiledMap;
import fr.thesmyler.terramap.maps.raster.TiledMapProvider;
import fr.thesmyler.terramap.util.geo.TilePosImmutable;
import net.minecraft.util.ResourceLocation;

public class TerrainPreviewMap extends CachingRasterTiledMap<TerrainPreviewTile> {

    public static final int BASE_ZOOM_LEVEL = 16;

    public TerrainPreviewMap() {
        super();
        this.setUseLowZoom(false); // Loading tiles at low zoom levels takes forever here
    }
    @Override
    protected TerrainPreviewTile createNewTile(TilePosImmutable position) {
        return new TerrainPreviewTile(position);
    }

    @Override
    public String getId() {
        return "terrain_preview_debug";
    }

    @Override
    public String getLocalizedName(String localeKey) {
        return SmyLibGui.getTranslator().format("terramap.maps.debug.terrain"); // This is always local
    }

    @Override
    public String getComment() {
        return "Terra++ terrain preview debug map";
    }

    @Override
    public TiledMapProvider getProvider() {
        return TiledMapProvider.INTERNAL;
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
    public ResourceLocation getDefaultTileTexture() {
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

}
