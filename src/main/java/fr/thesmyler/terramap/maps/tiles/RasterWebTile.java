package fr.thesmyler.terramap.maps.tiles;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.caching.Cachable;
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
public abstract class RasterWebTile implements Cachable {

	protected long x;
	protected long y;
	protected int zoom;
	protected int[] defaultPixel = {0, 0, 0, 0}; //What to return when the tile doesn't exist but should
	protected int size;
	protected BufferedImage image;
	protected ResourceLocation texture = null;
	
	private static ResourceLocation errorTileTexture = null;


	public RasterWebTile(int size, int zoom, long x, long y) {

		this.x = x;
		this.y = y;
		this.zoom = zoom;
		this.size = size;

		if(!WebMercatorUtils.isTileInWorld(zoom, x, y))
			throw new InvalidTileCoordinatesException(this);

	}


	public RasterWebTile(int size, int zoom, long x, long y, int[] defaultPixel) {

		this(size, zoom, x, y);
		this.defaultPixel = defaultPixel;

	}


	@Override
	public abstract URL getURL();


	@Override
	public String getFileName(){
		return this.getClass().getName() + "_" + this.getZoom() + "_" + this.getX() + "_" + this.getY() + ".png";
	}


	public BufferedImage getImage() throws IOException {
		if(this.image == null) {
			File f = TerramapMod.cacheManager.getFile(this);
			//If the file has been cached, it may have been loaded in this::cached
				if(this.image == null) this.loadImageFomFile(f);
		}
		return this.image;
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
	/**
	 * 
	 * @param x
	 * @param y
	 * @return The value of the pixel at x and y
	 * @throws InvalidMapboxSessionException 
	 * @throws IOException 
	 */
	public int[] getPixel(int x, int y) throws IOException {
		return TerramapImageUtils.decodeRGBA2Array(this.getImage().getRGB(x, y));
	}


	public ResourceLocation getTexture() {
		if(this.texture == null) {
			Minecraft mc = Minecraft.getMinecraft();
			TextureManager textureManager = mc.getTextureManager();
			try {
				BufferedImage image = this.getImage();
				this.texture = textureManager.getDynamicTextureLocation("textures/gui/maps/" + this.x + "/" + this.y + "/" + this.zoom, new DynamicTexture(image));
			} catch (Exception e) {
				TerramapMod.logger.catching(e);
				TerramapMod.cacheManager.reportError(this);
				TerramapMod.cacheManager.cacheAsync(this);
				return RasterWebTile.errorTileTexture;
			}
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
		public InvalidTileCoordinatesException(RasterWebTile t) {
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
		RasterWebTile.errorTileTexture = textureManager.getDynamicTextureLocation(TerramapMod.MODID + ":error_tile_texture", new DynamicTexture(TerramapImageUtils.imageFromColor(256,  256, color)));

	}

}
