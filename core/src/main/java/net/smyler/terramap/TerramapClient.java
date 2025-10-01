package net.smyler.terramap;

import net.smyler.terramap.world.World;
import net.smyler.terramap.world.WorldClient;
import net.smyler.terramap.util.geo.GeoProjection;

import java.util.Optional;

public interface TerramapClient {
    Optional<WorldClient> world();

    default Optional<GeoProjection> projection() {
        return world().flatMap(World::projection);
    }

}
