package fr.thesmyler.terramap.maps;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.maps.utils.TerramapImageUtils;
import fr.thesmyler.terramap.maps.utils.WebMercatorUtils;
import io.github.terra121.util.http.Disk;
import io.github.terra121.util.http.Http;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

/**
 * @author SmylerMC
 *
 */
public class WebTile {

	private final int x;
	private final int y;
	private final int zoom;
	private final String url;
	private ResourceLocation texture = null;
	private CompletableFuture<ByteBuf> textureTask;

	public static ResourceLocation errorTileTexture = null;

	public WebTile(String urlPattern, int zoom, int x, int y) {
		this.x = x;
		this.y = y;
		this.zoom = zoom;
		this.url = urlPattern
				.replace("{x}", ""+this.getX())
				.replace("{y}", ""+this.getY())
				.replace("{z}", ""+this.getZoom());
		if(!WebMercatorUtils.isTileInWorld(zoom, x, y))
			throw new InvalidTileCoordinatesException(this);

	}


	public String getURL() {
		return this.url;
	}
	
	public boolean isTextureAvailable() {
		return this.texture != null;
	}

	public ResourceLocation getTexture() throws IOException, InterruptedException, ExecutionException {
		if(this.texture == null) {
			if(this.textureTask == null) {
				this.textureTask = Http.get(this.getURL());
			} else if(this.textureTask.isDone() && !this.textureTask.isCompletedExceptionally()){
				Minecraft mc = Minecraft.getMinecraft();
				TextureManager textureManager = mc.getTextureManager();
				ByteBuf buf = this.textureTask.get();
				try (ByteBufInputStream is = new ByteBufInputStream(buf)) {
					BufferedImage image = ImageIO.read(is);
					if(image == null) throw new IOException("Failed to read image! url: " + this.getURL() + " file: " + Disk.cacheFileFor(new URL(this.getURL()).getFile()).toString());
					this.texture = textureManager.getDynamicTextureLocation("textures/gui/maps/" + this.getURL(), new DynamicTexture(image));
				}
			}
		}
		return this.texture;
	}
	
	public void cancelTextureLoading() {
		if(this.textureTask != null) {
			this.textureTask.cancel(true);
			this.textureTask = null;
		}
	}

	public void unloadTexture() {
		this.cancelTextureLoading();
		if(this.texture != null) {
			Minecraft mc = Minecraft.getMinecraft();
			TextureManager textureManager = mc.getTextureManager();
			textureManager.deleteTexture(this.texture);
			this.texture = null;
		}
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + zoom;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		WebTile other = (WebTile) obj;
		if (x != other.x) return false;
		if (y != other.y) return false;
		if (zoom != other.zoom) return false;
		return true;
	}

	///// Various uninteresting getters and setters from here /////

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZoom() {
		return zoom;
	}

	public class InvalidTileCoordinatesException extends RuntimeException{

		public InvalidTileCoordinatesException(WebTile t) {
			super("Invalid tile coordinates: " + t.zoom + "/" + t.x + "/" + t.y);
		}

	}

	public static void registerErrorTexture() {
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		int color[] = {170, 211, 223};
		WebTile.errorTileTexture = textureManager.getDynamicTextureLocation(TerramapMod.MODID + ":error_tile_texture", new DynamicTexture(TerramapImageUtils.imageFromColor(256,  256, color)));

	}

}
