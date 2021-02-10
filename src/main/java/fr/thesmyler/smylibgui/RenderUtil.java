package fr.thesmyler.smylibgui;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public final class RenderUtil {
	
	private static final List<Integer> SCISSOR_POS_STACK = new LinkedList<>();
	private static int scissorX, scissorY, scissorWidth, scissorHeight;
	
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
	public static void scissor(int x, int y, int width, int height) {
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
	public static int[] getScissor() {
		return new int[] {scissorX, scissorY, scissorWidth, scissorHeight};
	}
	
	private static void doScissor() {
		Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution res = new ScaledResolution(mc);
        int screenWidth = res.getScaledWidth();
        int screenHeight = res.getScaledHeight();
        double scaleW = mc.displayWidth / res.getScaledWidth_double();
        double scaleH = mc.displayHeight / res.getScaledHeight_double();
        int y = Math.max(0, Math.min(screenHeight, screenHeight - scissorY - scissorHeight));
        int x = Math.max(0, Math.min(screenWidth, scissorX));
        int width = Math.max(0, Math.min(scissorWidth + scissorX, screenWidth - scissorX));
        int height = Math.max(0, Math.min(scissorY + scissorHeight, screenHeight - scissorY));
        GL11.glScissor((int)(x * scaleW), (int)(y * scaleH), (int)(width * scaleW), (int)(height * scaleH));
	}
	
    public static void drawTexturedModalRect(int x, int y, int z, int u, int v, int width, int height) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + height, z).tex(u * 0.00390625, (v + height) * 0.00390625).endVertex();
        bufferbuilder.pos(x + width, y + height, z).tex((u + width) * 0.00390625, (v + height) * 0.00390625).endVertex();
        bufferbuilder.pos(x + width, y, z).tex((u + width) * 0.00390625, v * 0.00390625).endVertex();
        bufferbuilder.pos(x, y, z).tex(u * 0.00390625, v * 0.00390625).endVertex();
        tessellator.draw();
    }

    public void drawTexturedModalRect(float x, float y, int z, int minU, int minV, int maxU, int maxV) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + maxV, z).tex(minU * 0.00390625, (minV + maxV) * 0.00390625).endVertex();
        bufferbuilder.pos(x + maxU, y + maxV, z).tex((minU + maxU) * 0.00390625, (minV + maxV) * 0.00390625).endVertex();
        bufferbuilder.pos(x + maxU, y, z).tex((minU + maxU) * 0.00390625, minV * 0.00390625).endVertex();
        bufferbuilder.pos(x, y, z).tex(minU * 0.00390625, minV * 0.00390625).endVertex();
        tessellator.draw();
    }

    public void drawTexturedModalRect(int x, int y, int z, TextureAtlasSprite textureSprite, int width, int height) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + height, z).tex(textureSprite.getMinU(), textureSprite.getMaxV()).endVertex();
        bufferbuilder.pos(x + width, y + height, z).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).endVertex();
        bufferbuilder.pos(x + width, y, z).tex(textureSprite.getMaxU(), textureSprite.getMinV()).endVertex();
        bufferbuilder.pos(x, y, z).tex(textureSprite.getMinU(), textureSprite.getMinV()).endVertex();
        tessellator.draw();
    }

}
