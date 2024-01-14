package fr.thesmyler.terramap.gui.widgets.map;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.input.KeyBindings;
import fr.thesmyler.terramap.util.geo.GeoPointReadOnly;
import fr.thesmyler.terramap.util.geo.WebMercatorUtil;
import fr.thesmyler.terramap.util.math.Mat2d;
import fr.thesmyler.terramap.util.math.Vec2dMutable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;

import static fr.thesmyler.terramap.gui.widgets.map.MapWidget.CONTROLLER_Z;
import static java.lang.Math.*;

/**
 * Processes inputs for a map and propagates them to the map controller.
 *
 * @author SmylerMC
 */
class InputLayer extends MapLayer {

    final Vec2dMutable rotatePosition = new Vec2dMutable();
    boolean isRotating = false;
    private float rotationAngleOrigin = 0f;
    private final GeoPointReadOnly mouseLocation;

    // Stuff that can be pre-computed and that's used later when drawing a polygon at the place the map rotates around.
    private static final int ROTATION_POLYGON_VERTEX_COUNT = 5;
    private static final double[] ROTATION_POLYGON_VERTICES_OUTER = new double[ROTATION_POLYGON_VERTEX_COUNT * 2];
    private static final double[] ROTATION_POLYGON_VERTICES_INNER = new double[ROTATION_POLYGON_VERTEX_COUNT * 2];
    private static final float ROTATION_POLYGON_RADIUS_OUTER = 5;
    private static final float ROTATION_POLYGON_RADIUS_INNER = 2;
    static {
        Vec2dMutable outer = new Vec2dMutable(0, -ROTATION_POLYGON_RADIUS_OUTER);
        Vec2dMutable inner = new Vec2dMutable(0, -ROTATION_POLYGON_RADIUS_INNER);
        Mat2d rot = Mat2d.forRotation(-PI*2 / ROTATION_POLYGON_VERTEX_COUNT);
        for(int i = 0; i < ROTATION_POLYGON_VERTEX_COUNT; i++) {
            ROTATION_POLYGON_VERTICES_OUTER[2*i] = outer.x;
            ROTATION_POLYGON_VERTICES_OUTER[2*i + 1] = outer.y;
            ROTATION_POLYGON_VERTICES_INNER[2*i] = inner.x;
            ROTATION_POLYGON_VERTICES_INNER[2*i + 1] = inner.y;
            outer.apply(rot);
            inner.apply(rot);
        }
    }

    public InputLayer(MapWidget map) {
        super(map);
        this.z = CONTROLLER_Z;
        this.mouseLocation = map.getMouseLocation();
    }

    @Override
    public String getId() {
        return "controller";
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {

        if(this.isRotating) {
            // If we are processing rotation input, draw pentagons at the corresponding spot
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + this.rotatePosition.x, y + this.rotatePosition.y, 0);
            RenderUtil.drawPolygon(Color.DARK_OVERLAY, ROTATION_POLYGON_VERTICES_OUTER);
            RenderUtil.drawPolygon(Color.DARK_OVERLAY, ROTATION_POLYGON_VERTICES_INNER);
            GlStateManager.popMatrix();
        }

        if (this.map.isDebugMode()) {
            double pointHalfSize = 5.0d;
            Vec2dMutable position = new Vec2dMutable();
            this.getPositionOnWidget(position, this.controller.getTargetLocation());
            RenderUtil.drawRect(
                    x + position.x - pointHalfSize, y + position.y - pointHalfSize,
                    x + position.x + pointHalfSize, y + position.y + pointHalfSize,
                    Color.BLUE);
            pointHalfSize -= .5d;
            this.getPositionOnWidget(position, this.controller.getZoomStaticLocation());
            RenderUtil.drawRect(
                    x + position.x - pointHalfSize, y + position.y - pointHalfSize,
                    x + position.x + pointHalfSize, y + position.y + pointHalfSize,
                    Color.GREEN);
            this.getPositionOnWidget(position, this.controller.getRotationStaticLocation());
            pointHalfSize -= .5d;
            RenderUtil.drawRect(
                    x + position.x - pointHalfSize, y + position.y - pointHalfSize,
                    x + position.x + pointHalfSize, y + position.y + pointHalfSize,
                    Color.YELLOW);
            pointHalfSize -= .5d;
            RenderUtil.drawRect(
                    x + this.getWidth() / 2 - pointHalfSize, y + this.getHeight() / 2 - pointHalfSize,
                    x + this.getWidth() / 2 + pointHalfSize, y + this.getHeight() / 2 + pointHalfSize,
                    Color.RED);
        }

    }

