package net.smyler.terramap;

import net.smyler.terramap.world.ForgeWorldClientside;
import net.smyler.terramap.world.WorldClientside;

import java.util.Optional;

public class TerramapForgeClient implements TerramapClient {

    private ForgeWorldClientside world;

    @Override
    public Optional<WorldClientside> world() {
        return Optional.ofNullable(this.world);
    }

    public void setWorld(ForgeWorldClientside world) {
        this.world = world;
    }

}
