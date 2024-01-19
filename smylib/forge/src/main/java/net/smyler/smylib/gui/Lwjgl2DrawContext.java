package net.smyler.smylib.gui;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.smyler.smylib.Color;
import org.lwjgl.opengl.GL11;

import static net.smyler.smylib.Preconditions.checkArgument;
import static net.smyler.smylib.SmyLib.getGameClient;

public class Lwjgl2DrawContext implements DrawContext {

    private final Scissor scissor = new Gl11Scissor();
    private final GlState glState = new LwjglState();

    @Override
    public Scissor scissor() {
        return this.scissor;
    }

    @Override
    public GlState glState() {
        return this.glState;
    }

    @Override
    public void drawGradientRectangle(double z, double xLeft, double yTop, double xRight, double yBottom, Color upperLeftColor, Color lowerLeftColor, Color lowerRightColor, Color upperRightColor) {
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(xLeft, yTop, z).color(upperLeftColor.red(), upperLeftColor.green(), upperLeftColor.blue(), upperLeftColor.alpha()).endVertex();
        builder.pos(xLeft, yBottom, z).color(lowerLeftColor.red(), lowerLeftColor.green(), lowerLeftColor.blue(), lowerLeftColor.alpha()).endVertex();
        builder.pos(xRight, yBottom, z).color(lowerRightColor.red(), lowerRightColor.green(), lowerRightColor.blue(), lowerRightColor.alpha()).endVertex();
        builder.pos(xRight, yTop, z).color(upperRightColor.red(), upperRightColor.green(), upperRightColor.blue(), upperRightColor.alpha()).endVertex();
        tessellator.draw();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    @Override
    public void drawPolygon(double z, Color color, double... points) {
        this.drawMultiPointsGeometry(GL11.GL_POLYGON, z, color, points);
    }

    @Override
    public void drawStrokeLine(double z, Color color, float size, double... points) {
        GL11.glLineWidth(size * getGameClient().scaleFactor());
        this.drawMultiPointsGeometry(GL11.GL_LINE_STRIP, z, color, points);
    }

    @Override
    public void drawClosedStrokeLine(double z, Color color, float size, double... points) {
        GL11.glLineWidth(size * getGameClient().scaleFactor());
        this.drawMultiPointsGeometry(GL11.GL_LINE_LOOP, z, color, points);
    }

    private void drawMultiPointsGeometry(int glType, double z, Color color, double... points) {
        checkArgument(points.length % 2 == 0, "An even number of coordinates is required");
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(color.redf(), color.greenf(), color.bluef(), color.alphaf());
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(glType, DefaultVertexFormats.POSITION);
        for(int i=0; i<points.length; i+=2) {
            builder.pos(points[i], points[i+1], z).endVertex();
        }
        tessellator.draw();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

}
