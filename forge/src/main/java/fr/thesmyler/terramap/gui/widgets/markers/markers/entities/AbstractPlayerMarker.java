package fr.thesmyler.terramap.gui.widgets.markers.markers.entities;

import net.smyler.smylib.gui.DrawContext;
import net.smyler.smylib.gui.GlState;
import org.lwjgl.opengl.GL11;

import net.smyler.smylib.gui.containers.WidgetContainer;
import net.smyler.smylib.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.AbstractPlayerMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.markers.AbstractMovingMarker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import static net.smyler.smylib.Color.WHITE;

public abstract class AbstractPlayerMarker extends AbstractMovingMarker {

    private final int downScaleFactor;

    public AbstractPlayerMarker(MarkerController<?> controller, int downscaleFactor) {
        super(controller, 16f / downscaleFactor, 16f / downscaleFactor);
        this.downScaleFactor = downscaleFactor;
    }

    @Override
    public void draw(DrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        GlState glState = context.glState();

        boolean drawName = this.showName(hovered);
        float textureSize = 128f / this.downScaleFactor;
        glState.enableAlpha();
        if(hovered) context.drawRectangle(x +1, y +1, x + this.getWidth() + 1, y + this.getHeight() + 1, Color.DARK_OVERLAY);

        // Draw the direction arrow
        if(this.showDirection(hovered) && Float.isFinite(this.azimuth)) {
            float azimuth = this.azimuth;
            if(parent instanceof MapWidget) azimuth += ((MapWidget)parent).getController().getRotation();
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + this.width / 2, y + this.height / 2, 0);
            GlStateManager.rotate(azimuth, 0, 0, 1);
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            context.glState().disableAlpha();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.shadeModel(7425);
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buff = tess.getBuffer();
            buff.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_COLOR);
            buff.pos(0, -this.height*1.2, 0).color(1f, 0, 0, 0.7f).endVertex();
            buff.pos(-this.width/2, -this.height * 0.7, 0).color(0.8f, 0, 0, 0.9f).endVertex();
            buff.pos(0, -this.height * 0.8, 0).color(0.5f, 0, 0, 1f).endVertex();
            buff.pos(this.width/2, -this.height * 0.7, 0).color(0.8f, 0, 0, 0.9f).endVertex();
            tess.draw();
            GlStateManager.shadeModel(7424);
            GlStateManager.disableBlend();
            context.glState().enableAlpha();
            GlStateManager.enableTexture2D();
            GlStateManager.popMatrix();
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(this.getSkin());
        glState.setColor(WHITE.withAlpha(this.getTransparency()));
        RenderUtil.drawModalRectWithCustomSizedTexture(x, y, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight(), textureSize, textureSize);
        RenderUtil.drawModalRectWithCustomSizedTexture(x, y, 80f / this.downScaleFactor, this.getHeight(), this.getWidth(), this.getHeight(), textureSize, textureSize);

        if(drawName) {
            float halfSize = this.width / 2;
            String name = this.getDisplayName().getFormattedText();
            float strWidth = parent.getFont().getStringWidth(name);
            float nameY = y - parent.getFont().height() - 2;
            context.drawRectangle(x + halfSize - strWidth / 2 - 2, y - parent.getFont().height() - 4, x + strWidth / 2 + halfSize + 2, y - 1, Color.DARK_OVERLAY);
            parent.getFont().drawCenteredString(x + halfSize, nameY, name, WHITE, false);
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
