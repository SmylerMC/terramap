package net.smyler.terramap;

import net.smyler.terramap.world.World;
import net.smyler.terramap.world.WorldClientside;
import net.smyler.terramap.geo.GeoProjection;

import java.util.Optional;

public interface TerramapClient {
    Optional<WorldClientside> world();

    default Optional<GeoProjection> projection() {
        return world().flatMap(World::projection);
    }

}
