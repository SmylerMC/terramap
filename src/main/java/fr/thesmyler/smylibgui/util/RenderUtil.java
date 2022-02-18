package fr.thesmyler.smylibgui.util;

import java.util.LinkedList;
import java.util.List;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.devices.GameContext;
import org.lwjgl.opengl.GL11;

import net.buildtheearth.terraplusplus.dep.net.daporkchop.lib.common.util.PValidation;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import static fr.thesmyler.smylibgui.SmyLibGui.getGameContext;

public final class RenderUtil {

    private static final List<Float> SCISSOR_POS_STACK = new LinkedList<>();
    private static float scissorX, scissorY, scissorWidth, scissorHeight;

    /**
     * Enable or disable scissoring
     * 
     * @param yesNo
     */
    public static void setScissorState(boolean yesNo) {
        if(yesNo) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
        } else {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    /**
     * @return whether or not scissor is enabled in OpenGL
     */
    public static boolean isScissorEnabled() {
        return GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
    }

    /**
     * Set scissoring zone
     * This does not enable scissoring, it only sets the scissored zone
     * 
     * @see #setScissorState(boolean)
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public static void scissor(float x, float y, float width, float height) {
        scissorX = x;
        scissorY = y;
        scissorWidth = width;
        scissorHeight = height;
        doScissor();
    }

    /**
     * Saves current scissoring zone
     * 
     * @see #popScissorPos()
     */
    public static void pushScissorPos() {
        SCISSOR_POS_STACK.add(scissorX);
        SCISSOR_POS_STACK.add(scissorY);
        SCISSOR_POS_STACK.add(scissorWidth);
        SCISSOR_POS_STACK.add(scissorHeight);
    }

    /**
     * Reset scissoring zone to what it was last time pushScissor was called and remove the corresponding zone from the stack
     */
    public static void popScissorPos() {
        scissorHeight = SCISSOR_POS_STACK.remove(SCISSOR_POS_STACK.size() - 1);
        scissorWidth = SCISSOR_POS_STACK.remove(SCISSOR_POS_STACK.size() - 1);
        scissorY = SCISSOR_POS_STACK.remove(SCISSOR_POS_STACK.size() - 1);
        scissorX = SCISSOR_POS_STACK.remove(SCISSOR_POS_STACK.size() - 1);
        doScissor();
    }

    /**
     * @return the current scissor position {scissorX, scissorY, scissorWidth, scissorHeight}
     */
    public static float[] getScissor() {
        return new float[] {scissorX, scissorY, scissorWidth, scissorHeight};
    }

    private static void doScissor() {
        GameContext window = SmyLibGui.getGameContext();
        float screenWidth = window.getWindowWidth();
        float screenHeight = window.getWindowHeight();
        double scale = window.getScaleFactor();
        float y = Math.max(0, Math.min(screenHeight, screenHeight - scissorY - scissorHeight));
        float x = Math.max(0, Math.min(screenWidth, scissorX));
        float width = Math.max(0, Math.min(Math.min(scissorWidth + scissorX, scissorWidth), screenWidth - scissorX));
        float height = Math.max(0, Math.min(scissorHeight, screenHeight - scissorY));
        GL11.glScissor((int)Math.round(x * scale), (int)Math.round(y * scale), (int)Math.round(width * scale), (int)Math.round(height * scale));
    }

    public static void drawRect(int z, double xLeft, double yTop, double xRight, double yBottom, Color color) {
        drawGradientRect(z, xLeft, yTop, xRight, yBottom, color, color, color, color);
    }

    public static void drawRect(double xLeft, double yTop, double xRight, double yBottom, Color color) {
        drawGradientRect(0, xLeft, yTop, xRight, yBottom, color, color, color, color);
    }
    
    public static void drawRectWithContour(int z, double xLeft, double yTop, double xRight, double yBottom, Color color, float contourSize, Color contourColor) {
        drawRect(z, xLeft, yTop, xRight, yBottom, color);
        drawClosedStrokeLine(z, contourColor, contourSize, 
                xLeft, yTop,
                xLeft, yBottom,
                xRight, yBottom,
                xRight, yTop);
    }
    
    public static void drawRectWithContour(double xLeft, double yTop, double xRight, double yBottom, Color color, float contourSize, Color contourColor) {
        drawRectWithContour(0, xLeft, yTop, xRight, yBottom, color, contourSize, contourColor);
    }

    public static void drawGradientRect(int z, double xLeft, double yTop, double xRight, double yBottom, Color upperLeftColor, Color lowerLeftColor, Color lowerRightColor, Color upperRightColor) {
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

    public static void drawGradientRect(double xLeft, double yTop, double xRight, double yBottom, Color upperLeftColor, Color lowerLeftColor, Color lowerRightColor, Color upperRightColor) {
        drawGradientRect(0, xLeft, yTop, xRight, yBottom, upperLeftColor, lowerLeftColor, lowerRightColor, upperRightColor);
    }

    public static void drawModalRectWithCustomSizedTexture(double x, double y, double z, double u, double v, double width, double height, double textureWidth, double textureHeight) {
        double f = 1.0f / textureWidth;
        double f1 = 1.0f / textureHeight;
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_TEX);
        builder.pos(x, y + height, z).tex(u * f, (v + height) * f1).endVertex();
        builder.pos(x + width, y + height, z).tex((u + width) * f, (v + height) * f1).endVertex();
        builder.pos(x + width, y, z).tex((u + width) * f, v * f1).endVertex();
        builder.pos(x, y, z).tex(u * f, v * f1).endVertex();
        tessellator.draw();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
    }

    public static void drawModalRectWithCustomSizedTexture(double x, double y, double u, double v, double width, double height, double textureWidth, double textureHeight) {
        drawModalRectWithCustomSizedTexture(x, y, 0d, u, v, width, height, textureWidth, textureHeight);
    }

    public static void drawTexturedModalRect(double x, double y, double z, double minU, double minV, double maxU, double maxV) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        builder.pos(x, y + maxV, z).tex(minU * 0.00390625, (minV + maxV) * 0.00390625).endVertex();
        builder.pos(x + maxU, y + maxV, z).tex((minU + maxU) * 0.00390625, (minV + maxV) * 0.00390625).endVertex();
        builder.pos(x + maxU, y, z).tex((minU + maxU) * 0.00390625, minV * 0.00390625).endVertex();
        builder.pos(x, y, z).tex(minU * 0.00390625, minV * 0.00390625).endVertex();
        tessellator.draw();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
    }

