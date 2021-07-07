package fr.thesmyler.terramap.gui.widgets.markers.markers;

import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import fr.thesmyler.terramap.util.geo.GeoPoint;

public abstract class AbstractFixedMarker extends Marker {

    private GeoPoint location;

    public AbstractFixedMarker(MarkerController<?> controller, float width, float height, GeoPoint location) {
        super(controller, width, height);
        this.location = location;
    }

    @Override
    public GeoPoint getLocation() {
        return this.location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

}
