package net.smyler.terramap.entity.player;

import java.util.UUID;

import fr.thesmyler.terramap.TerramapMod;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameType;
import net.smyler.smylib.text.Text;
import net.smyler.terramap.entity.EntityPosition;
import net.smyler.terramap.geo.GeoPointMutable;
import net.smyler.terramap.geo.GeoPointView;
import net.smyler.terramap.geo.GeoProjection;
import net.smyler.terramap.geo.OutOfGeoBoundsException;
import net.smyler.terramap.world.Position;
import net.smyler.terramap.world.PositionImmutable;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;

import static fr.thesmyler.terramap.util.TerramapUtil.getWorldProjection;
import static net.smyler.terramap.Terramap.getTerramap;
import static net.smyler.terramap.Terramap.getTerramapClient;

public class PlayerLocalForge implements PlayerLocal {

    private final EntityPlayer player;
    private final Position position;
    private final GeoPointMutable location = new GeoPointMutable();

    public PlayerLocalForge(@NotNull EntityPlayer player) {
        this.player = player;
        this.position = new EntityPosition(player);
    }

    @Override
    public @NotNull UUID uuid() {
        return this.player.getPersistentID();
    }

    @Override
    public @NotNull Text displayName() {
        ITextComponent mcName = this.player == null ? new TextComponentString("Missing main player"): player.getDisplayName();
        String nameJson = ITextComponent.Serializer.componentToJson(mcName);
        return getTerramap().gson().fromJson(nameJson, Text.class);
    }

    @Override
    public @NotNull GeoPointView location() throws OutOfGeoBoundsException {
        GeoProjection projection;
        if (this.player.world.isRemote) {
            projection = getTerramapClient().projection().orElse(null);
        } else {
            projection = getWorldProjection(this.player.world);
        }
        if (projection == null) {
            return null;
        }
        Position position = new PositionImmutable(this.player.posX, this.player.posY, this.player.posZ);
        projection.toGeo(this.location, position);
        return this.location.getReadOnlyView();
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }

    @Override
    public @NotNull GameMode gameMode() {
        return fromForgeGameType(TerramapMod.proxy.getGameMode(this.player));
    }

    @Override
    public float azimuth() {
        GeoProjection projection;
        if (this.player.world.isRemote) {
            projection = getTerramapClient().projection().orElse(null);
        } else {
            projection = getWorldProjection(this.player.world);
        }
        if (projection == null) {
            return Float.NaN;
        }
        Position position = new PositionImmutable(this.player.posX, this.player.posY, this.player.posZ);
        projection.toGeo(this.location, position);
        try{
            return projection.azimuth(position);
        } catch(OutOfGeoBoundsException e) {
            return 0f;
        }
    }

    public static GameMode fromForgeGameType(GameType gameType) {
        switch (gameType) {
            case SURVIVAL:
                return GameMode.SURVIVAL;
            case CREATIVE:
                return GameMode.CREATIVE;
            case ADVENTURE:
                return GameMode.ADVENTURE;
            case SPECTATOR:
                return GameMode.SPECTATOR;
            default:
                return GameMode.UNSUPPORTED;
        }
    }

    @Override
    public Position position() {
        return this.position;
    }
}
