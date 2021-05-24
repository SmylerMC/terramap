package fr.thesmyler.terramap.gui.widgets;

import org.lwjgl.opengl.GL11;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.TextureUtil;
import fr.thesmyler.smylibgui.util.TextureUtil.TextureProperties;
import fr.thesmyler.smylibgui.util.TextureUtil.UnknownTextureException;
import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.terramap.TerramapMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class RibbonCompassWidget implements IWidget {

    private static final ResourceLocation COMPASS_BACKGROUND_TEXTURE = new ResourceLocation(TerramapMod.MODID, "textures/gui/compass_ribbon_background.png");
    private static final ResourceLocation COMPASS_INDICATOR_TEXTURE = new ResourceLocation(TerramapMod.MODID, "textures/gui/compass_ribbon_indicator.png");

    private float x, y;
    private int z;
    private float width, height, textureWidth, indicatorWidth, indicatorHeight;
    private float azimuth = 0;
    private boolean visibility = true;

    public RibbonCompassWidget(float x, float y, int z, float width) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        try {
            TextureProperties p1 = TextureUtil.getTextureProperties(COMPASS_BACKGROUND_TEXTURE);
            this.height = p1.getHeight();
            this.textureWidth = p1.getWidth();
            TextureProperties p2 = TextureUtil.getTextureProperties(COMPASS_INDICATOR_TEXTURE);
            this.indicatorHeight = p2.getHeight();
            this.indicatorWidth = p2.getWidth();
        } catch (UnknownTextureException e) {
            TerramapMod.logger.error("Failed to get texture heiht for ribbon compass");
            TerramapMod.logger.catching(e);
            this.height = 16;
        }
    }

    public RibbonCompassWidget(int z) {
        this(0, 0, z, 0);
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        double blendBorder = 10; // How many pixels to fade to alpha=0 on the sides
        double leftU = (double)(this.azimuth - 180) / 360 + (double)(this.textureWidth - this.width) / this.textureWidth / 2;
        double leftCU = leftU + blendBorder/this.textureWidth;
        double rightU = leftU + (double) this.width / this.textureWidth;
        double rightCU = rightU - blendBorder/this.textureWidth;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buff = tess.getBuffer();

        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        Minecraft.getMinecraft().getTextureManager().bindTexture(COMPASS_BACKGROUND_TEXTURE);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        buff.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buff.pos(x, y, 0).tex(leftU, 0d).color(1f, 1f, 1f, 0f).endVertex();
        buff.pos(x, y + this.height, 0).tex(leftU, 1d).color(1f, 1f, 1f, 0f).endVertex();
        buff.pos(x + blendBorder, y + this.height, 0).tex(leftCU, 1d).color(1f, 1f, 1f, 1f).endVertex();
        buff.pos(x + blendBorder, y, 0).tex(leftCU, 0d).color(1f, 1f, 1f, 1f).endVertex();

        buff.pos(x + blendBorder, y, 0).tex(leftCU, 0d).color(1f, 1f, 1f, 1f).endVertex();
        buff.pos(x + blendBorder, y + this.height, 0).tex(leftCU, 1d).color(1f, 1f, 1f, 1f).endVertex();
        buff.pos(x + this.width - blendBorder, y + this.height, 0).tex(rightCU, 1d).color(1f, 1f, 1f, 1f).endVertex();
        buff.pos(x + this.width - blendBorder, y, 0).tex(rightCU, 0d).color(1f, 1f, 1f, 1f).endVertex();

        buff.pos(x + this.width - blendBorder, y, 0).tex(rightCU, 0d).color(1f, 1f, 1f, 1f).endVertex();
        buff.pos(x + this.width - blendBorder, y + this.height, 0).tex(rightCU, 1d).color(1f, 1f, 1f, 1f).endVertex();
        buff.pos(x + this.width, y + this.height, 0).tex(rightU, 1d).color(1f, 1f, 1f, 0f).endVertex();
        buff.pos(x + this.width, y, 0).tex(rightU, 0d).color(1f, 1f, 1f, 0f).endVertex();

        tess.draw();

        Minecraft.getMinecraft().getTextureManager().bindTexture(COMPASS_INDICATOR_TEXTURE);
        Color.WHITE.applyGL();
        buff.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        double indX = x + (double)(this.width - this.indicatorWidth) / 2;
        double indY = y + (double)(this.height - this.indicatorHeight) / 2;
        buff.pos(indX, indY, 0).tex(0d, 0d).endVertex();
        buff.pos(indX, indY + this.indicatorHeight, 0).tex(0d, 1d).endVertex();
        buff.pos(indX + this.indicatorWidth, indY + this.indicatorHeight, 0).tex(1d, 1d).endVertex();
        buff.pos(indX + this.indicatorWidth, indY, 0).tex(1d, 0d).endVertex();
        tess.draw();

    }

    @Override
    public float getX() {
        return x;
    }

    public RibbonCompassWidget setX(float x) {
        this.x = x;
        return this;
    }

    @Override
    public float getY() {
        return y;
    }

    public RibbonCompassWidget setY(float y) {
        this.y = y;
        return this;
    }

    @Override
    public float getWidth() {
        return width;
    }

    public RibbonCompassWidget setWidth(float width) {
        this.width = width;
        return this;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public int getZ() {
        return z;
    }

    public float getAzimuth() {
        return this.azimuth;
    }

    public RibbonCompassWidget setAzimuth(float azimuth) {
        this.azimuth = azimuth;
        return this;
    }

    @Override
    public boolean isVisible(WidgetContainer parent) {
        return this.visibility;
    }


    public RibbonCompassWidget setVisibility(boolean yesNo) {
        this.visibility = yesNo;
        return this;
    }

}
