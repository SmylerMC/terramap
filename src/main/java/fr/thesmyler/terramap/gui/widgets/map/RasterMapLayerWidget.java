package fr.thesmyler.terramap.gui.widgets.map;

import java.util.HashSet;
import java.util.Set;

import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.terramap.maps.TiledMap;
import fr.thesmyler.terramap.maps.WebTile;
import fr.thesmyler.terramap.maps.WebTile.InvalidTileCoordinatesException;
import fr.thesmyler.terramap.maps.utils.WebMercatorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;

//TODO Fractional zoom
public class RasterMapLayerWidget extends MapLayerWidget {

	protected TiledMap map;
	private Set<WebTile> neededTiles = new HashSet<>();

	public RasterMapLayerWidget(TiledMap map, double tileScaling) {
		super(tileScaling);
		this.map = map;
	}
	
	public TiledMap getMap() {
		return this.map;
	}

	@Override
	public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean focused, Screen parent) {
		
		boolean debug = false;
		if(parent instanceof MapWidget) {
			debug = ((MapWidget) parent).isDebugMode();
		}
		
		Set<WebTile> neededNow = new HashSet<>();

		//TODO Remove the lines when tile scaling is not a power of 2
		double renderSize = WebMercatorUtils.TILE_DIMENSIONS / this.tileScaling;
		
		long upperLeftX = (long) this.getUpperLeftX();
		long upperLeftY = (long) this.getUpperLeftY();

		Minecraft mc = Minecraft.getMinecraft();
		TextureManager textureManager = mc.getTextureManager();

		int maxTileXY = (int) map.getSizeInTiles((int)this.zoom);
		long maxX = (long) (upperLeftX + this.width);
		long maxY = (long) (upperLeftY + this.height);

		int lowerTileX = (int) Math.floor((double)upperLeftX / (double)renderSize);
		int lowerTileY = (int) Math.floor((double)upperLeftY / (double)renderSize);

		for(int tileX = lowerTileX; tileX * renderSize < maxX; tileX++) {

			for(int tileY = lowerTileY; tileY * renderSize < maxY; tileY++) {

				WebTile tile;

				try {
					tile = map.getTile((int)this.zoom, Math.floorMod(tileX, maxTileXY), tileY);
				} catch(InvalidTileCoordinatesException e) { continue ;}
				
				//This is the tile we would like to render, but it is not possible if it hasn't been cached yet
				WebTile bestTile = tile;
				neededNow.add(tile);
				boolean lowerResRender = false;
				boolean unlockedZoomRender = false;
				if(!tile.hasTexture()) {
					lowerResRender = true;
					if(!tile.isWaitingForTexture()) {
						if(this.zoom <= this.map.getMaxZoom()) {
							tile.getTexture(); // Will request the texture if not already waiting for it
						} else {
							unlockedZoomRender = true;
						}
					}
						
					while(tile.getZoom() > 0 && !tile.hasTexture()) {
						tile = this.map.getTile(tile.getZoom()-1, tile.getX() /2, tile.getY() /2);
					}
				}

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
					int sizeFactor = (1 <<(bestTile.getZoom() - tile.getZoom()));

					int xInBiggerTile = (int) (bestTile.getX() - sizeFactor * tile.getX());
					int yInBiggerTile = (int) (bestTile.getY() - sizeFactor * tile.getY());

					double factorX = (double)xInBiggerTile / (double)sizeFactor;
					double factorY = (double)yInBiggerTile / (double)sizeFactor;
					renderSizedSize *= sizeFactor;
					dX += (int) (factorX * renderSizedSize);
					dY += (int) (factorY * renderSizedSize);
				}

				GlStateManager.color(1, 1, 1, 1);
				textureManager.bindTexture(tile.getTexture());
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
				}
				GlStateManager.color(1, 1, 1, 1);

			}
		}
		this.neededTiles.forEach((tile) -> {if(!neededNow.contains(tile)) tile.cancelLoading();});
		this.neededTiles = neededNow;
	}

}
