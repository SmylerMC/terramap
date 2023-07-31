package fr.thesmyler.terramap.gui.widgets.map;

import com.google.gson.JsonPrimitive;
import fr.thesmyler.smylibgui.container.TestingWidgetContainer;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapTest;
import fr.thesmyler.terramap.gui.widgets.map.layer.RasterMapLayer;
import fr.thesmyler.terramap.maps.SavedLayerState;
import fr.thesmyler.terramap.maps.SavedMapState;
import fr.thesmyler.terramap.maps.raster.MapStylesLibrary;
import fr.thesmyler.terramap.util.geo.GeoPointImmutable;
import fr.thesmyler.terramap.util.math.Vec2dImmutable;
import org.junit.jupiter.api.Test;

import static fr.thesmyler.terramap.gui.widgets.map.MapLayerLibrary.RASTER_LAYER_ID;
import static fr.thesmyler.terramap.util.geo.GeoPointTest.PARIS;
import static org.junit.jupiter.api.Assertions.*;

class MapWidgetTest extends TerramapTest {

    @Test
    void canSaveMapWidgetToSavedMapState() throws InterruptedException {
        TestingWidgetContainer screen = new TestingWidgetContainer(60, 500f, 500f);
        MapWidget map = new MapWidget(0f, 0f, 0,  500f, 500f, MapContext.FULLSCREEN, 1f);
        screen.addWidget(map);
        RasterMapLayer raster_osm = (RasterMapLayer) map.createLayer(RASTER_LAYER_ID);
        RasterMapLayer raster_stamen = (RasterMapLayer) map.createLayer(RASTER_LAYER_ID);
        raster_osm.setTiledMap(MapStylesLibrary.getBaseMaps().get("osm"));
        map.setLayerZ(raster_osm, -2);
        raster_stamen.setTiledMap(MapStylesLibrary.getBaseMaps().get("stamen_terrain"));
        map.setLayerZ(raster_stamen, -1);
        raster_stamen.setAlpha(0.5f);
        raster_stamen.setRenderingOffset(new Vec2dImmutable(0.042d, -0.24d));
        raster_stamen.setRotationOffset(15f);

        map.getController().setZoom(10, false);
        map.getController().setRotation(45f, false);
        screen.doTick();

        map.getController().moveLocationToCenter(PARIS, false);
        screen.doTick();

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

        SavedLayerState savedStamen = saved.layers.get(1);
        assertEquals(RASTER_LAYER_ID, savedStamen.type);
        assertEquals(-1, savedStamen.z);
        assertEquals(0.5f, savedStamen.alpha);
        assertEquals(0, savedStamen.cartesianOffset.subtract(0.042d, -0.24d).norm(), 1e-5d);
        assertEquals(15f, savedStamen.rotationOffset, 1e-5f);

    }

    @Test
    public void canRestoreMapState() throws InterruptedException {

        TestingWidgetContainer screen = new TestingWidgetContainer(60, 500f, 500f);
        MapWidget map = new MapWidget(0f, 0f, 0,  500f, 500f, MapContext.FULLSCREEN, 1f);
        screen.moveMouse(750, 750, 1000);
        screen.addWidget(map);

        // Let's start in some random state
        RasterMapLayer rasterLayer = (RasterMapLayer) map.createLayer(RASTER_LAYER_ID);
        rasterLayer.setTiledMap(MapStylesLibrary.getBaseMaps().get("osm"));
        map.setLayerZ(rasterLayer, -1);
        map.getController().setZoomStaticLocation(PARIS);
        map.getController().setZoom(18, true);
        screen.runFor(1000);
        map.getController().moveLocationToCenter(PARIS, true);
        screen.runFor(1000);
        map.getController().setRotationStaticLocation(PARIS);
        map.getController().setRotation(45f, true);

        // A completely different saved state
        SavedMapState state = new SavedMapState();
        state.center.set(GeoPointImmutable.ORIGIN);
        state.zoom = 0;
        state.rotation = 0;
        SavedLayerState layerState = new SavedLayerState();
        layerState.type = RASTER_LAYER_ID;
        layerState.settings.add("style", new JsonPrimitive("stamen_terrain"));
        state.layers.add(layerState);

        // And we try restoring...
        map.restore(state);
        screen.doTick();

        assertEquals(GeoPointImmutable.ORIGIN, map.getController().getCenterLocation().getImmutable());
        assertEquals(0, map.getController().getZoom());
        assertEquals(0, map.getController().getRotation());
        assertEquals(2, map.getLayers().size()); // Input layer and raster layer
        MapLayer layer = map.getLayers().get(1);
        assertTrue(layer instanceof RasterMapLayer);
        assertEquals("stamen_terrain", ((RasterMapLayer) layer).getTiledMap().getId());

    }

}