package net.smyler.smylib.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
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

    @Override
    public void drawSpriteCropped(double x, double y, double z, Sprite sprite, double leftCrop, double topCrop, double rightCrop, double bottomCrop) {
        final ResourceLocation location = new ResourceLocation(sprite.texture.namespace, sprite.texture.path);
        Minecraft.getMinecraft().getTextureManager().bindTexture(location);
        double uLeft = (sprite.xLeft + leftCrop) / sprite.textureWidth;
        double uRight = (sprite.xRight - rightCrop) / sprite.textureWidth;
        double vTop = (sprite.yTop + topCrop) / sprite.textureHeight;
        double vBottom = (sprite.yBottom - bottomCrop) / sprite.textureHeight;
        double width = sprite.width() - leftCrop - rightCrop;
        double height = sprite.height() - topCrop - bottomCrop;

        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_TEX);
        builder.pos(x, y + height, z).tex(uLeft, vBottom).endVertex();
        builder.pos(x + width, y + height, z).tex(uRight, vBottom).endVertex();
        builder.pos(x + width, y, z).tex(uRight, vTop).endVertex();
        builder.pos(x, y, z).tex(uLeft, vTop).endVertex();
        tessellator.draw();

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
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
