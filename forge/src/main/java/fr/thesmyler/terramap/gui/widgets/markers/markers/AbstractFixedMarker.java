package fr.thesmyler.terramap.gui.widgets.markers.markers;

import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import net.smyler.terramap.geo.GeoPoint;
import net.smyler.terramap.geo.GeoPointMutable;
import net.smyler.terramap.geo.GeoPointView;

public abstract class AbstractFixedMarker extends Marker {

    private final GeoPointMutable location = new GeoPointMutable();

    public AbstractFixedMarker(MarkerController<?> controller, float width, float height, GeoPoint location) {
        super(controller, width, height);
        this.location.set(location);
    }

    @Override
    public GeoPointView getLocation() {
        return this.location.getReadOnlyView();
    }

    public void setLocation(GeoPoint location) {
        this.location.set(location);
    }

}
