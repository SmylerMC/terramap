package net.smyler.terramap.geo.projection;

import net.smyler.terramap.geo.OutOfGeoBoundsException;
import net.smyler.terramap.geo.TissotsIndicatrix;
import net.smyler.terramap.geo.point.GeoPoint;
import net.smyler.terramap.world.PositionMutable;

import org.jetbrains.annotations.NotNull;

/**
 * A projection of the Earth onto the Minecraft world.
 *
 * @author Smyler
 */
public interface GeoToMcProjection {

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
    void fromGeo(@NotNull PositionMutable position, @NotNull GeoPoint location) throws OutOfGeoBoundsException;

    /**
     * Computes the <a href="https://en.wikipedia.org/wiki/Tissot%27s_indicatrix">Tissot's indicatrix</a> of this projection at the given point (i.e. the distortion).
     * <br>
     * The indicatrix is not updated if an exception is thrown and the projection fails.
     *
     * @param indicatrix an indicatrix to update
     * @param location   the geographic location to compute the indicatrix at
     * @throws OutOfGeoBoundsException if the location is outside the bounds of this projection and the indicatrix cannot be computed
     */
    void tissot(@NotNull TissotsIndicatrix indicatrix, @NotNull GeoPoint location) throws OutOfGeoBoundsException;

}
