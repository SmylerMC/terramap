package fr.thesmyler.smylibgui.util;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;


@Deprecated
public final class RenderUtil {

    @Deprecated
    public static void drawModalRectWithCustomSizedTexture(double x, double y, double z, double u, double v, double width, double height, double textureWidth, double textureHeight) {
        double f = 1.0f / textureWidth;
        double f1 = 1.0f / textureHeight;
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.enableTexture2D();
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

    @Deprecated
    public static void drawModalRectWithCustomSizedTexture(double x, double y, double u, double v, double width, double height, double textureWidth, double textureHeight) {
        drawModalRectWithCustomSizedTexture(x, y, 0d, u, v, width, height, textureWidth, textureHeight);
    }

}
