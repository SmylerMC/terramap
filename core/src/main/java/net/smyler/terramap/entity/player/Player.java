package net.smyler.terramap.entity.player;

import java.util.UUID;

import net.smyler.smylib.text.Text;
import net.smyler.terramap.geo.GeoPoint;
import net.smyler.terramap.geo.OutOfGeoBoundsException;
import org.jetbrains.annotations.NotNull;

import static net.smyler.terramap.entity.player.GameMode.SPECTATOR;

/**
 * A Minecraft player.
 *
 * @author Smyler
 */
public interface Player {

    /**
     * Minecraft player are identified by a unique account ID,
     * persistent across game sessions.
     *
     * @return the player's UUID
     */
    @NotNull UUID uuid();

    /**
     * The player's display name.
     * This may change at any time and is purely cosmetic.
     * It may not match the player's game profile name.
     *
     * @return the player's display name
     */
    @NotNull Text displayName();

    /**
     * This player's current geographical location.
     * This may consistently return the same mutable object,
     * but there is no guarantee such an object would be updated unless this method is called.
     *
     * @return this player's geographic location
     * @throws OutOfGeoBoundsException if the player is not currently within projection bounds
     */
    @NotNull GeoPoint location() throws OutOfGeoBoundsException;

    /**
     * The azimuth this player is currently facing.
     * 
     * @return the azimuth the player is currently facing, as a finite unbounded float
     */
    float azimuth();

    /**
     * The player's current {@link GameMode}.
     *
     * @return the player's game mode
     */
    @NotNull GameMode gameMode();

    /**
     * Whether the player's current game mode is {@link GameMode#SPECTATOR}
     *
     * @return whether the player is spectating the game.
     */
    default boolean isSpectator() {
        return this.gameMode().equals(SPECTATOR);
    }

}
