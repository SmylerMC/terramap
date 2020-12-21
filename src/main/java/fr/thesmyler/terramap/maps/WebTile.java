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

	private long x;
	private long y;
	private int zoom;
	private int size;
	private ResourceLocation texture = null;
	private String urlPattern;
	private CompletableFuture<ByteBuf> textureTask;

	public static ResourceLocation errorTileTexture = null;


	public WebTile(String urlPattern, int zoom, long x, long y) {
		this.urlPattern = urlPattern;
		this.x = x;
		this.y = y;
		this.zoom = zoom;
		this.size = 256;

		if(!WebMercatorUtils.isTileInWorld(zoom, x, y))
			throw new InvalidTileCoordinatesException(this);

	}


	public String getURL() {
		return this.urlPattern
				.replace("{x}", ""+this.getX())
				.replace("{y}", ""+this.getY())
				.replace("{z}", ""+this.getZoom());
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
			this.textureTask.cancel(true); //FIXME This is not working
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


	///// Various uninteresting getters and setters from here /////

	public long getX() {
		return x;
	}

	public long getY() {
		return y;
	}

	public int getZoom() {
		return zoom;
	}

	public long getXinPixel() {
		return this.x * this.size;
	}

	public long getYinPixel() {
		return this.y * this.size;
	}

	public int getSideSize() {
		return this.size;
	}


	public class InvalidTileCoordinatesException extends RuntimeException{

		/**
		 * @param string
		 */
		public InvalidTileCoordinatesException(WebTile t) {
			super("Invalid tile coordinates: " + t.zoom + "/" + t.x + "/" + t.y);
		}


	}

	public static void registerErrorTexture() {
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		int color[] = {175, 175, 175};
		WebTile.errorTileTexture = textureManager.getDynamicTextureLocation(TerramapMod.MODID + ":error_tile_texture", new DynamicTexture(TerramapImageUtils.imageFromColor(256,  256, color)));

	}

}
