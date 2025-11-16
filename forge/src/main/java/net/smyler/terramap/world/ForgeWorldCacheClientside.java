package net.smyler.terramap.world;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.WorldServer;
import net.smyler.terramap.util.collections.HashMapBuilder;

import java.util.Map;

/**
 * Utility class that holds world weak references,
 * to help consistently wrap vanilla worlds into Terramap worlds.
 * <br>
 * This class supports both server and client worlds,
 * and is intended to be used in the dedicated client.
 *
 * @author Smyler
 */
public final class ForgeWorldCacheClientside extends ForgeWorldCache{

    private final LoadingCache<WorldClient, ForgeWorldClientside> clientCache = CacheBuilder.newBuilder()
            .weakKeys()
            .weakValues()
            .recordStats()
            .build(CacheLoader.from(ForgeWorldClientside::new));

    @Override
    public World getTerraWorld(net.minecraft.world.World world) {
        if (world instanceof WorldClient) {
            return this.getTerraWorld((WorldClient) world);
        }
        return super.getTerraWorld(world);
    }

    @Override
    public ForgeWorldServerside getTerraWorld(WorldServer world) {
        return super.getTerraWorld(world);
    }

    /**
     * Given a vanilla {@link WorldClient}, returns its Terramap {@link WorldClientside} wrapper.
     * If the world had no existing wrappers, wraps it and adds it to the internal weak cache.
     * If it did have an existing wrapper in the cache, return it.
     *
     * @param world the vanilla client world to get a wrapper for
     * @return the Terramap world
     */
    public ForgeWorldClientside getTerraWorld(WorldClient world) {
        return this.clientCache.getUnchecked(world);
    }

    public Map<String, Long> stats() {
        return new HashMapBuilder<String, Long>()
                .putAll(super.stats())
                .put("client", this.clientCache.size())
                .build();
    }


}
