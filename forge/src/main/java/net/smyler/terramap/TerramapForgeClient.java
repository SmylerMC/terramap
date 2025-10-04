package net.smyler.terramap;

import net.smyler.terramap.minecraft.world.ForgeWorldClient;
import net.smyler.terramap.minecraft.world.WorldClient;

import java.util.Optional;

public class TerramapForgeClient implements TerramapClient {

    private ForgeWorldClient world;

    @Override
    public Optional<WorldClient> world() {
        return Optional.ofNullable(this.world);
    }

    public void setWorld(ForgeWorldClient world) {
        this.world = world;
    }

}
