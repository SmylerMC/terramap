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
import net.smyler.terramap.util.geo.GeoPointImmutable;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;

import static net.smyler.smylib.Color.WHITE;

public abstract class AbstractLivingMarker extends AbstractMovingMarker {

    private final @NotNull Sprite sprite;
    protected final @NotNull Entity entity;
    protected GeoPointImmutable actualLocation;
    protected float actualAzimuth;

    public AbstractLivingMarker(MarkerController<?> controller, @NotNull Sprite sprite, @NotNull Entity entity) {
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
        double x = this.entity.posX;
        double z = this.entity.posZ;
        GeographicProjection proj = TerramapClientContext.getContext().getProjection();
        try {
            this.actualLocation = new GeoPointImmutable(proj.toGeo(x, z));
            this.actualAzimuth = proj.azimuth(x, z, this.entity.rotationYaw);
        } catch(OutOfProjectionBoundsException | NullPointerException e) {
            this.actualLocation = null;
            this.actualAzimuth = Float.NaN;
        }
        super.onUpdate(mouseX, mouseY, parent);
        if(this.entity.isDead) parent.scheduleBeforeNextUpdate(() -> map.removeMarker(this));
    }

    @Override
    protected GeoPointImmutable getActualLocation() throws OutOfProjectionBoundsException {
        if (this.actualLocation == null) throw OutOfProjectionBoundsException.get();
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
    public ITextComponent getDisplayName() {
        return this.entity.getDisplayName();
    }

    @Override
    public String getIdentifier() {
        return this.getControllerId() + ":" + this.entity.getUniqueID();
    }

    @Override
    public float getActualAzimuth() throws OutOfProjectionBoundsException {
        if (Double.isNaN(this.actualAzimuth)) throw OutOfProjectionBoundsException.get();
        return this.actualAzimuth;
    }

    @NotNull
    public final Sprite getSprite() {
        return this.sprite;
    }

}
