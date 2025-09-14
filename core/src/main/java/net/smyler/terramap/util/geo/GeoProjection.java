package net.smyler.terramap.util.geo;

import net.smyler.terramap.content.Position;
import net.smyler.terramap.content.PositionMutable;
import org.jetbrains.annotations.NotNull;

/**
 * A projection of the earth onto a flat Minecraft world.
 *
 * @author Smyler
 */
public interface GeoProjection {

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
     * Projects a real world location onto a position in the Minecraft world.
     * It is expected that the implementation is in fact a (latitude, longitude) -> (x, z) function,
     * and that it therefore leaves the y, yaw and pitch components of the position as-is.
     * <br>
     * The position is not updated if an exception is thrown and the projection fails.
     *
     * @param position the position to update with the projected location
     * @param location the location to project
     * @throws OutOfGeoBoundsException when the given location is outside the bounds of the projection
     */
    void fromGeo(@NotNull PositionMutable position, @NotNull GeoPoint<?> location) throws OutOfGeoBoundsException;

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

    /**
     * Computes the <a href="https://en.wikipedia.org/wiki/Tissot%27s_indicatrix">Tissot's indicatrix</a> of this projection at the given point (i.e. the distortion).
     * <br>
     * The indicatrix is not updated if an exception is thrown and the projection fails.
     *
     * @param indicatrix an indicatrix to update
     * @param location   the geographic location to compute the indicatrix at
     * @throws OutOfGeoBoundsException if the location is outside the bounds of this projection and the indicatrix cannot be computed
     */
    void tissot(@NotNull TissotsIndicatrix indicatrix, @NotNull GeoPoint<?> location) throws OutOfGeoBoundsException;

}
