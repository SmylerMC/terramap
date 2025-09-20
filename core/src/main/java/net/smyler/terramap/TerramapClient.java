package net.smyler.terramap;

import net.smyler.terramap.content.World;
import net.smyler.terramap.content.WorldClient;
import net.smyler.terramap.util.geo.GeoProjection;

import java.util.Optional;

public interface TerramapClient {
    Optional<WorldClient> world();

    default Optional<GeoProjection> projection() {
        return world().flatMap(World::projection);
    }

}
