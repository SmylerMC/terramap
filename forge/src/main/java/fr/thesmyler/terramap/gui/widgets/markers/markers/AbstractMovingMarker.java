package fr.thesmyler.terramap.gui.widgets.markers.markers;

import net.smyler.smylib.Animation;

import net.smyler.terramap.geo.OutOfGeoBoundsException;
import net.smyler.terramap.geo.point.GeoPoint;
import net.smyler.terramap.geo.point.GeoPointMutable;
import net.smyler.terramap.geo.point.GeoPointView;

import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;

public abstract class AbstractMovingMarker extends Marker {

    protected final Animation movingAnimation;

    private final GeoPointMutable location = new GeoPointMutable();
    private boolean isOutOfBounds = false;
    protected float azimuth;

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
            this.location.set(this.getActualLocation());
            this.azimuth = this.getActualAzimuth();
            this.isOutOfBounds = false;
        } catch (OutOfGeoBoundsException e) {
            this.isOutOfBounds = true;
        }

    }

    protected abstract GeoPoint getActualLocation() throws OutOfGeoBoundsException;

    @Override
    public GeoPointView getLocation() {
        return this.isOutOfBounds ? null : this.location.getReadOnlyView();
    }

    public float getAzimuth() {
        return this.isOutOfBounds ? Float.NaN : this.azimuth;
    }

    protected abstract float getActualAzimuth() throws OutOfGeoBoundsException;

}
