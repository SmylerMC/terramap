package fr.thesmyler.terramap.maps;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.maps.utils.TerramapImageUtils;
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

	private final UnmutableTilePosition pos;
	private final String url;
	private ResourceLocation texture = null;
	private CompletableFuture<ByteBuf> textureTask;

	public static ResourceLocation errorTileTexture = null;

	public WebTile(String urlPattern, UnmutableTilePosition pos) {
		this.pos = pos;
		this.url = urlPattern
				.replace("{x}", "" + this.getX())
				.replace("{y}", "" + this.getY())
				.replace("{z}", "" + this.getZoom());
	}
	
	public WebTile(String urlPattern, int zoom, int x, int y) {
		this(urlPattern, new UnmutableTilePosition(zoom, x , y));
	}


	public String getURL() {
		return this.url;
	}
	
	public boolean isTextureAvailable() {
		if(texture != null) return true; // Don't try loading the texture if it has already been loaded
		try {
			this.tryLoadingTexture();
		} catch (InterruptedException | ExecutionException | IOException e) {
			return false;
		}
		return this.texture != null;
	}

	public ResourceLocation getTexture() throws IOException, InterruptedException, ExecutionException {
		if(this.texture == null) {
			if(this.textureTask == null) {
				this.textureTask = Http.get(this.getURL());
			} else this.tryLoadingTexture();
		}
		return this.texture;
	}
	
	private void tryLoadingTexture() throws InterruptedException, ExecutionException, IOException {
		if(this.textureTask != null && this.textureTask.isDone() && !this.textureTask.isCompletedExceptionally()){
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
	public boolean equals(Object obj) {
		if(obj == this) return true;
		if(obj == null) return false;
		if(!(obj instanceof WebTile)) return false;
		WebTile other = (WebTile) obj;
		return other.url.equals(this.url);
	}

	///// Various uninteresting getters and setters from here /////

	public UnmutableTilePosition getPosition() {
		return this.pos;
	}
	
	public int getX() {
		return this.pos.xPosition;
	}

	public int getY() {
		return this.pos.yPosition;
	}

	public int getZoom() {
		return this.pos.zoom;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public static void registerErrorTexture() {
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		int color[] = {170, 211, 223};
		WebTile.errorTileTexture = textureManager.getDynamicTextureLocation(TerramapMod.MODID + ":error_tile_texture", new DynamicTexture(TerramapImageUtils.imageFromColor(256,  256, color)));

	}

}
