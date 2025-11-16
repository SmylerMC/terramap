package net.smyler.terramap;

import net.minecraft.client.entity.EntityPlayerSP;
import net.smyler.terramap.entity.player.ForgePlayerClientsideLocal;
import net.smyler.terramap.entity.player.PlayerClientsideLocal;
import net.smyler.terramap.world.ForgeWorldClientside;
import net.smyler.terramap.world.WorldClientside;

import java.lang.ref.WeakReference;
import java.util.Optional;

import static net.minecraft.client.Minecraft.getMinecraft;
import static net.smyler.terramap.Terramap.getTerramap;

public class TerramapForgeClient implements TerramapClient {

    private ForgeWorldClientside world;
    private WeakReference<ForgePlayerClientsideLocal> mainPlayerRef = new WeakReference<>(null);

    @Override
    public Optional<WorldClientside> world() {
        return Optional.ofNullable(this.world);
    }

    @Override
    public Optional<PlayerClientsideLocal> mainPlayer() {
        // Attempt to keep the same Terramap player instance,
        // but update it if the underlying Minecraft player instance has changed,
        // and do all of this with a weak reference so we don't keep things around for nothing
        // (the Terramap player has a ref to the mc player which may have refs to many other footprint-heavy objects).

        ForgePlayerClientsideLocal terraPlayer = this.mainPlayerRef.get();
        EntityPlayerSP mcPlayer = getMinecraft().player;

        if (mcPlayer == null) {
            if (terraPlayer != null) {
                this.mainPlayerRef = new WeakReference<>(null);
                getTerramap().logger().trace("wiped main player ref");
            }
            return Optional.empty();
        }

        if (terraPlayer == null || terraPlayer.backingObject() != mcPlayer) {
            terraPlayer = new ForgePlayerClientsideLocal(mcPlayer);
            this.mainPlayerRef = new WeakReference<>(terraPlayer);
            getTerramap().logger().trace("updated main player ref");
        }

        return Optional.of(terraPlayer);
    }

    public void setWorld(ForgeWorldClientside world) {
        this.world = world;
    }

}
