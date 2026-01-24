package net.smyler.terramap.geo.projection;

import net.smyler.terramap.geo.OutOfGeoBoundsException;
import net.smyler.terramap.geo.point.GeoPointMutable;
import net.smyler.terramap.world.Position;
import org.jetbrains.annotations.NotNull;

/**
 * A projection of the Minecraft world onto the Earth.
 *
 * @author Smyler
 */
public interface McToGeoProjection {

    /**
     * Projects a position in the Minecraft world onto a real world location.
     * <br>
     * The location is not updated if an exception is thrown and the projection fails.
     *
     * @param location the location to update with the projected position
     * @param position the position to project
     * @throws OutOfGeoBoundsException when the given position is outside the bounds of the projection
     */
    void toGeo(@NotNull GeoPointMutable location, @NotNull Position position) throws OutOfGeoBoundsException;

    /**
     * Reads the {@link Position#yaw() yaw} component of a Minecraft position and projects it as an <a href="https://en.wikipedia.org/wiki/Azimuth">azimuth</a> in the real world.
     * <br>
     * An azimuth is a vertically clockwise angle from the North axis.
     * <br>
     * A projection may fail to compute the azimuth of at a location that is otherwise within its bounds.
     * <br>
     * No guarantees are given regarding the bounds of the otherwise finite azimuth.
     *
     * @param position the position to project
     * @return the geographic azimuth, in degrees
     * @throws OutOfGeoBoundsException when the location is not within the bounds where the projection is able to compute an azimuth
     */
    float azimuth(@NotNull Position position) throws OutOfGeoBoundsException;

}
