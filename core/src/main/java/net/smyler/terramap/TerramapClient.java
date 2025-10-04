package net.smyler.terramap;

import net.smyler.terramap.minecraft.world.World;
import net.smyler.terramap.minecraft.world.WorldClient;
import net.smyler.terramap.geo.GeoProjection;

import java.util.Optional;

public interface TerramapClient {
    Optional<WorldClient> world();

    default Optional<GeoProjection> projection() {
        return world().flatMap(World::projection);
    }

}
