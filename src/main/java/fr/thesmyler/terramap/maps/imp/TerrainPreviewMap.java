package fr.thesmyler.terramap.maps.imp;

import fr.thesmyler.terramap.maps.CachingRasterTiledMap;
import fr.thesmyler.terramap.maps.TiledMapProvider;
import fr.thesmyler.terramap.maps.utils.TilePosUnmutable;
import net.minecraft.client.resources.I18n;

public class TerrainPreviewMap extends CachingRasterTiledMap<TerrainPreviewTile> {

    public static final int BASE_ZOOM_LEVEL = 16;

    public TerrainPreviewMap() {
        super();
        this.setUseLowZoom(false); // Loading tiles at low zoom levels takes forever here
    }
    @Override
    protected TerrainPreviewTile createNewTile(TilePosUnmutable position) {
        return new TerrainPreviewTile(position);
    }

    @Override
    public String getId() {
        return "terrain_preview_debug";
    }

    @Override
    public String getLocalizedName(String localeKey) {
        return I18n.format("terramap.maps.debug.terrain"); // This is always local
    }

    @Override
    public String getComment() {
        return "Terra++ terrain perview debug map";
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
    public int getMinZoom() {
        return BASE_ZOOM_LEVEL;
    }

    @Override
    public int getMaxZoom() {
        return 20;
    }

}
