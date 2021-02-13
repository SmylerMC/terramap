package fr.thesmyler.terramap.maps.imp;

import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.maps.IRasterTile;
import fr.thesmyler.terramap.maps.utils.TilePosUnmutable;
import net.buildtheearth.terraplusplus.generator.TerrainPreview;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class TerrainPreviewTile implements IRasterTile {

	private final TilePosUnmutable position;
	private ResourceLocation texture;
	private CompletableFuture<BufferedImage> textureTask;

	public TerrainPreviewTile(TilePosUnmutable position) {
		this.position = position;
	}

	@Override
	public boolean isTextureAvailable() {
		try {
			this.tryLoadingTexture();
		} catch (Throwable e) {
			return false;
		}
		return this.texture != null;
	}

	@Override
	public ResourceLocation getTexture() throws Throwable {
		
		if(this.getPosition().getZoom() < TerrainPreviewMap.BASE_ZOOM_LEVEL)
			throw new IllegalArgumentException("Trying to request a terrain preview with a zoom that's to low (" + this.position.getZoom() + ")");
		
		 if(this.getPosition().getZoom() != TerrainPreviewMap.BASE_ZOOM_LEVEL) return null;
		
		if(this.texture == null) {
			if(this.textureTask == null) {
				TerrainPreview preview = TerramapClientContext.getContext().getTerrainPreview();
				if(preview != null) {
					this.textureTask = preview.tile(this.position.getX(), this.position.getY(), TerrainPreviewMap.BASE_ZOOM_LEVEL - this.position.getZoom());
				}
			} else this.tryLoadingTexture();
		}
		return this.texture;
	}

	@Override
	public void cancelTextureLoading() {
		if(this.textureTask != null) {
			this.textureTask.cancel(true);
			this.textureTask = null;
		}
	}

	@Override
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
	public TilePosUnmutable getPosition() {
		return this.position;
	}

	private void tryLoadingTexture() throws Throwable {
		if(this.textureTask != null && this.textureTask.isDone()){
			if(this.textureTask.isCompletedExceptionally()) {
				if(!this.textureTask.isCancelled()) {
					try {
						this.textureTask.get(); // That will throw an exception
					} catch(ExecutionException e) {
						this.textureTask = null;
						throw e.getCause();
					}
				}
				this.textureTask = null;
				return;
			}
			Minecraft mc = Minecraft.getMinecraft();
			TextureManager textureManager = mc.getTextureManager();
			BufferedImage image = this.textureTask.get();
			this.texture = textureManager.getDynamicTextureLocation("textures/gui/maps/debugterrainpreviewmap/" + this.position.getZoom() + "/" + this.position.getX() + "/" + this.position.getY(), new DynamicTexture(image));
			this.textureTask = null;
		}
	}

}
