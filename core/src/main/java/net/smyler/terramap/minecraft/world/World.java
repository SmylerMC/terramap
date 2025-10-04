package net.smyler.terramap.minecraft.world;

import net.smyler.terramap.geo.GeoProjection;

import java.util.Optional;
import java.util.UUID;

public interface World {

    Optional<UUID> uuid();

    Optional<GeoProjection> projection();

}
