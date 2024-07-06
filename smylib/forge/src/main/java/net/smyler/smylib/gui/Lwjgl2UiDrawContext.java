package net.smyler.smylib.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.smyler.smylib.Color;
import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.gl.*;
import net.smyler.smylib.gui.sprites.Sprite;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.floor;
import static net.minecraft.client.Minecraft.getMinecraft;
import static net.smyler.smylib.Preconditions.checkArgument;
import static net.smyler.smylib.SmyLib.getGameClient;

public class Lwjgl2UiDrawContext implements UiDrawContext {

    private final Scissor scissor = new Gl11Scissor();
    private final GlContext gl = new Lwjgl2GlContext();
    private final AtomicInteger dynamicTextureCounter = new AtomicInteger(0);

    @Override
    public Scissor scissor() {
        return this.scissor;
    }

    @Override
    public GlContext gl() {
        return this.gl;
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
        getMinecraft().getTextureManager().bindTexture(location);
        double uLeft = (sprite.xLeft + leftCrop) / sprite.textureWidth;
        double uRight = (sprite.xRight - rightCrop) / sprite.textureWidth;
        double vTop = (sprite.yTop + topCrop) / sprite.textureHeight;
        double vBottom = (sprite.yBottom - bottomCrop) / sprite.textureHeight;
        double width = sprite.width() - leftCrop - rightCrop;
        double height = sprite.height() - topCrop - bottomCrop;

        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.enableTexture2D();
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

    @Override
    public void drawTooltip(String text, double x, double y) {
        GuiScreen currentScreen = getMinecraft().currentScreen;
        if (currentScreen == null) {
            //TODO make it draw by the HUD instead (once we have a working HUD system)
            return;  // We are in game, not in a GUI
        }
        // This is a workaround for vanilla not allowing double coordinates and re-enabling lighting without any check
        boolean lighting = GL11.glIsEnabled(GL11.GL_LIGHTING);
        GlStateManager.pushMatrix();
        int px = (int) floor(x);
        int py = (int) floor(y);
        double rx = x - px;
        double ry = y - py;
        GlStateManager.translate(rx, ry, 0);
        currentScreen.drawHoveringText(text, px, py);
        GlStateManager.popMatrix();
        if(!lighting) GlStateManager.disableLighting();
    }

    @Override
    public void drawTexture(Identifier texture, double x, double y, double u, double v, double width, double height, double textureWidth, double textureHeight) {
        getMinecraft().getTextureManager().bindTexture(new ResourceLocation(texture.namespace, texture.path));
        double f = 1.0f / textureWidth;
        double f1 = 1.0f / textureHeight;
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_TEX);
        builder.pos(x, y + height, 0d).tex(u * f, (v + height) * f1).endVertex();
        builder.pos(x + width, y + height, 0d).tex((u + width) * f, (v + height) * f1).endVertex();
        builder.pos(x + width, y, 0d).tex((u + width) * f, v * f1).endVertex();
        builder.pos(x, y, 0d).tex(u * f, v * f1).endVertex();
        tessellator.draw();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
    }

    @Override
    public Identifier loadDynamicTexture(BufferedImage image) {
        DynamicTexture dynamicTexture = new DynamicTexture(image);
        String name = "smylib/dynamic/" + this.dynamicTextureCounter.getAndIncrement() + ".png";
        ResourceLocation location = getMinecraft().getTextureManager().getDynamicTextureLocation(name, dynamicTexture);
        return new Identifier(location.getNamespace(), location.getPath());
    }

    @Override
    public void unloadDynamicTexture(Identifier texture) {
        getMinecraft().getTextureManager().deleteTexture(new ResourceLocation(texture.namespace, texture.path));
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
