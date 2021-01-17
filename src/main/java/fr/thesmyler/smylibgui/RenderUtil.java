package fr.thesmyler.smylibgui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public final class RenderUtil {
	
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
