package net.smyler.terramap.entity.player;

import net.smyler.smylib.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public abstract class PlayerClientsideSynchronized extends PlayerSynchronized implements PlayerClientside {

    public PlayerClientsideSynchronized(@NotNull UUID uuid, @NotNull Text name) {
        super(uuid, name);
    }

}
