package fr.thesmyler.terramap.gui.widgets.markers.markers.entities;

import net.smyler.smylib.gui.GlState;
import net.smyler.smylib.gui.containers.WidgetContainer;
import net.smyler.smylib.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.markers.AbstractMovingMarker;
import net.smyler.smylib.gui.DrawContext;
import net.smyler.terramap.Terramap;
import net.smyler.terramap.util.geo.GeoPointImmutable;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import static net.smyler.smylib.Color.WHITE;

public abstract class AbstractLivingMarker extends AbstractMovingMarker {

    protected static final ResourceLocation ENTITY_MARKERS_TEXTURE = new ResourceLocation(Terramap.MOD_ID, "textures/gui/entity_markers.png");

    protected ResourceLocation texture;
    protected int u, v, textureWidth, textureHeight;
    protected final Entity entity;
    protected GeoPointImmutable actualLocation;
    protected float actualAzimuth;

    public AbstractLivingMarker(MarkerController<?> controller, float width, float height, ResourceLocation texture, int u, int v, int textureWidth, int textureHeight, Entity entity) {
        super(controller, width, height, 16, Integer.MAX_VALUE);
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.entity = entity;
    }

    @Override
    public void draw(DrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        GlState glState = context.glState();
        boolean drawName = hovered;
        if(parent instanceof MapWidget) {
            MapWidget map = (MapWidget) parent;
            drawName = drawName && !map.getContext().equals(MapContext.MINIMAP);
        }
        glState.enableAlpha();
        if(hovered) context.drawRectangle(x +1, y +1, x + 1 + this.width, y + 1 + this.height, Color.LIGHT_OVERLAY);
        Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
        glState.setColor(WHITE);
        GlStateManager.enableBlend();
        RenderUtil.drawModalRectWithCustomSizedTexture(x, y, this.u, this.v, this.width, this.height, this.textureWidth, this.textureHeight);

        if(drawName) {
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



}
