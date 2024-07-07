package net.smyler.smylib.gui.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.smyler.smylib.Color;
import net.smyler.smylib.Identifier;

import static net.smyler.smylib.Preconditions.checkState;


public class Blaze3dGlContext implements GlContext {

    private final TextureManager textureManager;
    private final Tesselator tesselator;
    private final BufferBuilder bufferBuilder;
    private VertexFormat currentFormat;

    public Blaze3dGlContext(Minecraft minecraft) {
        this.textureManager = minecraft.getTextureManager();
        this.tesselator = Tesselator.getInstance();
        this.bufferBuilder = this.tesselator.getBuilder();
    }

    @Override
    public void enableAlpha() {
        //TODO enable alpha
    }

    @Override
    public void disableAlpha() {
        //TODO disable alpha
    }

    @Override
    public void setColor(Color color) {
        RenderSystem.setShaderColor(color.redf(), color.greenf(), color.bluef(), color.alphaf());
    }

    @Override
    public Color getColor() {
        float[] colorFloat = RenderSystem.getShaderColor();
        return new Color(colorFloat[0], colorFloat[1], colorFloat[2], colorFloat[3]);
    }

    @Override
    public void setTexture(Identifier texture) {
        this.textureManager.bindForSetup(new ResourceLocation(texture.namespace, texture.path));
    }

    @Override
    public void enableColorLogic(ColorLogic colorLogic) {
        //TODO enableColorLogic
    }

    @Override
    public void disableColorLogic() {
        RenderSystem.disableColorLogicOp();
    }

    @Override
    public void enableSmoothShading() {
        //TODO implement shading models
    }

    @Override
    public void enableFlatShading() {
        //TODO implement shading models
    }

    @Override
    public void pushViewMatrix() {
        RenderSystem.getModelViewStack().pushPose();
    }

    @Override
    public void rotate(double angle) {
        RenderSystem.getModelViewMatrix().rotate((float)(angle), 0f, 0f, 1f);
    }

    @Override
    public void translate(double x, double y) {
        RenderSystem.getModelViewMatrix().translate((float)x, (float)y, 0);
    }

    @Override
    public void scale(double x, double y) {
        RenderSystem.getModelViewMatrix().scale((float)x, (float)y, 1);
    }

    @Override
    public void popViewMatrix() {
        RenderSystem.getModelViewStack().popPose();
    }

    @Override
    public void startDrawing(DrawMode mode, VertexFormat format) {
        checkState(this.currentFormat == null, "Already drawing!");
        this.currentFormat = format;
        com.mojang.blaze3d.vertex.VertexFormat.Mode blazeMode = switch (mode) {
            case LINES -> com.mojang.blaze3d.vertex.VertexFormat.Mode.LINES;
            case LINE_STRIP -> com.mojang.blaze3d.vertex.VertexFormat.Mode.LINE_STRIP;
            case TRIANGLES -> com.mojang.blaze3d.vertex.VertexFormat.Mode.TRIANGLES;
            case TRIANGLE_STRIP -> com.mojang.blaze3d.vertex.VertexFormat.Mode.TRIANGLE_STRIP;
            case TRIANGLE_FAN -> com.mojang.blaze3d.vertex.VertexFormat.Mode.TRIANGLE_FAN;
            case QUADS -> com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS;
        };
        com.mojang.blaze3d.vertex.VertexFormat blazeFormat = switch (format) {
            case POSITION -> DefaultVertexFormat.POSITION;
            case POSITION_TEXTURE -> DefaultVertexFormat.POSITION_TEX;
            case POSITION_COLOR -> DefaultVertexFormat.POSITION_COLOR;
            case POSITION_TEXTURE_COLOR -> DefaultVertexFormat.POSITION_TEX_COLOR;
        };
        this.bufferBuilder.begin(blazeMode, blazeFormat);
    }

    @Override
    public VertexBuilder vertex() {
        return new VertexBuilderImplementation();
    }

    @Override
    public void draw() {
        checkState(this.currentFormat != null, "Not drawing!");
        this.tesselator.end();
    }

    private class VertexBuilderImplementation implements VertexBuilder {
        double x, y, z;
        float u, v;
        float r, g, b, a;

        @Override
        public VertexBuilder position(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }

        @Override
        public VertexBuilder color(float r, float g, float b, float a) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
            return this;
        }

        @Override
        public VertexBuilder color(Color color) {
            this.color(color.redf(), color.greenf(), color.bluef(), color.alphaf());
            return this;
        }

        @Override
        public VertexBuilder texture(double u, double v) {
            this.u = (float)u;
            this.v = (float)v;
            return this;
        }

        @Override
        public void end() {
            VertexFormat format = Blaze3dGlContext.this.currentFormat;
            VertexConsumer consumer = Blaze3dGlContext.this.bufferBuilder.vertex(this.x, this.y, this.z);
            if (format.texture) {
                consumer = consumer.uv(this.u, this.v);
            }
            if (format.color) {
                consumer.color(this.r, this.g, this.b, this.a);
            }
            consumer.endVertex();
        }

    }

}
