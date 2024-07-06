package net.smyler.smylib.gui.gl;

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

public class Lwjgl2GlContext implements GlContext {

    private final Tessellator tessellator = Tessellator.getInstance();
    private final BufferBuilder bufferBuilder = this.tessellator.getBuffer();
    private final TextureManager textureManager = getMinecraft().getTextureManager();

    private VertexFormat currentFormat = null;

    @Override
    public void enableAlpha() {
        GlStateManager.enableAlpha();
    }

    @Override
    public void disableAlpha() {
        GlStateManager.disableAlpha();
    }

    @Override
    public void setColor(Color color) {
        GlStateManager.color(
            color.redf(),
            color.greenf(),
            color.bluef(),
            color.alphaf()
        );
    }

    @Override
    public Color getColor() {
        return new Color(GL11.glGetInteger(GL11.GL_CURRENT_COLOR));
    }

    @Override
    public void setTexture(Identifier identifier) {
        this.textureManager.bindTexture(new ResourceLocation(identifier.namespace, identifier.path));
    }

    @Override
    public void enableColorLogic(ColorLogic colorLogic) {
        GlStateManager.LogicOp op = this.getGlColorLogic(colorLogic);
        GlStateManager.colorLogicOp(op);
        GlStateManager.enableColorLogic();
    }

    private GlStateManager.LogicOp getGlColorLogic(ColorLogic logic) {
        switch (logic) {
            case CLEAR:
                return GlStateManager.LogicOp.CLEAR;
            case SET:
                return GlStateManager.LogicOp.SET;
            case COPY:
                return GlStateManager.LogicOp.COPY;
            case COPY_INVERTED:
                return GlStateManager.LogicOp.COPY_INVERTED;
            case NOOP:
                return GlStateManager.LogicOp.NOOP;
            case INVERT:
                return GlStateManager.LogicOp.INVERT;
            case AND:
                return GlStateManager.LogicOp.AND;
            case NAND:
                return GlStateManager.LogicOp.NAND;
            case OR:
                return GlStateManager.LogicOp.OR;
            case NOR:
                return GlStateManager.LogicOp.NOR;
            case XOR:
                return GlStateManager.LogicOp.XOR;
            case EQUIV:
                return GlStateManager.LogicOp.EQUIV;
            case AND_REVERSE:
                return GlStateManager.LogicOp.AND_REVERSE;
            case OR_REVERSE:
                return GlStateManager.LogicOp.OR_REVERSE;
            case OR_INVERTED:
                return GlStateManager.LogicOp.OR_INVERTED;
        }
        throw new IllegalStateException("Illegal enum value");
    }

    @Override
    public void disableColorLogic() {
        GlStateManager.disableColorLogic();
    }

    @Override
    public void pushViewMatrix() {
        GlStateManager.pushMatrix();
    }

    @Override
    public void rotate(double angle) {
        GlStateManager.rotate((float)angle, 0, 0, 1f);
    }

    @Override
    public void translate(double x, double y) {
        GlStateManager.translate(x, y, 0);
    }

    @Override
    public void scale(double x, double y) {
        GlStateManager.scale(x, y, 1d);
    }

    @Override
    public void popViewMatrix() {
        GlStateManager.popMatrix();
    }

    @Override
    public void startDrawing(DrawMode mode, VertexFormat format) {
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
    public VertexBuilder vertex() {
        checkState(this.currentFormat != null, "Not building!");
        return new VertexBuilderImplementation();
    }

    @Override
    public void draw() {
        checkState(this.currentFormat != null, "Not building!");
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
            VertexFormat format = Lwjgl2GlContext.this.currentFormat;
            BufferBuilder builder = Lwjgl2GlContext.this.bufferBuilder;
            builder.pos(this.x, this.y, this.z);
            if (format.texture) {
                builder.tex(this.u, this.v);
            }
            if (format.color) {
                builder.color(this.r, this.g, this.b, this.a);
            }
            builder.endVertex();
        }
    }

}
