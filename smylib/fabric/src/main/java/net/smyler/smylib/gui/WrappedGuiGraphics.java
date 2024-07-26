package net.smyler.smylib.gui;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.smyler.smylib.Color;
import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.gl.Blaze3dGlContext;
import net.smyler.smylib.gui.gl.Gl20Scissor;
import net.smyler.smylib.gui.gl.GlContext;
import net.smyler.smylib.gui.gl.Scissor;
import net.smyler.smylib.gui.sprites.Sprite;
import org.joml.Matrix4f;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static net.smyler.smylib.Preconditions.checkArgument;

public class WrappedGuiGraphics implements UiDrawContext {

    public final GuiGraphics vanillaGraphics;
    private final Scissor scissor = new Gl20Scissor();
    private final GlContext glState = new Blaze3dGlContext();
    private final TextureManager textureManager;
    private static final AtomicInteger dynamicTextureLocationCounter = new AtomicInteger(0);

    private final Map<Identifier, DynamicTexture> dynamicTextureCache = new HashMap<>();

    public WrappedGuiGraphics(Minecraft minecraft, GuiGraphics vanillaGraphics) {
        this.vanillaGraphics = vanillaGraphics;
        this.textureManager = minecraft.getTextureManager();
    }

    @Override
    public Scissor scissor() {
        return this.scissor;
    }

    @Override
    public GlContext gl() {
        return this.glState;
    }

    @Override
    public void drawGradientRectangle(double z, double xLeft, double yTop, double xRight, double yBottom, Color upperLeftColor, Color lowerLeftColor, Color lowerRightColor, Color upperRightColor) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        Matrix4f matrix = this.vanillaGraphics.pose().last().pose();
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        builder.vertex(matrix, (float)xLeft, (float) yTop, (float)z).color(upperLeftColor.asInt()).endVertex();
        builder.vertex(matrix, (float)xLeft, (float) yBottom, (float)z).color(lowerLeftColor.asInt()).endVertex();
        builder.vertex(matrix, (float)xRight, (float) yBottom, (float)z).color(lowerRightColor.asInt()).endVertex();
        builder.vertex(matrix, (float)xRight, (float) yTop, (float)z).color(upperRightColor.asInt()).endVertex();
        BufferUploader.drawWithShader(builder.end());

        RenderSystem.disableBlend();
    }

    @Override
    public void drawStrokeLine(double z, Color color, float size, double... points) {
        RenderSystem.lineWidth(size);
        this.drawMultiPointsGeometry(z, color, points);
    }

    @Override
    public void drawClosedStrokeLine(double z, Color color, float size, double... points) {
        if (points.length < 2) {
            return;
        }
        RenderSystem.lineWidth(size);
        this.drawMultiPointsGeometry(z, color, points);
        this.drawMultiPointsGeometry(z, color,
                points[points.length - 2], points[points.length - 1],
                points[0], points[1]
        );
    }

    @Override
    public void drawSpriteCropped(double x, double y, double z, Sprite sprite, double leftCrop, double topCrop, double rightCrop, double bottomCrop) {
        ResourceLocation location = new ResourceLocation(sprite.texture.namespace, sprite.texture.path);
        RenderSystem.setShaderTexture(0, location);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        float fx = (float) x;
        float fy = (float) y;
        float fz = (float) z;
        float uLeft = (float) ((sprite.xLeft + leftCrop) / sprite.textureWidth);
        float uRight = (float) ((sprite.xRight - rightCrop) / sprite.textureWidth);
        float vTop = (float) ((sprite.yTop + topCrop) / sprite.textureHeight);
        float vBottom = (float) ((sprite.yBottom - bottomCrop) / sprite.textureHeight);
        float width = (float) (sprite.width() - leftCrop - rightCrop);
        float height = (float) (sprite.height() - topCrop - bottomCrop);

        Matrix4f matrix = this.vanillaGraphics.pose().last().pose();
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.vertex(matrix, fx, fy + height, fz).uv(uLeft, vBottom).endVertex();
        builder.vertex(matrix, fx + width, fy + height, fz).uv(uRight, vBottom).endVertex();
        builder.vertex(matrix, fx + width, fy, fz).uv(uRight, vTop).endVertex();
        builder.vertex(matrix, fx, fy, fz).uv(uLeft, vTop).endVertex();
        BufferUploader.drawWithShader(builder.end());

        RenderSystem.disableBlend();
    }

    @Override
    public void drawTooltip(String text, double x, double y) {
        //TODO implement drawTooltip
    }

    @Override
    public Identifier loadDynamicTexture(BufferedImage image) {
        NativeImage nativeImage = new NativeImage(image.getWidth(), image.getHeight(), true);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int pixel = image.getRGB(x, y);
                pixel = (pixel & 0xFF) << 16 | (pixel & 0xFF0000) >> 16 | (pixel & 0xFF00FF00);
                nativeImage.setPixelRGBA(x, y, pixel);
            }
        }
        ResourceLocation location = new ResourceLocation("smylib", "dynamic_texture/" + dynamicTextureLocationCounter.getAndIncrement());
        Identifier identifier = new Identifier(location.getNamespace(), location.getPath());
        DynamicTexture texture = new DynamicTexture(nativeImage);
        this.textureManager.register(location, texture);
        this.dynamicTextureCache.put(identifier, texture);
        return identifier;
    }

    @Override
    public void unloadDynamicTexture(Identifier texture) {
        this.textureManager.release(new ResourceLocation(texture.path, texture.namespace));
        DynamicTexture removed = this.dynamicTextureCache.remove(texture);
        removed.close();
    }

    private void drawMultiPointsGeometry(double z, Color color, double... points) {
        checkArgument(points.length % 2 == 0, "An even number of coordinates is required");
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        RenderSystem.setShaderColor(color.redf(), color.greenf(), color.bluef(), color.alphaf());
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.LINE_STRIP, DefaultVertexFormat.POSITION);
        for(int i=0; i<points.length; i+=2) {
            builder.vertex(points[i], points[i+1], z).endVertex();
        }
        BufferUploader.drawWithShader(builder.end());
        RenderSystem.disableBlend();
    }

}
