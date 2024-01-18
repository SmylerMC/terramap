package fr.thesmyler.terramap.gui.widgets.map.layer;

import fr.thesmyler.smylibgui.container.FlexibleWidgetContainer;
import fr.thesmyler.terramap.maps.raster.RasterTiledMap;
import fr.thesmyler.terramap.maps.raster.imp.TerrainPreviewMap;

import static net.smyler.smylib.SmyLib.getGameClient;

public class GenerationPreviewLayer extends RasterMapLayer {

    private final TerrainPreviewMap map = new TerrainPreviewMap();

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
    public RasterTiledMap getTiledMap() {
        return this.map;
    }

}
