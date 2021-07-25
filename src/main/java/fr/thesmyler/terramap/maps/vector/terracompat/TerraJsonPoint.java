package fr.thesmyler.terramap.maps.vector.terracompat;

import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.terramap.maps.vector.features.Point;
import fr.thesmyler.terramap.util.geo.GeoPoint;
import fr.thesmyler.terramap.util.geo.WebMercatorUtil;
import fr.thesmyler.terramap.util.math.DoubleRange;
import net.buildtheearth.terraplusplus.dataset.geojson.object.Feature;

public class TerraJsonPoint extends TerraJsonVectorFeature implements Point {
    
    private GeoPoint point;
        
    public TerraJsonPoint(net.buildtheearth.terraplusplus.dataset.geojson.geometry.Point deleguate, Feature feature) {
        super(feature);
        this.point = new GeoPoint(deleguate.lon(), deleguate.lat());
    }

    @Override
    public DoubleRange getZoomRange() {
        return WebMercatorUtil.ZOOM_RANGE;
    }

    @Override
    public GeoPoint getPosition() {
        return this.point;
    }

    @Override
    public Color getColor() {
        return Color.RED; //TODO proper color for GeoJson points
    }

    @Override
    public String getIconName() {
        // TODO proper icons for GeoJson points
        return null;
    }

}
