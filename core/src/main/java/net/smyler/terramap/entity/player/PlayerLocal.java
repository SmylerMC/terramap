package net.smyler.terramap.entity.player;

import net.smyler.terramap.world.Position;
import org.jetbrains.annotations.NotNull;

/**
 * A player that exists in the current Minecraft side (server or client),
 * as opposed to a player whose information have been synchronized between server
 * and client by Terramap.
 *
 * @author Smyler
 */
public interface PlayerLocal extends Player {

    /**
     * This player's position.
     * This may always return the same object.
     *
     * @return the player's position
     */
    @NotNull Position position();

}
