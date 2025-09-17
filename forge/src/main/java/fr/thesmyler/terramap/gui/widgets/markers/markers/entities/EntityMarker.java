package fr.thesmyler.terramap.gui.widgets.markers.markers.entities;

import net.smyler.smylib.gui.UiDrawContext;
import net.smyler.smylib.gui.gl.GlContext;
import net.smyler.smylib.gui.containers.WidgetContainer;
import net.smyler.smylib.Color;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.markers.AbstractMovingMarker;
import net.smyler.smylib.gui.sprites.Sprite;
import net.smyler.smylib.text.Text;
import net.smyler.terramap.content.Position;
import net.smyler.terramap.content.PositionMutable;
import net.smyler.terramap.util.geo.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;

import static net.smyler.smylib.Color.WHITE;
import static net.smyler.terramap.Terramap.getTerramap;

/**
 * A marker for any type of entity.
 * Used by multiple marker controllers.
 *
 * @author Smyler
 */
public class EntityMarker extends AbstractMovingMarker {

    private final @NotNull Sprite sprite;
    private final @NotNull Entity entity;
    private final GeoPointMutable actualLocation = new GeoPointMutable();
    private float actualAzimuth;
    private boolean isOutOfBounds = false;

    public EntityMarker(MarkerController<?> controller, @NotNull Sprite sprite, @NotNull Entity entity) {
        super(controller, (float) sprite.width(), (float) sprite.height() ,16, Integer.MAX_VALUE);  //FIXME should not cast size
        this.entity = entity;
        this.sprite = sprite;
    }

    @Override
    public void draw(UiDrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        GlContext gl = context.gl();
        boolean drawName = hovered;
        if (parent != null) {
            drawName |= parent.getWidth() >= 500d && parent.getHeight() >= 100;
        }
        gl.enableAlpha();
        if (hovered) {
            context.drawRectangle(x +1, y +1, x + 1 + this.width, y + 1 + this.height, Color.LIGHT_OVERLAY);
        }
        gl.setColor(WHITE);

        context.drawSprite(x, y, this.sprite);

        if(drawName && parent != null) {
            String name = this.entity.getDisplayName().getFormattedText();
            float strWidth = parent.getFont().computeWidth(name);
            float nameY = y - parent.getFont().height() - 2;
            context.drawRectangle(x + this.width / 2 - strWidth / 2 - 2, y - parent.getFont().height() - 4, x + strWidth / 2 + this.width / 2 + 2, y - 1, Color.DARK_OVERLAY);
            parent.getFont().drawCentered(x + this.width / 2, nameY, name, WHITE, false);
        }
    }

    @Override
    public void onUpdate(float mouseX, float mouseY, WidgetContainer parent) {
        MapWidget map = (MapWidget) parent;
        GeoProjection projection = TerramapClientContext.getContext().getProjection();
        Position position = new PositionMutable(this.entity.posX, this.entity.posY, this.entity.posZ);
        try {
            projection.toGeo(this.actualLocation, position);
            this.actualAzimuth = projection.azimuth(position);
            this.isOutOfBounds = false;
        } catch(OutOfGeoBoundsException | NullPointerException e) {
            this.isOutOfBounds = true;
        }
        super.onUpdate(mouseX, mouseY, parent);
        if(this.entity.isDead) parent.scheduleBeforeNextUpdate(() -> map.removeMarker(this));
    }

    @Override
    protected GeoPoint getActualLocation() throws OutOfGeoBoundsException {
        if (this.isOutOfBounds) {
            throw new OutOfGeoBoundsException("Entity is currently out of the projected area");
        }
        return this.actualLocation;
    }

    @Override
    public float getDeltaX() {
        return -this.getWidth() / 2;
    }

    @Override
    public float getDeltaY() {
        return -this.getHeight() / 2;
    }

    @NotNull
    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public boolean canBeTracked() {
        return true;
    }

    @Override
    public Text getDisplayName() {
        String nameJson = ITextComponent.Serializer.componentToJson(this.entity.getDisplayName());
        return getTerramap().gson().fromJson(nameJson, Text.class);
    }

    @Override
    public String getIdentifier() {
        return this.getControllerId() + ":" + this.entity.getUniqueID();
    }

    @Override
    public float getActualAzimuth() throws OutOfGeoBoundsException {
        if (this.isOutOfBounds) {
            throw new OutOfGeoBoundsException("Entity is currently out of the projected area");
        }
        return this.actualAzimuth;
    }

}
