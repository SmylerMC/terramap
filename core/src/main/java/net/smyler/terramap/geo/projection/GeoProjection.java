package net.smyler.terramap.geo.projection;

/**
 * A bidirectional projection between the Earth and the Minecraft world.
 * This does not have to be bijective.
 *
 * @author Smyler
 */
public interface GeoProjection extends GeoToMcProjection, McToGeoProjection {

}
