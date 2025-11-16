package net.smyler.terramap;

import net.smyler.terramap.entity.player.PlayerClientside;
import net.smyler.terramap.entity.player.PlayerClientsideLocal;
import net.smyler.terramap.world.ForgeWorldClientside;
import net.smyler.terramap.world.WorldClientside;

import java.util.Optional;

public class TerramapForgeClient implements TerramapClient {

    private ForgeWorldClientside world;

    @Override
    public Optional<WorldClientside> world() {
        return Optional.ofNullable(this.world);
    }

    @Override
    public Optional<PlayerClientsideLocal> mainPlayer() {
        return Optional.empty();
    }

    public void setWorld(ForgeWorldClientside world) {
        this.world = world;
    }

}
