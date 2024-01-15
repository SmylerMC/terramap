package fr.thesmyler.terramap.gui.widgets.map;

import fr.thesmyler.smylibgui.container.TestingWidgetContainer;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapTest;
import net.smyler.smylib.math.Vec2dMutable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.thesmyler.terramap.util.geo.GeoPointImmutable.ORIGIN;
import static fr.thesmyler.terramap.util.geo.GeoPointTest.PARIS;
import static fr.thesmyler.terramap.Assertions.assertEquals;

public class MapControllerTest extends TerramapTest {

    private TestingWidgetContainer screen;
    private MapWidget map;
    private MapController controller;

    @BeforeEach
    public void setupMap() {
        this.screen = new TestingWidgetContainer(60, 1280f, 720f);
        this.map = new MapWidget(-4f, -2f, 10,  141f, 83f, MapContext.MINIMAP, 2.0f);
        this.screen.addWidget(this.map);
        this.controller = map.getController();
    }


    @Test
    public void zoomingWithoutAnimationKeepsStaticLocationStatic() {

        // Same center and static location, zoom in
        this.controller.setZoom(0, false);
        this.controller.moveLocationToCenter(ORIGIN, false);
        this.controller.setZoomStaticLocation(ORIGIN);
        this.controller.zoom(5, false);
        assertEquals(ORIGIN, this.controller.getCenterLocation(), 1e-3);

        // Close enough points, zoom in
        this.controller.setZoom(0, false);
        this.controller.moveLocationToCenter(PARIS, false);
        this.controller.moveMap(30, 30, false);
        this.controller.setZoomStaticLocation(PARIS);
        Vec2dMutable originalPosition = new Vec2dMutable();
        this.map.getInputLayer().getPositionOnWidget(originalPosition, PARIS);
        this.controller.setZoom(18, false);
        Vec2dMutable newPosition = new Vec2dMutable();
        this.map.getInputLayer().getPositionOnWidget(newPosition, PARIS);
        assertEquals(originalPosition, newPosition, 1e-3);

        // Close enough points, zoom out
        this.controller.setZoom(25, false);
        this.controller.moveLocationToCenter(PARIS, false);
        this.controller.moveMap(30, 30, false);
        this.controller.setZoomStaticLocation(PARIS);
        originalPosition = new Vec2dMutable();
        this.map.getInputLayer().getPositionOnWidget(originalPosition, PARIS);
        this.controller.setZoom(0, false);
        newPosition = new Vec2dMutable();
        this.map.getInputLayer().getPositionOnWidget(newPosition, PARIS);
        assertEquals(originalPosition, newPosition, 1e-3);

    }

    @Test
    public void extremelyFarStaticZoomLocationsDoNotCauseFloatingPointOverflow() {
        this.controller.setZoom(25, false);
        this.controller.setRotation(246f, false);
        this.controller.moveLocationToCenter(PARIS, false);
        this.controller.setZoomStaticLocation(ORIGIN);
        this.controller.setRotationStaticLocation(ORIGIN);

        this.controller.setRotation(0, false);
        this.controller.setZoom(0, false);
        this.controller.moveLocationToCenter(ORIGIN, false);
    }

}
