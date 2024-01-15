package fr.thesmyler.terramap.gui.widgets.markers.markers;

import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import net.smyler.terramap.util.geo.GeoPoint;
import net.smyler.terramap.util.geo.GeoPointMutable;
import net.smyler.terramap.util.geo.GeoPointReadOnly;

public abstract class AbstractFixedMarker extends Marker {

    private final GeoPointMutable location = new GeoPointMutable();

    public AbstractFixedMarker(MarkerController<?> controller, float width, float height, GeoPoint<?> location) {
        super(controller, width, height);
        this.location.set(location);
    }

    @Override
    public GeoPointReadOnly getLocation() {
        return this.location.getReadOnly();
    }

    public void setLocation(GeoPoint<?> location) {
        this.location.set(location);
    }

}
