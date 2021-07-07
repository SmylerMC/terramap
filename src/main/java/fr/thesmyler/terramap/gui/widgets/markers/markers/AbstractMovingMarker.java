package fr.thesmyler.terramap.gui.widgets.markers.markers;

import fr.thesmyler.smylibgui.util.Animation;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import fr.thesmyler.terramap.util.geo.GeoPoint;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;

public abstract class AbstractMovingMarker extends Marker {

    protected Animation movingAnimation;

    protected GeoPoint location;
    protected float azimuth;
    protected GeoPoint oldLocation;

    public AbstractMovingMarker(MarkerController<?> controller, float width, float height, int minZoom, int maxZoom) {
        super(controller, width, height, minZoom, maxZoom);
        this.movingAnimation = new Animation(10000);
    }

    public AbstractMovingMarker(MarkerController<?> controller, float width, float height) {
        this(controller, width, height, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public void update(MapWidget map) {

        //TODO Animate for smoother movements
        try {
            GeoPoint location = this.getActualLocation();
            this.location = location;
        } catch(OutOfProjectionBoundsException e) {
            this.location = null;
        }
        try {
            float realAzimuth = this.getActualAzimuth();
            this.azimuth = Math.round(realAzimuth);
        } catch (OutOfProjectionBoundsException e) {
            this.azimuth = Float.NaN;
        }

    }

    protected abstract GeoPoint getActualLocation() throws OutOfProjectionBoundsException;

    @Override
    public GeoPoint getLocation() {
        return this.location;
    }

    public float getAzimuth() {
        return this.azimuth;
    }

    protected abstract float getActualAzimuth() throws OutOfProjectionBoundsException;

}