    @Override
    public boolean onClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
        this.controller.stopPanning();
        this.controller.stopRotating();
        this.controller.stopTracking();
        this.isRotating = false;
        if(this.isShortcutEnabled()) {
            this.map.teleportPlayerTo(this.map.getMouseLocation());
            if(this.map.getContext().equals(MapContext.FULLSCREEN)) {
                Minecraft.getMinecraft().displayGuiScreen(null); //TODO change this so it can work from any menu
            }
        }
        if(this.map.isRightClickMenuEnabled() && mouseButton == 1 && WebMercatorUtil.PROJECTION_BOUNDS.contains(this.mouseLocation)) {
            parent.showMenu(mouseX, mouseY, this.map.getRightClickMenu());
        }
        if(this.map.isInteractive() && mouseButton == 2 && !isRotating) {
            this.rotatePosition.set(mouseX, mouseY);
            this.controller.setRotationStaticPosition(mouseX, mouseY);
            this.isRotating = true;
            this.rotationAngleOrigin = this.controller.getRotation();
        }
        return false;
    }

    @Override
    public boolean onDoubleClick(float mouseX, float mouseY, int mouseButton, @Nullable WidgetContainer parent) {
        this.controller.stopPanning();
        this.controller.stopRotating();
        this.controller.stopTracking();
        this.isRotating = false;
        if (mouseButton != 0) {
            // We don't care about double right and middle clicks
            this.onClick(mouseX, mouseY, mouseButton, parent);
        } else if (this.map.isInteractive()) {
            if(this.map.isFocusedZoom()) this.controller.setZoomStaticPosition(mouseX, mouseY);
            this.controller.zoom(this.controller.getZoomSnapping(), true);
            this.updateViewPorts();
        }
        return false;
    }

    @Override
    public boolean onParentClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
        this.isRotating = false;
        return super.onParentClick(mouseX, mouseY, mouseButton, parent);
    }

    @Override
    public boolean onParentDoubleClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
        this.isRotating = false;
        return super.onParentDoubleClick(mouseX, mouseY, mouseButton, parent);
    }

    @Override
    public void onMouseDragged(float mouseX, float mouseY, float dX, float dY, int mouseButton, @Nullable WidgetContainer parent, long dt) {
        this.isRotating = false;
        if(this.map.isInteractive() && mouseButton == 0) {
            this.controller.dragMap(dX, dY, dt);
        }
    }

    @Override
    public void onUpdate(float mouseX, float mouseY, @Nullable WidgetContainer parent) {
        super.onUpdate(mouseX, mouseY, parent);
        // If we are currently taking rotation inputs, rotate the map
        if(this.map.isInteractive() && this.isRotating) {
            this.getPositionOnWidget(this.rotatePosition, this.controller.getRotationStaticLocation());
            if(abs(mouseX - this.rotatePosition.x) > 5f || abs(mouseY - this.rotatePosition.y) > 5f) {
                float angle = (float) toDegrees(
                        atan2(mouseY - this.rotatePosition.y, mouseX - this.rotatePosition.x)
                ) + 90f + this.rotationAngleOrigin;
                if(Float.isFinite(angle)) {
                    this.controller.setRotation(angle, false);
                }
            }
        }
    }

    @Override
    public boolean onMouseWheeled(float mouseX, float mouseY, int amount, @Nullable WidgetContainer parent) {
        if(this.map.isInteractive()) {
            this.isRotating = false;
            if(this.map.isFocusedZoom()) {
                this.controller.setZoomStaticPosition(mouseX, mouseY);
            } else {
                this.controller.setZoomStaticLocation(this.controller.getCenterLocation());
            }
            double zoom = copySign(1, amount) * this.controller.getZoomSnapping();
            this.controller.zoom(zoom, true);
            this.updateViewPorts();
        }
        return false;
    }

    @Override
    public String getTooltipText() {
        return isShortcutEnabled() ? SmyLibGui.getTranslator().format("terramap.mapwidget.shortcuts.tp"): "";
    }

    @Override
    public long getTooltipDelay() {
        return 0;
    }

    @Override
    public MapLayer copy(MapWidget mapFor) {
        throw new UnsupportedOperationException("Cannot copy a map input layer");
    }

    @Override
    public String name() {
        return "Input";
    }

    @Override
    public String description() {
        return "Input";
    }

    private boolean isShortcutEnabled() {
        return this.map.isInteractive() && Keyboard.isKeyDown(KeyBindings.MAP_SHORTCUT.getKeyCode()) && this.map.allowsQuickTp();
    }

}
