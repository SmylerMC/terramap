package net.smyler.terramap.world;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.world.WorldServer;
import net.smyler.terramap.util.collections.HashMapBuilder;

import java.util.Map;

import static net.smyler.terramap.Terramap.getTerramap;

/**
 * Utility class that holds world weak references,
 * to help consistently wrap vanilla worlds into Terramap worlds.
 * <br>
 * This base class is intended to be used in dedicated servers.
 *
 * @see ForgeWorldCacheClientside
 *
 * @author Smyler
 */
public class ForgeWorldCache {

    private final LoadingCache<net.minecraft.world.World, ForgeWorldGeneric> genericCache = CacheBuilder.newBuilder()
            .weakKeys()
            .weakValues()
            .recordStats()
            .build(CacheLoader.from(ForgeWorldGeneric::new));

    private final LoadingCache<WorldServer, ForgeWorldServerside> serverCache = CacheBuilder.newBuilder()
            .weakKeys()
            .weakValues()
            .recordStats()
            .build(CacheLoader.from(ForgeWorldServerside::new));

    /**
     * Given a vanilla {@link net.minecraft.world.World}, returns its Terramap wrapper.
     * If the world had no existing wrappers, wraps it and adds it to the internal weak cache.
     * If it did have an existing wrapper in the cache, return it.
     * The {@link World} implementation returned is chosen from the class of the vanilla world.
     *
     * @param world the vanilla world to get a wrapper for
     * @return the Terramap world
     */
    public World getTerraWorld(net.minecraft.world.World world) {
        if (world instanceof WorldServer) {
            return getTerraWorld((WorldServer) world);
        }
        getTerramap().logger().warn("Resorted to generic forge world wrapper");
        return this.genericCache.getUnchecked(world);
    }

    /**
     * Given a vanilla {@link WorldServer}, returns its Terramap {@link WorldServerside} wrapper.
     * If the world had no existing wrappers, wraps it and adds it to the internal weak cache.
     * If it did have an existing wrapper in the cache, return it.
     *
     * @param world the vanilla server world to get a wrapper for
     * @return the Terramap world
     */
    public ForgeWorldServerside getTerraWorld(WorldServer world) {
        return this.serverCache.getUnchecked(world);
    }

    public Map<String, Long> stats() {
        return new HashMapBuilder<String, Long>()
                .put("generic", this.genericCache.size())
                .put("server", this.serverCache.size())
                .build();
    }

}
