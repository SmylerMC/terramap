package net.smyler.terramap.world;

import fr.thesmyler.terramap.saving.server.TerramapServerPreferences;
import fr.thesmyler.terramap.util.TerramapUtil;
import net.minecraft.world.WorldServer;
import net.smyler.terramap.geo.GeoProjection;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

public class ForgeWorldServerside implements WorldServerside {

    private final WorldServer world;

    ForgeWorldServerside(WorldServer world) {
        this.world = world;
    }

    @Override
    public @NotNull Path saveDirectory() {
        return this.world.getSaveHandler().getWorldDirectory().toPath();
    }

    @Override
    public Optional<UUID> uuid() {
        return Optional.of(TerramapServerPreferences.getWorldUUID(this.world));
    }

    @Override
    public Optional<GeoProjection> projection() {
        return Optional.ofNullable(TerramapUtil.getWorldProjection(this.world));
    }

}
