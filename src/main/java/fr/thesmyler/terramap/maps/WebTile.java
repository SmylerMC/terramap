package fr.thesmyler.terramap.maps;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.caching.Cachable;
import fr.thesmyler.terramap.caching.requests.CachedRequest;
import fr.thesmyler.terramap.maps.utils.TerramapImageUtils;
import fr.thesmyler.terramap.maps.utils.WebMercatorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

/**
 * @author SmylerMC
 *
 */
public class WebTile implements Cachable {

	protected long x;
	protected long y;
	protected int zoom;
	protected int[] defaultPixel = {0, 0, 0, 0}; //What to return when the tile doesn't exist but should
	protected int size;
	protected BufferedImage image;
	protected ResourceLocation texture = errorTileTexture;
	private String urlPattern;
	private boolean waitingForTexture = false;

	private static ResourceLocation errorTileTexture = null;


	public WebTile(String urlPattern, int zoom, long x, long y) {
		this.urlPattern = urlPattern;
		this.x = x;
		this.y = y;
		this.zoom = zoom;
		this.size = 256;

		if(!WebMercatorUtils.isTileInWorld(zoom, x, y))
			throw new InvalidTileCoordinatesException(this);

	}


	public WebTile(String urlPattern, int zoom, long x, long y, int[] defaultPixel) {
		this(urlPattern, zoom, x, y);
		this.defaultPixel = defaultPixel;
	}


	@Override
	public URL getURL() {
		try {
			return new URL(
					this.urlPattern
					.replace("{x}", ""+this.getX())
					.replace("{y}", ""+this.getY())
					.replace("{z}", ""+this.getZoom()));
		} catch (MalformedURLException e) {
			TerramapMod.logger.error("Failed to craft url for a raster tile. URL pattern is " + this.urlPattern);
			e.printStackTrace();
		}
		return null;
	}


	@Override
	public String getFileName(){
		URL u = this.getURL();
		u.getPath();
		return u.getHost() + u.getPath().replace('/', '.'); //TODO Make sure this is always a valid file name
	}

	private void loadFromRequest(CachedRequest r) {
		try {
			Minecraft mc = Minecraft.getMinecraft();
			TextureManager textureManager = mc.getTextureManager();
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(r.getData()));
			this.texture = textureManager.getDynamicTextureLocation("textures/gui/maps/" + this.x + "/" + this.y + "/" + this.zoom, new DynamicTexture(image));
			this.waitingForTexture = false;
		} catch (Exception e) {
			TerramapMod.logger.catching(e);
			TerramapMod.cacheManager.reportError(this);				
		}
	}

	public boolean hasTexture() {
		return !errorTileTexture.equals(this.texture);
	}
	
	public boolean isWaitingForTexture() {
		return this.waitingForTexture;
	}

	@Override
	public void cached(File f) {
		try {
			this.loadImageFomFile(f);
		} catch (IOException e) {
			TerramapMod.logger.error("Got an IOException when reading a file which should have been properly cached!");
			TerramapMod.logger.catching(e);
		}
	}


	private void loadImageFomFile(File f) throws IOException {
		if(!f.exists() || !f.isFile()) {
			this.image = TerramapImageUtils.imageFromColor(this.size, this.size, this.defaultPixel);
		} else {
			this.image = ImageIO.read(f);
		}
	}

	public ResourceLocation getTexture() {
		if(!this.hasTexture() && !this.waitingForTexture) {
			TerramapMod.cacheManagerNew.getAsync(this.getURL(), (d) -> Minecraft.getMinecraft().addScheduledTask(() -> this.loadFromRequest(d)));			
			this.waitingForTexture = true;
		}
		return this.texture;
	}

	public void unloadTexture() {
		if(this.texture != null) {
			Minecraft mc = Minecraft.getMinecraft();
			TextureManager textureManager = mc.getTextureManager();
			textureManager.deleteTexture(this.texture);
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

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;


	}

	public static void registerErrorTexture() {
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		int color[] = {175, 175, 175};
		WebTile.errorTileTexture = textureManager.getDynamicTextureLocation(TerramapMod.MODID + ":error_tile_texture", new DynamicTexture(TerramapImageUtils.imageFromColor(256,  256, color)));

	}

}
