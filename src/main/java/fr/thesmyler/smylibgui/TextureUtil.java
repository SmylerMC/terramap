package fr.thesmyler.smylibgui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public final class TextureUtil {

	public static TextureProperties getTextureProperties(ResourceLocation location) throws UnknownTextureException {
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		textureManager.bindTexture(location);
		if(textureManager.getTexture(location) == null) throw new UnknownTextureException();
		int format = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_INTERNAL_FORMAT);
		int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
		int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
		return new TextureProperties(width, height, format);
	}
	
	public static class TextureProperties {
		
		private int width, height, format;
		
		public TextureProperties(int width, int height, int format) {
			this.width = width;
			this.height = height;
			this.format = format;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public int getFormat() {
			return format;
		}
		
	}

	public static class UnknownTextureException extends Exception {}
}
