package fr.thesmyler.terramap.gui.widgets.map.layer;

import net.smyler.smylib.gui.containers.FlexibleWidgetContainer;
import net.smyler.terramap.tilesets.raster.RasterTileSet;
import net.smyler.terramap.tilesets.raster.TerrainPreviewTileSet;

import static net.smyler.smylib.SmyLib.getGameClient;

public class GenerationPreviewLayer extends RasterMapLayer {

    private final TerrainPreviewTileSet map = new TerrainPreviewTileSet();

    @Override
    public String name() {
        return getGameClient().translator().format("terramap.mapwidget.layers.preview.name");
    }

    @Override
    public String description() {
        return getGameClient().translator().format("terramap.mapwidget.layers.preview.description");
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public FlexibleWidgetContainer createConfigurationContainer() {
        return null;
    }

    @Override
    public RasterTileSet getTiledMap() {
        return this.map;
    }

}
