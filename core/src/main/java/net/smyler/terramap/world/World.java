package net.smyler.terramap.world;

import net.smyler.terramap.util.geo.GeoProjection;

import java.util.Optional;
import java.util.UUID;

public interface World {

    Optional<UUID> uuid();

    Optional<GeoProjection> projection();

}
