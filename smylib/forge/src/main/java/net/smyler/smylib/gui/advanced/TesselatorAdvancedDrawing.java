package net.smyler.smylib.gui.advanced;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.smyler.smylib.Color;
import net.smyler.smylib.Identifier;
import org.lwjgl.opengl.GL11;

import static net.minecraft.client.Minecraft.getMinecraft;
import static net.smyler.smylib.Preconditions.checkState;

public class TesselatorAdvancedDrawing implements AdvancedDrawing {

    private final Tessellator tessellator = Tessellator.getInstance();
    private final BufferBuilder bufferBuilder = this.tessellator.getBuffer();
    private final TextureManager textureManager = getMinecraft().getTextureManager();

    private VertexFormat currentFormat = null;

    @Override
    public void begin(DrawMode mode, VertexFormat format) {
        int glMode;
        net.minecraft.client.renderer.vertex.VertexFormat glVertexFormat;
        switch (mode) {
            case LINES:
                glMode = GL11.GL_LINES;
                break;
            case LINE_STRIP:
                glMode = GL11.GL_LINE_STRIP;
                break;
            case TRIANGLES:
                glMode = GL11.GL_TRIANGLES;
                break;
            case TRIANGLE_STRIP:
                glMode = GL11.GL_TRIANGLE_STRIP;
                break;
            case TRIANGLE_FAN:
                glMode = GL11.GL_TRIANGLE_FAN;
                break;
            case QUADS:
                glMode = GL11.GL_QUADS;
                break;
            default:
                throw new IllegalStateException("Unsupported mode: " + mode);
        }
        switch (format) {
            case POSITION:
                glVertexFormat = DefaultVertexFormats.POSITION;
                break;
            case POSITION_COLOR:
                glVertexFormat = DefaultVertexFormats.POSITION_COLOR;
                break;
            case POSITION_TEXTURE:
                glVertexFormat = DefaultVertexFormats.POSITION_TEX;
                break;
            case POSITION_TEXTURE_COLOR:
                glVertexFormat = DefaultVertexFormats.POSITION_TEX_COLOR;
                break;
            default:
                throw new IllegalStateException("Unsupported format: " + format);
        }
        this.bufferBuilder.begin(glMode, glVertexFormat);
        this.currentFormat = format;
    }

    @Override
    public void texture(Identifier identifier) {
        this.textureManager.bindTexture(new ResourceLocation(identifier.namespace, identifier.path));
    }

    @Override
    public void color(Color color) {
        GlStateManager.color(color.redf(), color.greenf(), color.bluef(), color.alphaf());
    }

    @Override
    public VertexBuilder vertex() {
        checkState(this.currentFormat != null, "Not building!");
        return new VertexBuilderImplementation();
    }

    @Override
    public void draw() {
        if (this.currentFormat.texture) {
            GlStateManager.enableTexture2D();
            GlStateManager.shadeModel(GL11.GL_FLAT);
        } else {
            GlStateManager.disableTexture2D();
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        }
        GlStateManager.enableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.enableBlend();
        this.tessellator.draw();
        this.currentFormat = null;
    }

    private class VertexBuilderImplementation implements VertexBuilder {

        private double x, y, z;
        private float r, g, b, a;
        private double u, v;

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
            return this.color(color.redf(), color.greenf(), color.bluef(), color.alphaf());
        }

        @Override
        public VertexBuilder texture(double u, double v) {
            this.u = u;
            this.v = v;
            return this;
        }

        @Override
        public void end() {
            VertexFormat format = TesselatorAdvancedDrawing.this.currentFormat;
            TesselatorAdvancedDrawing.this.bufferBuilder.pos(this.x, this.y, this.z);
            if (format.texture) {
                TesselatorAdvancedDrawing.this.bufferBuilder.tex(this.u, this.v);
            }
            if (format.color) {
                TesselatorAdvancedDrawing.this.bufferBuilder.color(this.r, this.g, this.b, this.a);
            }
            TesselatorAdvancedDrawing.this.bufferBuilder.endVertex();
        }
    }

}
