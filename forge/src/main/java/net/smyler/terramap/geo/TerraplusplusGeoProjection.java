package net.smyler.terramap.geo;

import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.smyler.terramap.world.Position;
import net.smyler.terramap.world.PositionMutable;
import org.jetbrains.annotations.NotNull;

public class TerraplusplusGeoProjection implements GeoProjection {

    private final GeographicProjection wrapped;

    public TerraplusplusGeoProjection(GeographicProjection projection) {
        this.wrapped = projection;
    }

    @Override
    public void toGeo(@NotNull GeoPointMutable location, @NotNull Position position) {
        try {
            double[] result = this.wrapped.toGeo(position.x(), position.z());
            location.set(result[0], result[1]);
        } catch (OutOfProjectionBoundsException e) {
            throw new OutOfGeoBoundsException("Position " + position + " is out of bounds for the current projection", e);
        }
    }

    @Override
    public void fromGeo(@NotNull PositionMutable position, @NotNull GeoPoint location) {
        try {
            double[] result = this.wrapped.fromGeo(location.longitude(), location.latitude());
            position.setXZ(result[0], result[1]);
        } catch (OutOfProjectionBoundsException e) {
            throw new OutOfGeoBoundsException("Location " + position + " is out of bounds for the current projection", e);
        }
    }

    @Override
    public float azimuth(@NotNull Position position) throws OutOfGeoBoundsException {
        try {
            return this.wrapped.azimuth(position.x(), position.z(), position.yaw());
        } catch (OutOfProjectionBoundsException e) {
            throw new OutOfGeoBoundsException("Position " + position + " is out of bounds for the current projection", e);
        }
    }

    @Override
    public void tissot(@NotNull TissotsIndicatrix indicatrix, @NotNull GeoPoint location) throws OutOfGeoBoundsException {
        try {
            double[] result = this.wrapped.tissot(location.longitude(), location.latitude());
            indicatrix.set(result[0], result[1], result[3], result[2]);
        } catch (OutOfProjectionBoundsException e) {
            throw new OutOfGeoBoundsException("Location " + location + " is out of bounds for the current projection", e);
        }

    }

}
