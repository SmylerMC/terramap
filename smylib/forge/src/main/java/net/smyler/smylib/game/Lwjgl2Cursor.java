package net.smyler.smylib.game;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.Cursor;
import net.smyler.smylib.gui.sprites.Sprite;
import net.smyler.smylib.resources.CursorResourceMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.IntBuffer;

public final class Lwjgl2Cursor implements Cursor {

    private final Identifier identifier;
    private int hotspotX;
    private int hotspotY;

    private int width = 0;
    private int height = 0;
    private Sprite sprite = null;
    private org.lwjgl.input.Cursor nativeCursor = null;

    public Lwjgl2Cursor(Identifier identifier, @Nullable CursorResourceMetadata metadata) {
        this.identifier = identifier;
        if (metadata != null) {
            this.hotspotX = metadata.hotspotX();
            this.hotspotY = metadata.hotspotY();
        } else {
            this.hotspotX = this.hotspotY = -1;
        }
    }

    public void load() throws Exception {
        ResourceLocation location = new ResourceLocation(this.identifier.namespace, this.identifier.path);

        // Ensure the texture is loaded and bound and get its information
        Minecraft.getMinecraft().getTextureManager().bindTexture(location);
        int format = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_INTERNAL_FORMAT);
        this.width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        this.height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

        // If hotspot was not set from the resource metadata, set it to the middle of the texture
        if (this.hotspotX == -1 || this.hotspotY == -1) {
            this.hotspotX = this.width / 2;
            this.hotspotY = this.height / 2;
        }

        // Prepare a buffer with the image data
        IntBuffer buffer = BufferUtils.createIntBuffer(this.width * this.height);
        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, format, GL11.GL_UNSIGNED_BYTE, buffer);
        for (int x = 0; x < width; x++) { // Flip the image along the X axis
            for(int i = 0; i < height / 2; i++) {
                int yBottom = width * (height - i - 1) + x;
                int yTop = width * i + x;
                int pixel = buffer.get(yBottom);
                buffer.put(yBottom, buffer.get(yTop));
                buffer.put(yTop, pixel);
            }
        }

        // Create the LWJGL cursor object and the corresponding sprite
        this.nativeCursor = new org.lwjgl.input.Cursor(
                this.width, this.height,
                this.hotspotX, this.hotspotY,
                1,
                buffer, BufferUtils.createIntBuffer(1)
        );
        this.sprite = Sprite.builder()
                .texture(this.identifier, this.width, this.height)
                .fullTexture()
                .build();

    }

    @Override
    public @NotNull Identifier identifier() {
        return this.identifier;
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return this.height;
    }

    @Override
    public int hotspotX() {
        return this.hotspotX;
    }

    @Override
    public int hotspotY() {
        return this.hotspotY;
    }

    @Override
    public @NotNull Sprite sprite() {
        return this.sprite;
    }

    public org.lwjgl.input.Cursor getNativeCursor() {
        return this.nativeCursor;
    }
}
