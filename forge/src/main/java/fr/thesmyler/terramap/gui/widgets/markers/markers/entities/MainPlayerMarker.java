package fr.thesmyler.terramap.gui.widgets.markers.markers.entities;

import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.containers.WidgetContainer;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import net.smyler.smylib.text.ImmutableText;
import net.smyler.smylib.text.Text;
import net.smyler.terramap.TerramapClient;
import net.smyler.terramap.entity.player.Player;
import net.smyler.terramap.entity.player.PlayerClientside;
import net.smyler.terramap.geo.GeoPoint;
import net.smyler.terramap.geo.OutOfGeoBoundsException;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;

import static net.smyler.smylib.SmyLib.getGameClient;
import static net.smyler.terramap.Terramap.getTerramapClient;

/**
 * A marker for the "main" player of this game client
 * (i.e. the player currently playing the game on this client).
 * <br>
 * Does not hold any internal state and uses {@link TerramapClient#mainPlayer()},
 * so what gets displayed is always accurate.
 * 
 * @author Smyler
 */
public class MainPlayerMarker extends AbstractPlayerMarker {

    public MainPlayerMarker(MarkerController<?> controller, int downscaleFactor) {
        super(controller, downscaleFactor);
    }

    @Override
    public void onUpdate(float mouseX, float mouseY, WidgetContainer parent) {
        Optional<PlayerClientside> playerOptional = getTerramapClient().mainPlayer();
        if (!playerOptional.isPresent()) {
            parent.scheduleBeforeNextUpdate(() -> parent.removeWidget(this));
            return;
        }
        super.onUpdate(mouseX, mouseY, parent);
    }

    @Override
    protected ResourceLocation getSkin() {
        Identifier id = getTerramapClient().mainPlayer()
                .map(PlayerClientside::skin)
                .orElse(getGameClient().sprites().getSprite("minecraft:player_skin_wide_steve").texture);
        return new ResourceLocation(id.namespace, id.path);
    }

    @Override
    protected GeoPoint getActualLocation() throws OutOfGeoBoundsException {
        return getTerramapClient().mainPlayer().map(Player::location).orElseThrow(OutOfGeoBoundsException::new);
    }

    @Override
    protected float getTransparency() {
        return 1f;
    }

    @Override
    public Text getDisplayName() {
        return getTerramapClient().mainPlayer()
                .map(Player::displayName)
                .orElseGet(() -> ImmutableText.ofPlainText("Missing main player"));
    }

    @Override
    public String getIdentifier() {
        return this.getControllerId() + ":" + getTerramapClient().mainPlayer()
                .map(Player::uuid)
                .map(Object::toString)
                .orElse("mainPlayer");
    }

    @Override
    protected float getActualAzimuth() throws OutOfGeoBoundsException {
        return getTerramapClient().mainPlayer().map(Player::azimuth).orElseThrow(OutOfGeoBoundsException::new);
    }

}
