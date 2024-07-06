package fr.thesmyler.terramap.gui.widgets.markers.markers.entities;

import net.smyler.smylib.gui.UiDrawContext;
import net.smyler.smylib.gui.gl.GlContext;

import net.smyler.smylib.gui.containers.WidgetContainer;
import net.smyler.smylib.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.AbstractPlayerMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.markers.AbstractMovingMarker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import static net.smyler.smylib.Color.WHITE;
import static net.smyler.smylib.gui.gl.DrawMode.TRIANGLE_FAN;
import static net.smyler.smylib.gui.gl.VertexFormat.POSITION_COLOR;

public abstract class AbstractPlayerMarker extends AbstractMovingMarker {

    private final int downScaleFactor;

    public AbstractPlayerMarker(MarkerController<?> controller, int downscaleFactor) {
        super(controller, 16f / downscaleFactor, 16f / downscaleFactor);
        this.downScaleFactor = downscaleFactor;
    }

    @Override
    public void draw(UiDrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        GlContext glState = context.gl();

        boolean drawName = this.showName(hovered);
        float textureSize = 128f / this.downScaleFactor;
        glState.enableAlpha();
        if(hovered) context.drawRectangle(x +1, y +1, x + this.getWidth() + 1, y + this.getHeight() + 1, Color.DARK_OVERLAY);

        // Draw the direction arrow
        if(this.showDirection(hovered) && Float.isFinite(this.azimuth)) {
            float azimuth = this.azimuth;
            if(parent instanceof MapWidget) {
                azimuth += ((MapWidget)parent).getController().getRotation();
            }

            GlContext gl = context.gl();

            gl.pushViewMatrix();
            gl.translate(x + this.width / 2, y + this.height / 2);
            gl.rotate(azimuth);

            gl.disableAlpha();
            gl.enableSmoothShading();
            gl.startDrawing(TRIANGLE_FAN, POSITION_COLOR);
            gl.vertex().position(0, -this.height*1.2, 0).color(1f, 0, 0, 0.7f).end();
            gl.vertex().position(-this.width/2, -this.height * 0.7, 0).color(0.8f, 0, 0, 0.9f).end();
            gl.vertex().position(0, -this.height * 0.8, 0).color(0.5f, 0, 0, 1f).end();
            gl.vertex().position(this.width/2, -this.height * 0.7, 0).color(0.8f, 0, 0, 0.9f).end();
            gl.draw();

            GlStateManager.popMatrix();
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(this.getSkin());
        glState.setColor(WHITE.withAlpha(this.getTransparency()));
        RenderUtil.drawModalRectWithCustomSizedTexture(x, y, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight(), textureSize, textureSize);
        RenderUtil.drawModalRectWithCustomSizedTexture(x, y, 80f / this.downScaleFactor, this.getHeight(), this.getWidth(), this.getHeight(), textureSize, textureSize);

        if(drawName) {
            float halfSize = this.width / 2;
            String name = this.getDisplayName().getFormattedText();
            float strWidth = parent.getFont().computeWidth(name);
            float nameY = y - parent.getFont().height() - 2;
            context.drawRectangle(x + halfSize - strWidth / 2 - 2, y - parent.getFont().height() - 4, x + strWidth / 2 + halfSize + 2, y - 1, Color.DARK_OVERLAY);
            parent.getFont().drawCentered(x + halfSize, nameY, name, WHITE, false);
        }

        glState.setColor(WHITE);
    }

    protected abstract ResourceLocation getSkin();

    protected abstract float getTransparency();

    protected boolean showName(boolean hovered) {
        if(this.getController() instanceof AbstractPlayerMarkerController) {
            AbstractPlayerMarkerController<?> controller = (AbstractPlayerMarkerController<?>) this.getController();
            return controller.doesShowNames() || hovered;
        }
        return hovered;
    }

    protected boolean showDirection(boolean hovered) {
        if(this.getController() instanceof AbstractPlayerMarkerController) {
            AbstractPlayerMarkerController<?> controller = (AbstractPlayerMarkerController<?>) this.getController();
            return controller.doesShowDirection();
        }
        return true;
    }

    @Override
    public float getDeltaX() {
        return - this.getWidth() / 2;
    }

    @Override
    public float getDeltaY() {
        return - this.getHeight() / 2;
    }

    @Override
    public boolean canBeTracked() {
        return true;
    }

}
