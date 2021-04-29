package fr.thesmyler.terramap.gui.widgets.map;

import java.util.HashSet;
import java.util.Set;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.Font;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.terramap.GeoServices;
import fr.thesmyler.terramap.maps.IRasterTile;
import fr.thesmyler.terramap.maps.IRasterTiledMap;
import fr.thesmyler.terramap.maps.imp.UrlRasterTile;
import fr.thesmyler.terramap.maps.utils.TilePos.InvalidTilePositionException;
import fr.thesmyler.terramap.maps.utils.WebMercatorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.profiler.Profiler;
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
	public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
		
		Font smallFont = new Font((float) (1/SmyLibGui.getMinecraftGuiScale()));

		boolean perfectDraw = true;
		Set<IRasterTile> neededTiles = new HashSet<>();

		boolean debug = false;
		MapWidget parentMap = null;
		Profiler profiler = new Profiler();
		if(parent instanceof MapWidget) {
			parentMap = (MapWidget) parent;
			debug = parentMap.isDebugMode();
			profiler = parentMap.getProfiler();
		}
		
		profiler.startSection("render-raster-layer_" + this.map.getId());

		double renderSize = WebMercatorUtils.TILE_DIMENSIONS / this.tileScaling;

		double upperLeftX = this.getUpperLeftX();
		double upperLeftY = this.getUpperLeftY();

		Minecraft mc = Minecraft.getMinecraft();
		TextureManager textureManager = mc.getTextureManager();

		int maxTileXY = (int) WebMercatorUtils.getDimensionsInTile((int)this.zoom);
		double maxX = upperLeftX + this.width;
		double maxY = upperLeftY + this.height;

		int lowerTileX = (int) Math.floor(upperLeftX / renderSize);
		int lowerTileY = (int) Math.floor(upperLeftY / renderSize);

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

				double dispX = tileX * renderSize - upperLeftX;
				double dispY = tileY * renderSize - upperLeftY;

				double renderSizedSize = renderSize;

				double dX = 0;
				double dY = 0;

				double displayWidth = Math.min(renderSize, maxX - tileX * renderSize);
				double displayHeight = Math.min(renderSize, maxY - tileY * renderSize);

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
					dX += factorX * renderSizedSize;
					dY += factorY * renderSizedSize;
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
				RenderUtil.drawModalRectWithCustomSizedTexture(
						x + dispX,
						y + dispY,
						dX, dY,
						displayWidth,
						displayHeight,
						renderSizedSize,
						renderSizedSize
				);
				if(debug) {
					Color lineColor = lowerResRender? unlockedZoomRender? Color.BLUE: Color.RED : Color.WHITE;
					RenderUtil.drawClosedStrokeLine(lineColor, 1f, 
							dispX, dispY,
							dispX, dispY + displayHeight - 1,
							dispX + displayWidth - 1, dispY + displayHeight - 1,
							dispX + displayWidth - 1, dispY
					);
					smallFont.drawCenteredString((float)(dispX + displayWidth/2), (float)(dispY + displayHeight/2), "" + tile.getPosition().getZoom(), lineColor, false);
					smallFont.drawString((float)dispX + 2, (float)(dispY + displayHeight/2), GeoServices.formatGeoCoordForDisplay(dispX), lineColor, false);
					smallFont.drawCenteredString((float)(dispX + displayWidth/2), (float)dispY + 2, GeoServices.formatGeoCoordForDisplay(dispY), lineColor, false);
				}
				GlStateManager.color(1, 1, 1, 1);
			}
		}

		if(perfectDraw && parentMap != null) parentMap.discardPreviousErrors(this);
		this.lastNeededTiles.removeAll(neededTiles);
		this.lastNeededTiles.forEach(tile -> tile.cancelTextureLoading());
		this.lastNeededTiles = neededTiles;
		
		profiler.endSection();

	}

}
