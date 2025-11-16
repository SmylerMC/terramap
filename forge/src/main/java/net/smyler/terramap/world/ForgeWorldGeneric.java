package net.smyler.terramap.world;

import fr.thesmyler.terramap.util.TerramapUtil;
import net.minecraft.world.WorldServer;
import net.smyler.terramap.geo.GeoProjection;

import java.util.Optional;
import java.util.UUID;

/**
 * A generic {@link net.minecraft.world.World} wrapper,
 * to be used as a fallback for worlds that are neither {@link WorldServer} or {@link net.minecraft.client.multiplayer.WorldClient}.
 * This *should* only happen with unsupported modded worlds,
 * as {@link WorldServer} and {@link net.minecraft.client.multiplayer.WorldClient} are the only two vanilla classes
 * inheriting from {@link net.minecraft.world.World}.
 *
 * @author Smyler
 */
public class ForgeWorldGeneric implements World {

    private final net.minecraft.world.World world;

    ForgeWorldGeneric(net.minecraft.world.World world) {
        this.world = world;
    }

    @Override
    public Optional<UUID> uuid() {
        return Optional.empty();
    }

    @Override
    public Optional<GeoProjection> projection() {
        // This is likely to fail, but let's try anyway
        return Optional.ofNullable(TerramapUtil.getWorldProjection(this.world));
    }

}
