package fr.thesmyler.terramap.gui.widgets.map;

import java.util.HashSet;
import java.util.Set;

import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.terramap.maps.IRasterTile;
import fr.thesmyler.terramap.maps.IRasterTiledMap;
import fr.thesmyler.terramap.maps.imp.UrlRasterTile;
import fr.thesmyler.terramap.maps.utils.TilePos.InvalidTilePositionException;
import fr.thesmyler.terramap.maps.utils.WebMercatorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

//TODO Fractional zoom
public class RasterMapLayerWidget extends MapLayerWidget {

	protected IRasterTiledMap map;
	protected Set<IRasterTile> lastNeededTiles = new HashSet<>();

	public RasterMapLayerWidget(IRasterTiledMap map, double tileScaling) {
		super(tileScaling);
		this.map = map;
	}

	public IRasterTiledMap getMap() {
		return this.map;
	}

	@Override
	public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean focused, Screen parent) {

		boolean perfectDraw = true;
		Set<IRasterTile> neededTiles = new HashSet<>();

		boolean debug = false;
		MapWidget parentMap = null;
		if(parent instanceof MapWidget) {
			parentMap = (MapWidget) parent;
			debug = parentMap.isDebugMode();
		}

		//TODO Remove the lines when tile scaling is not a power of 2
		double renderSize = WebMercatorUtils.TILE_DIMENSIONS / this.tileScaling;

		long upperLeftX = (long) this.getUpperLeftX();
		long upperLeftY = (long) this.getUpperLeftY();

		Minecraft mc = Minecraft.getMinecraft();
		TextureManager textureManager = mc.getTextureManager();

		int maxTileXY = (int) WebMercatorUtils.getDimensionsInTile((int)this.zoom);
		long maxX = (long) (upperLeftX + this.width);
		long maxY = (long) (upperLeftY + this.height);

		int lowerTileX = (int) Math.floor((double)upperLeftX / (double)renderSize);
		int lowerTileY = (int) Math.floor((double)upperLeftY / (double)renderSize);

		for(int tileX = lowerTileX; tileX * renderSize < maxX; tileX++) {

			for(int tileY = lowerTileY; tileY * renderSize < maxY; tileY++) {

				IRasterTile tile;

				try {
					tile = map.getTile((int)this.zoom, Math.floorMod(tileX, maxTileXY), tileY);
				} catch(InvalidTilePositionException e) { continue ;}

				// This is the tile we would like to render, but it is not possible if it hasn't been cached yet
				IRasterTile bestTile = tile;
				neededTiles.add(bestTile);
				boolean lowerResRender = false;
				boolean unlockedZoomRender = false;
				if(!bestTile.isTextureAvailable()) {
					lowerResRender = true;
					perfectDraw = false;
					if(this.zoom <= this.map.getMaxZoom()) {
						try {
							bestTile.getTexture(); // Will start loading the texture from cache / network
						} catch(Throwable e) {
							if(parentMap != null) {
								parentMap.reportError(this, e.toString());
							}
							perfectDraw = false;
						}
					} else {
						unlockedZoomRender = true;
					}

					while(tile.getPosition().getZoom() > 0 && !tile.isTextureAvailable()) {
						tile = this.map.getTile(tile.getPosition().getZoom()-1, tile.getPosition().getX() /2, tile.getPosition().getY() /2);
						if(tile.getPosition().getZoom() == this.map.getMaxZoom()) {
							try {
								tile.getTexture();
								neededTiles.add(tile);
							} catch (Throwable e) {
								if(parentMap != null) {
									parentMap.reportError(this, e.toString());
								}
							}
						}
					}
				}

				neededTiles.add(tile);

				int dispX = (int) Math.round(tileX * renderSize - upperLeftX);
				int dispY = (int) Math.round(tileY * renderSize - upperLeftY);

				int renderSizedSize = (int) Math.round(renderSize);

				int dX = 0;
				int dY = 0;

				int displayWidth = (int) Math.round(Math.min(renderSize, maxX - tileX * renderSize));
				int displayHeight = (int) Math.round(Math.min(renderSize, maxY - tileY * renderSize));

				if(tileX == lowerTileX) {
					dX -= dispX;
					dispX = 0;
					displayWidth -= dX;
				}
				if(tileY == lowerTileY) {
					dY -= dispY;
					dispY = 0;
					displayHeight -= dY;
				}

				if(lowerResRender) {
					int sizeFactor = (1 <<(bestTile.getPosition().getZoom() - tile.getPosition().getZoom()));

					int xInBiggerTile = (int) (bestTile.getPosition().getX() - sizeFactor * tile.getPosition().getX());
					int yInBiggerTile = (int) (bestTile.getPosition().getY() - sizeFactor * tile.getPosition().getY());

					double factorX = (double)xInBiggerTile / (double)sizeFactor;
					double factorY = (double)yInBiggerTile / (double)sizeFactor;
					renderSizedSize *= sizeFactor;
					dX += (int) (factorX * renderSizedSize);
					dY += (int) (factorY * renderSizedSize);
				}

				GlStateManager.color(1, 1, 1, 1);
				ResourceLocation texture = UrlRasterTile.errorTileTexture;
				try {
					if(tile.isTextureAvailable()) texture = tile.getTexture();
					else perfectDraw = false;
				} catch (Throwable e) {
					perfectDraw = false;
					if(parentMap != null) parentMap.reportError(this, e.toString());
				}
				textureManager.bindTexture(texture);
				Gui.drawModalRectWithCustomSizedTexture(
						x + dispX,
						y + dispY,
						dX, dY,
						displayWidth,
						displayHeight,
						renderSizedSize,
						renderSizedSize);

				if(debug) {
					final int RED = 0xFFFF0000;
					final int BLUE = 0xFF0000FF;
					final int WHITE = 0xFFFFFFFF;
					int lineColor = lowerResRender? unlockedZoomRender? BLUE: RED : WHITE;
					parent.drawHorizontalLine(
							dispX,
							dispX + displayWidth - 1,
							dispY,
							lineColor);
					parent.drawHorizontalLine(
							dispX,
							dispX + displayWidth - 1,
							dispY + displayHeight - 1,
							lineColor);
					parent.drawVerticalLine(
							dispX,
							dispY,
							dispY + displayHeight - 1,
							lineColor);
					parent.drawVerticalLine(
							dispX + displayWidth - 1,
							dispY,
							dispY + displayHeight - 1,
							lineColor);
					parent.getFont().drawString("" + tile.getPosition().getZoom(), dispX + 2, dispY + 2, lineColor);
				}
				GlStateManager.color(1, 1, 1, 1);
			}
		}

		if(perfectDraw && parentMap != null) parentMap.discardPreviousErrors(this);
		this.lastNeededTiles.removeAll(neededTiles);
		this.lastNeededTiles.forEach(tile -> tile.cancelTextureLoading());
		this.lastNeededTiles = neededTiles;

	}

}
