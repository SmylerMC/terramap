package net.smyler.smylib.gui.widgets;

import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.Font;
import net.smyler.smylib.gui.UiDrawContext;
import net.smyler.smylib.gui.containers.WidgetContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.net.URLConnection.guessContentTypeFromStream;
import static net.smyler.smylib.Color.*;
import static net.smyler.smylib.Objects.requireNonNullElse;
import static net.smyler.smylib.SmyLib.getGameClient;
import static net.smyler.smylib.gui.gl.DrawMode.QUADS;
import static net.smyler.smylib.gui.gl.VertexFormat.POSITION_TEXTURE;

/**
 * Previews the content of an {@link InputStream input stream}.
 * Detects the file type and chooses the most appropriate display method
 * (only images supported for now).
 *
 * @author Smyler
 */
public class ContentPreviewWidget implements Widget {

    private float x, y, width, height;
    private final float padding = 10f;
    private final int z;
    private final Font font = getGameClient().defaultFont();

    private String mimeType;
    private Preview preview;

    public ContentPreviewWidget(float x, float y, int z, float width, float height) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void previewFrom(@NotNull InputStream stream) throws IOException {
        if (this.preview != null) {
            this.preview.dispose();
        }
        stream = new BufferedInputStream(stream);  // Makes sure the stream supports marking
        this.mimeType = guessContentTypeFromStream(stream);
        try {
            if (this.mimeType.startsWith("image/")) {
                this.preview = new ImagePreview(stream);
            } else {
                throw new IOException("Unsupported MIME type: " + this.mimeType);
            }
        } catch (Exception e) {
            this.preview = new UnsupportedPreview();
        }
    }

    public void clearPreview() {
        if (this.preview != null) {
            this.preview.dispose();
        }
        this.mimeType = null;
        this.preview = null;
    }

    @Override
    public void draw(UiDrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, @Nullable WidgetContainer parent) {
        context.drawRectangle(x, y, x + this.width, y + this.height, DARK_OVERLAY);
        String info = requireNonNullElse(this.mimeType, "Unknown file type");
        String[] lines = this.font.wrapToWidth(info, this.width - this.padding * 2);
        float mimeHeight = this.font.computeHeight(lines);
        this.font.drawCenteredLines(x + this.width / 2f, y + this.padding, MEDIUM_GRAY, false, lines);
        context.drawStrokeLine(
                LIGHT_GRAY, 1f,
                x + this.padding,  y + this.padding * 2 + mimeHeight,
                x + this.width - this.padding * 2, y + this.padding * 2 + mimeHeight
        );
        if (this.preview != null) {
            this.preview.draw(
                    context,
                    x + this.padding,
                    y + this.padding * 3 + mimeHeight,
                    this.width - 2 * this.padding,
                    this.height - mimeHeight - 4 * this.padding
            );
        }
    }

    @Override
    public float getX() {
        return this.x;
    }

    public ContentPreviewWidget setX(float x) {
        this.x = x;
        return this;
    }

    @Override
    public float getY() {
        return this.y;
    }

    public ContentPreviewWidget setY(float y) {
        this.y = y;
        return this;
    }

    @Override
    public int getZ() {
        return this.z;
    }

    @Override
    public float getWidth() {
        return this.width;
    }

    public ContentPreviewWidget setWidth(float width) {
        this.width = width;
        return this;
    }

    @Override
    public float getHeight() {
        return this.height;
    }

    public ContentPreviewWidget setHeight(float height) {
        this.height = height;
        return this;
    }

    private interface Preview {
        void draw(UiDrawContext context, float x, float y, float width, float height);
        void dispose();
    }

    private class UnsupportedPreview implements Preview {

        @Override
        public void draw(UiDrawContext context, float x, float y, float width, float height) {
            Font font = ContentPreviewWidget.this.font;
            font.drawCentered(
                    x + width / 2f, y + height / 2f - font.height() / 2f,
                    "Unsupported file type",
                    LIGHT_GRAY, false
            );
        }

        @Override
        public void dispose() {}

    }

    private class ImagePreview implements Preview {

        final Identifier dynamicTexture;
        final float width, height;

        ImagePreview(InputStream stream) throws IOException {
            BufferedImage image = ImageIO.read(stream);
            this.dynamicTexture = getGameClient().guiDrawContext().loadDynamicTexture(image);
            this.width = image.getWidth();
            this.height = image.getHeight();
        }

        @Override
        public void draw(UiDrawContext context, float x, float y, float width, float height) {
            String resolution = String.format("%sx%s", this.width, this.height);
            float resolutionHeight = ContentPreviewWidget.this.font.height() + ContentPreviewWidget.this.padding;
            float renderWidth = width;
            float renderHeight = width * this.height / this.width;
            if (renderHeight > height - resolutionHeight) {
                renderWidth = (height - resolutionHeight) * this.width / this.height;
                renderHeight = height - resolutionHeight;
            }
            int z = ContentPreviewWidget.this.z;
            context.gl().setTexture(this.dynamicTexture);
            context.gl().startDrawing(QUADS, POSITION_TEXTURE);
            float renderX = x + (width - renderWidth) / 2f;
            float renderY = y + (height - renderHeight - resolutionHeight) / 2f;
            context.gl().vertex().texture(0, 0).position(renderX, renderY, z).end();
            context.gl().vertex().texture(1, 0).position(renderX + renderWidth, renderY, z).end();
            context.gl().vertex().texture(1, 1).position(renderX + renderWidth, renderY + renderHeight, z).end();
            context.gl().vertex().texture(0, 1).position(renderX, renderY + renderHeight, z).end();
            context.gl().draw();
            ContentPreviewWidget.this.font.drawCentered(x + width / 2f, renderY + renderHeight + padding, resolution, LIGHT_GRAY, false);
        }

        @Override
        public void dispose() {
            getGameClient().guiDrawContext().unloadDynamicTexture(this.dynamicTexture);
        }

    }

}
