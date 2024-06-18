package fr.thesmyler.smylibgui.util;

import java.nio.IntBuffer;

import net.smyler.terramap.Terramap;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import fr.thesmyler.terramap.TerramapMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;

import static net.smyler.smylib.SmyLib.getLogger;

//TODO Reload cursors with resources
//TODO Have a wrapper class for lwjgl cursors
public final class Cursors {

    public static final Cursor CURSOR_MOVE = loadCursorFromTexture(new ResourceLocation(TerramapMod.MODID, "textures/gui/cursors/move.png"), 32, 32);
    public static final Cursor CURSOR_RESIZE_VERTICAL = loadCursorFromTexture(new ResourceLocation(TerramapMod.MODID, "textures/gui/cursors/resize_vertical.png"), 16, 32);
    public static final Cursor CURSOR_RESIZE_HORIZONTAL = loadCursorFromTexture(new ResourceLocation(TerramapMod.MODID, "textures/gui/cursors/resize_horizontal.png"), 32, 16);
    public static final Cursor CURSOR_RESIZE_DIAGONAL_1 = loadCursorFromTexture(new ResourceLocation(TerramapMod.MODID, "textures/gui/cursors/resize_diag1.png"), 32, 32);
    public static final Cursor CURSOR_RESIZE_DIAGONAL_2 = loadCursorFromTexture(new ResourceLocation(TerramapMod.MODID, "textures/gui/cursors/resize_diag2.png"), 32, 32);

    static {
        if(Minecraft.IS_RUNNING_ON_MAC) {
            getLogger().error("Running on MacOS, will not use custom cursors.");
        }
    }


    /**
     * Try setting the native cursor, logging an error if fails
     * @param cursor
     */
    public static void trySetCursor(Cursor cursor) {
        if(Minecraft.IS_RUNNING_ON_MAC) return;
        try {
            Mouse.setNativeCursor(cursor);
        } catch (LWJGLException e) {
            getLogger().catching(e);
        }
    }

    private static Cursor loadCursorFromTexture(ResourceLocation loc, int hotX, int hotY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(loc); // That makes sure the texture is loaded
        ITextureObject texture = Minecraft.getMinecraft().getTextureManager().getTexture(loc);
        if(texture != null) {
            int id = texture.getGlTextureId();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
            int format = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_INTERNAL_FORMAT);
            int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
            int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
            IntBuffer buffer = BufferUtils.createIntBuffer(width * height);
            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, format, GL11.GL_UNSIGNED_BYTE, buffer);
            for(int x=0; x<width; x++) for(int y=0; y<height/2; y++) { // Flip the image
                int i1 = width*(height-y-1) + x;
                int i2 = width*y + x;
                int p = buffer.get(i1);
                buffer.put(i1, buffer.get(i2));
                buffer.put(i2, p);
            }
            try {
                return new Cursor(width, height, hotX, hotY, 1, buffer, BufferUtils.createIntBuffer(1));
            } catch (LWJGLException e) {
                Terramap.instance().logger().error("Failed to load a custom cursor for {}", loc);
                Terramap.instance().logger().catching(e);
            }
        }
        return Mouse.getNativeCursor();
    }

}
