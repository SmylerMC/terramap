package fr.thesmyler.smylibgui.util;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

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
		Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution res = new ScaledResolution(mc);
        int screenWidth = res.getScaledWidth();
        int screenHeight = res.getScaledHeight();
        double scaleW = mc.displayWidth / res.getScaledWidth_double();
        double scaleH = mc.displayHeight / res.getScaledHeight_double();
        float y = Math.max(0, Math.min(screenHeight, screenHeight - scissorY - scissorHeight));
        float x = Math.max(0, Math.min(screenWidth, scissorX));
        float width = Math.max(0, Math.min(scissorWidth + scissorX, screenWidth - scissorX));
        float height = Math.max(0, Math.min(scissorY + scissorHeight, screenHeight - scissorY));
        GL11.glScissor((int)Math.round(x * scaleW), (int)Math.round(y * scaleH), (int)Math.round(width * scaleW), (int)Math.round(height * scaleH));
	}
    
    public static void drawTexturedModalRect(float x, float y, int z, int minU, int minV, int maxU, int maxV) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + maxV, z).tex(minU * 0.00390625, (minV + maxV) * 0.00390625).endVertex();
        bufferbuilder.pos(x + maxU, y + maxV, z).tex((minU + maxU) * 0.00390625, (minV + maxV) * 0.00390625).endVertex();
        bufferbuilder.pos(x + maxU, y, z).tex((minU + maxU) * 0.00390625, minV * 0.00390625).endVertex();
        bufferbuilder.pos(x, y, z).tex(minU * 0.00390625, minV * 0.00390625).endVertex();
        tessellator.draw();
    }
    
    public static void drawTexturedModalRect(float x, float y, int z, float minU, float minV, float maxU, float maxV) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + maxV, z).tex(minU * 0.00390625, (minV + maxV) * 0.00390625).endVertex();
        bufferbuilder.pos(x + maxU, y + maxV, z).tex((minU + maxU) * 0.00390625, (minV + maxV) * 0.00390625).endVertex();
        bufferbuilder.pos(x + maxU, y, z).tex((minU + maxU) * 0.00390625, minV * 0.00390625).endVertex();
        bufferbuilder.pos(x, y, z).tex(minU * 0.00390625, minV * 0.00390625).endVertex();
        tessellator.draw();
    }
    
    public static void drawTexturedModalRect(float x, float y, float minU, float minV, float maxU, float maxV) {
    	drawTexturedModalRect(x, y, 0, minU, minV, maxU, maxV);
    }
    
    public static void drawRect(int z, float xLeft, float yTop, float xRight, float yBottom, Color color) {
    	drawGradientRect(z, xLeft, yTop, xRight, yBottom, color, color, color, color);
    }
    
    public static void drawRect(float xLeft, float yTop, float xRight, float yBottom, Color color) {
    	drawGradientRect(0, xLeft, yTop, xRight, yBottom, color, color, color, color);
    }
    
    public static void drawGradientRect(int z, float xLeft, float yTop, float xRight, float yBottom, Color upperLeftColor, Color lowerLeftColor, Color lowerRightColor, Color upperRightColor) {
    	GlStateManager.enableAlpha();
    	GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(xLeft, yTop, z).color(upperLeftColor.red(), upperLeftColor.green(), upperLeftColor.blue(), upperLeftColor.alpha()).endVertex();
        bufferbuilder.pos(xLeft, yBottom, z).color(lowerLeftColor.red(), lowerLeftColor.green(), lowerLeftColor.blue(), lowerLeftColor.alpha()).endVertex();
        bufferbuilder.pos(xRight, yBottom, z).color(lowerRightColor.red(), lowerRightColor.green(), lowerRightColor.blue(), lowerRightColor.alpha()).endVertex();
        bufferbuilder.pos(xRight, yTop, z).color(upperRightColor.red(), upperRightColor.green(), upperRightColor.blue(), upperRightColor.alpha()).endVertex();
        tessellator.draw();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }
    
    public static void drawGradientRect(float xLeft, float yTop, float xRight, float yBottom, Color upperLeftColor, Color lowerLeftColor, Color lowerRightColor, Color upperRightColor) {
    	drawGradientRect(0, xLeft, yTop, xRight, yBottom, upperLeftColor, lowerLeftColor, lowerRightColor, upperRightColor);
    }
    
    public static void drawModalRectWithCustomSizedTexture(float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight) {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + height, 0.0D).tex(u * f, (v + height) * f1).endVertex();
        bufferbuilder.pos(x + width, y + height, 0.0D).tex((u + width) * f, (v + height) * f1).endVertex();
        bufferbuilder.pos(x + width, y, 0.0D).tex((u + width) * f, v * f1).endVertex();
        bufferbuilder.pos(x, y, 0.0D).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }

}
