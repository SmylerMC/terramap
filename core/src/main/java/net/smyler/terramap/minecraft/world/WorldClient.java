package net.smyler.terramap.minecraft.world;

import net.smyler.terramap.geo.GeoProjection;

import java.util.Optional;
import java.util.UUID;

public abstract class WorldClient implements World {

    private UUID uuid;
    private GeoProjection projection;

    @Override
    public Optional<UUID> uuid() {
        return Optional.ofNullable(this.uuid);
    }

    @Override
    public Optional<GeoProjection> projection() {
        return Optional.ofNullable(this.projection);
    }

    public void setUuidFromServer(UUID uuid) {
        this.uuid = uuid;
    }

    public void setProjectionFromServer(GeoProjection projection) {
        this.projection = projection;
    }

}
