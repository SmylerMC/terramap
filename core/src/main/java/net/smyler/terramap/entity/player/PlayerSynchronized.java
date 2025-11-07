package net.smyler.terramap.entity.player;

import net.smyler.smylib.text.Text;
import net.smyler.terramap.geo.GeoPoint;
import net.smyler.terramap.geo.GeoPointMutable;
import net.smyler.terramap.geo.GeoPointView;
import net.smyler.terramap.geo.OutOfGeoBoundsException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static java.lang.Float.isFinite;
import static java.util.Objects.requireNonNull;
import static net.smyler.smylib.Preconditions.checkArgument;

/**
 * A player than may not be known by the local Minecraft server,
 * and whose information is being synchronized by Terramap.
 *
 * @author Smyler
 */
public class PlayerSynchronized implements Player {

    private final UUID uuid;
    private Text displayName;
    private final GeoPointMutable location = new GeoPointMutable();
    private float azimuth;
    private boolean outOfProjection = true;
    private GameMode gamemode = GameMode.UNSUPPORTED;

    /**
     * Constructs a new {@link PlayerSynchronized} with a UUID and a display name.
     * The player is initialized as outside the projection and with an unsupported game mode.
     *
     * @param uuid  the player's UUID, which cannot be changed later for this object
     * @param name  the player's initial display name
     *
     * @throws NullPointerException is either argument is null
     */
    public PlayerSynchronized(@NotNull UUID uuid, @NotNull Text name) {
        this.uuid = requireNonNull(uuid);
        this.displayName = requireNonNull(name);
    }

    /**
     * Synchronized this player's state with the given player's state,
     * if they have the same UUID.
     * If they do not, this method does nothing.
     *
     * @param player the player to update from
     */
    public void syncWith(@Nullable Player player) {
        if (player == null) {
            return;
        }
        if (!this.uuid.equals(player.uuid())) {
            return;
        }
        this.setDisplayName(player.displayName());
        this.setGameMode(player.gameMode());
        try {
            this.setLocationAndAzimuth(player.location(), player.azimuth());
        } catch (OutOfGeoBoundsException e) {
            this.setOutOfProjection();
        }
    }

    @Override
    public @NotNull UUID uuid() {
        return this.uuid;
    }

    @Override
    public @NotNull Text displayName() {
        return this.displayName;
    }

    /**
     * Changes this player's display name.
     *
     * @param displayName the new display name
     *
     * @throws NullPointerException if the new display name is null
     */
    public void setDisplayName(@NotNull Text displayName) {
        this.displayName = requireNonNull(displayName);
    }
    
    @Override
    public @NotNull GeoPointView location() throws OutOfGeoBoundsException {
        if (this.outOfProjection) {
            throw new OutOfGeoBoundsException("Player out of projection");
        }
        return this.location.getReadOnlyView();
    }

    /**
     * Sets this player geographic information.
     * Marks this projection as being inside the projection bounds.
     *
     * @param location  a location to update this player's location with
     * @param azimuth   the azimuth to set, must be finite
     *
     * @throws NullPointerException     if the given {@link GeoPoint} is null
     * @throws IllegalArgumentException if the given azimuth is not a finite float
     */
    public void setLocationAndAzimuth(@NotNull GeoPoint location, float azimuth) {
        requireNonNull(location);
        checkArgument(isFinite(azimuth), "azimuth must be finite");
        this.location.set(location);
        this.azimuth = azimuth;
        this.outOfProjection = false;
    }

    @Override
    public float azimuth() {
        return this.azimuth;
    }

    /**
     * Marks this player as being outside the projection bounds.
     * Subsequent calls to {@link #location()}  will fail with {@link OutOfGeoBoundsException}
     * until {@link #setLocationAndAzimuth(GeoPoint, float)} gets called to reset the state.
     */
    public void setOutOfProjection() {
        this.outOfProjection = true;
    }

    /**
     * Sets this player's game mode.
     *
     * @param mode the new game mode
     * @throws NullPointerException if game mode is null
     */
    public void setGameMode(@NotNull GameMode mode) {
        requireNonNull(mode);
        this.gamemode = mode;
    }

    @Override
    public @NotNull GameMode gameMode() {
        return this.gamemode;
    }

}