    public static void drawTexturedModalRect(double x, double y, double minU, double minV, double maxU, double maxV) {
        drawTexturedModalRect(x, y, 0, minU, minV, maxU, maxV);
    }

    public static void drawPolygon(double z, Color color, double... points) {
        PValidation.checkArg(points.length % 2 == 0, "An even number of coordinates is required");
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        color.applyGL();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION);
        for(int i=0; i<points.length; i+=2) {
            builder.pos(points[i], points[i+1], z).endVertex();
        }
        tessellator.draw();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    public static void drawPolygon(Color color, double... points) {
        drawPolygon(0d, color, points);
    }

    public static void drawStrokeLine(double z, Color color, float size, double... points) {
        GL11.glLineWidth((float)(size * getGameContext().getScaleFactor()));
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        color.applyGL();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        for(int i=0; i<points.length; i+=2) {
            builder.pos(points[i], points[i+1], z).endVertex();
        }
        tessellator.draw();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    public static void drawStrokeLine(Color color, float size, double... points) {
        drawStrokeLine(0, color, size, points);
    }

    public static void drawClosedStrokeLine(double z, Color color, float size, double... points) {
        GL11.glLineWidth((float)(size * getGameContext().getScaleFactor()));
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        color.applyGL();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
        for(int i=0; i<points.length; i+=2) {
            builder.pos(points[i], points[i+1], z).endVertex();
        }
        tessellator.draw();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    public static void drawClosedStrokeLine(Color color, float size, double... points) {
        drawClosedStrokeLine(0d, color, size, points);
    }

}
