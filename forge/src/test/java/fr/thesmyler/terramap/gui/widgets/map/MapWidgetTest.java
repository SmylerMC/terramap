package fr.thesmyler.terramap.gui.widgets.map;

import com.google.gson.JsonPrimitive;
import net.smyler.smylib.game.TestGameClient;
import fr.thesmyler.terramap.TerramapTest;
import fr.thesmyler.terramap.gui.widgets.map.layer.OnlineRasterMapLayer;
import fr.thesmyler.terramap.gui.widgets.map.layer.RasterMapLayer;
import fr.thesmyler.terramap.maps.SavedLayerState;
import fr.thesmyler.terramap.maps.SavedMapState;
import net.smyler.smylib.gui.screen.Screen;
import net.smyler.smylib.math.Vec2dImmutable;
import net.smyler.terramap.tilesets.raster.RasterTileSetManager;
import net.smyler.terramap.geo.GeoPointImmutable;
import org.junit.jupiter.api.Test;

import static fr.thesmyler.terramap.MapContext.FULLSCREEN;
import static fr.thesmyler.terramap.gui.widgets.map.MapLayerRegistry.RASTER_LAYER_ID;
import static net.smyler.terramap.Terramap.getTerramap;
import static net.smyler.terramap.geo.GeoPointImmutable.ORIGIN;
import static fr.thesmyler.terramap.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class MapWidgetTest extends TerramapTest {

    public static GeoPointImmutable PARIS = new GeoPointImmutable(2.350987d, 48.856667d);

    @Test
    void canSaveMapWidgetToSavedMapState() throws InterruptedException {
        TestGameClient client = this.getTestGameClient();
        RasterTileSetManager tileSetManager = getTerramap().rasterTileSetManager();
        client.setWindowDimensions(500f, 500f);
        client.setTargetFps(60);
        MapWidget map = new MapWidget(0f, 0f, 0, 500f, 500f, FULLSCREEN, 1f);
        client.getCurrentScreen().addWidget(map);
        OnlineRasterMapLayer raster_osm = (OnlineRasterMapLayer) map.createLayer(RASTER_LAYER_ID);
        OnlineRasterMapLayer osm_fr_hot = (OnlineRasterMapLayer) map.createLayer(RASTER_LAYER_ID);
        raster_osm.setTiledMap(tileSetManager.getBaseMaps().get("osm"));
        map.setLayerZ(raster_osm, -2);
        osm_fr_hot.setTiledMap(tileSetManager.getBaseMaps().get("osm_fr_hot"));
        map.setLayerZ(osm_fr_hot, -1);
        osm_fr_hot.setAlpha(0.5f);
        osm_fr_hot.setVisibility(false);
        osm_fr_hot.setRenderingOffset(new Vec2dImmutable(0.042d, -0.24d));
        osm_fr_hot.setRotationOffset(15f);

        map.getController().setZoom(10, false);
        map.getController().setRotation(45f, false);
        client.doTick();

        map.getController().moveLocationToCenter(PARIS, false);
        client.doTick();

        SavedMapState saved = map.save();

        assertEquals(0, PARIS.distanceTo(saved.center), 1e-3d);
        assertEquals(10d, saved.zoom);
        assertEquals(45f, saved.rotation);
        assertEquals(2, saved.layers.size());

        SavedLayerState savedOsm = saved.layers.get(0);
        assertEquals(RASTER_LAYER_ID, savedOsm.type);
        assertEquals(-2, savedOsm.z);
        assertEquals(1f, savedOsm.alpha);
        assertEquals(0d, savedOsm.cartesianOffset.subtract(0d, 0d).norm(), 1e-5d);
        assertEquals(0f, savedOsm.rotationOffset, 1e-5f);

        SavedLayerState savedOsmFrHot = saved.layers.get(1);
        assertEquals(RASTER_LAYER_ID, savedOsmFrHot.type);
        assertEquals(-1, savedOsmFrHot.z);
        assertEquals(0.5f, savedOsmFrHot.alpha);
        assertFalse(savedOsmFrHot.visible);
        assertEquals(0, savedOsmFrHot.cartesianOffset.subtract(0.042d, -0.24d).norm(), 1e-5d);
        assertEquals(15f, savedOsmFrHot.rotationOffset, 1e-5f);

    }

    @Test
    public void canRestoreMapState() throws InterruptedException {

        TestGameClient client = this.getTestGameClient();
        RasterTileSetManager tileSetManager = getTerramap().rasterTileSetManager();
        client.setWindowDimensions(500f, 500f);
        client.setTargetFps(60);
        Screen screen = client.getCurrentScreen();

        MapWidget map = new MapWidget(0f, 0f, 0, 500f, 500f, FULLSCREEN, 1f);
        client.moveMouse(750, 750, 1000);
        screen.addWidget(map);

        // Let's start in some random state
        OnlineRasterMapLayer rasterLayer = (OnlineRasterMapLayer) map.createLayer(RASTER_LAYER_ID);
        rasterLayer.setTiledMap(tileSetManager.getBaseMaps().get("osm"));
        rasterLayer.setVisibility(false);
        map.setLayerZ(rasterLayer, -1);
        map.getController().setZoomStaticLocation(PARIS);
        map.getController().setZoom(18, true);
        client.runFor(1000);
        map.getController().moveLocationToCenter(PARIS, true);
        client.runFor(1000);
        map.getController().setRotationStaticLocation(PARIS);
        map.getController().setRotation(45f, true);

        // A completely different saved state
        SavedMapState state = new SavedMapState();
        state.center.set(ORIGIN);
        state.zoom = 0;
        state.rotation = 0;
        SavedLayerState layerState = new SavedLayerState();
        layerState.type = RASTER_LAYER_ID;
        layerState.settings.add("style", new JsonPrimitive("osm_fr_hot"));
        state.layers.add(layerState);

        // And we try restoring...
        map.restore(state);
        client.doTick();

        assertEquals(ORIGIN, map.getController().getCenterLocation().getImmutable());
        assertEquals(0, map.getController().getZoom());
        assertEquals(0, map.getController().getRotation());
        assertEquals(1, map.getLayers().size()); // Input layer and raster layer
        MapLayer layer = map.getLayers().get(0);
        assertInstanceOf(RasterMapLayer.class, layer);
        assertEquals("osm_fr_hot", ((RasterMapLayer) layer).getTiledMap().getId());
        assertTrue(layer.isVisible());

    }

    @Test
    public void canRestoreMapStateWithInvalidLayerId() throws InterruptedException {

        TestGameClient client = this.getTestGameClient();
        client.setWindowDimensions(500f, 500f);
        client.setTargetFps(60);

        Screen screen = client.getCurrentScreen();

        MapWidget map = new MapWidget(0f, 0f, 0, 500f, 500f, FULLSCREEN, 1f);
        client.moveMouse(750, 750, 1000);
        screen.addWidget(map);

        SavedMapState state = new SavedMapState();
        SavedLayerState layerState = new SavedLayerState();
        layerState.type = "This is not a valid layer type";
        state.layers.add(layerState);

        map.restore(state);
        client.doTick();

    }

    @Test
    public void layersViewportsAreProperlyUpdatedWhenMapResizes() {
        MapWidget map = new MapWidget(0f, 0f, 0, 100f, 100F, FULLSCREEN, 1d);
        assertEquals(new Vec2dImmutable(100d, 100d), map.getInputLayer().getRenderSpaceDimensions(), 1e-3);
        map.setSize(500f, 500f);
        assertEquals(new Vec2dImmutable(500d, 500d), map.getInputLayer().getRenderSpaceDimensions(), 1e-3);
        map.setWidth(100f);
        assertEquals(new Vec2dImmutable(100d, 500d), map.getInputLayer().getRenderSpaceDimensions(), 1e-3);
        map.setHeight(100f);
        assertEquals(new Vec2dImmutable(100d, 100d), map.getInputLayer().getRenderSpaceDimensions(), 1e-3);
    }

}