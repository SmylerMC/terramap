package fr.thesmyler.terramap.gui.widgets.map;

import java.util.HashSet;
import java.util.Set;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.Font;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.smylibgui.util.Util;
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
import net.minecraft.util.math.Vec3d;

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

		Font smallFont = Util.getSmallestFont();

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

		GlStateManager.pushMatrix();
		double radRot = Math.toRadians(this.orientation);
		double cosRot = Math.cos(radRot);
		double sinRot = Math.sin(radRot);
		double cosRotAbs = Math.abs(cosRot);
		double sinRotAbs = Math.abs(sinRot);
		double viewPortWidth = cosRotAbs*this.width + sinRotAbs*this.height;
		double viewPortHeight = cosRotAbs*this.height + sinRotAbs*this.width;
		double deltaWidth = -(viewPortWidth - this.width) / 2;
		double deltaHeight = -(viewPortHeight - this.height) / 2;

		// These are the x and y original elementary vectors expressed in the rotated coordinate system
		Vec3d xvec = new Vec3d(cosRot, -sinRot, 0);
		Vec3d yvec = new Vec3d(sinRot, cosRot, 0);

		GlStateManager.translate(this.width / 2, this.height / 2, 0);
		GlStateManager.rotate(this.orientation, 0, 0, 1);
		GlStateManager.translate(-viewPortWidth / 2, -viewPortHeight / 2, 0);


		double upperLeftX = this.getUpperLeftX() + deltaWidth;
		double upperLeftY = this.getUpperLeftY() + deltaHeight;

		Minecraft mc = Minecraft.getMinecraft();
		TextureManager textureManager = mc.getTextureManager();

		int zoomLevel = (int) Math.round(this.zoom);
		double zoomSizeFactor = this.zoom - zoomLevel;

		double renderSize = WebMercatorUtils.TILE_DIMENSIONS / this.tileScaling * Math.pow(2, zoomSizeFactor);

		int maxTileXY = (int) WebMercatorUtils.getDimensionsInTile(zoomLevel);
		double maxX = upperLeftX + viewPortWidth;
		double maxY = upperLeftY + viewPortHeight;

		int lowerTileX = (int) Math.floor(upperLeftX / renderSize);
		int lowerTileY = (int) Math.floor(upperLeftY / renderSize);

		for(int tileX = lowerTileX; tileX * renderSize < maxX; tileX++) {

			for(int tileY = lowerTileY; tileY * renderSize < maxY; tileY++) {

				IRasterTile tile;

				try {
					tile = map.getTile(zoomLevel, Math.floorMod(tileX, maxTileXY), tileY);
				} catch(InvalidTilePositionException silenced) { continue ;}

				// This is the tile we would like to render, but it is not possible if it hasn't been cached yet
				IRasterTile bestTile = tile;
				double dispX = tileX * renderSize - upperLeftX;
				double dispY = tileY * renderSize - upperLeftY;
				double displayWidth = Math.min(renderSize, maxX - tileX * renderSize);
				double displayHeight = Math.min(renderSize, maxY - tileY * renderSize);

				/* 
				 * Let's do some checks to ensure the tile is indeed visible when rotation is taken into account.
				 * To do that, we project each corner of the tile onto the corresponding vector or the non-rotated coordinate system,
				 * and if the result of the projection is further than the limit, we skip the tile
				 */
				Vec3d left;
				Vec3d right;
				Vec3d top;
				Vec3d bottom;
				if(this.orientation < 90) {
					top = new Vec3d(dispX, dispY, 0);
					right = new Vec3d(dispX + displayWidth, dispY, 0);
					bottom = new Vec3d(dispX + displayWidth, dispY + displayHeight, 0);
					left  = new Vec3d(dispX, dispY + displayHeight, 0);
				} else if(this.orientation < 180){
					right = new Vec3d(dispX, dispY, 0);
					bottom = new Vec3d(dispX + displayWidth, dispY, 0);
					left = new Vec3d(dispX + displayWidth, dispY + displayHeight, 0);
					top = new Vec3d(dispX, dispY + displayHeight, 0);
				} else if(this.orientation < 270){
					bottom = new Vec3d(dispX, dispY, 0);
					left = new Vec3d(dispX + displayWidth, dispY, 0);
					top = new Vec3d(dispX + displayWidth, dispY + displayHeight, 0);
					right = new Vec3d(dispX, dispY + displayHeight, 0);
				} else {
					left = new Vec3d(dispX, dispY, 0);
					top = new Vec3d(dispX + displayWidth, dispY, 0);
					right= new Vec3d(dispX + displayWidth, dispY + displayHeight, 0);
					bottom = new Vec3d(dispX, dispY + displayHeight, 0);
				}
				top = top.add(-viewPortWidth / 2, -viewPortHeight/ 2, 0);
				right = right.add(-viewPortWidth / 2, -viewPortHeight/ 2, 0);
				bottom = bottom.add(-viewPortWidth / 2, -viewPortHeight/ 2, 0);
				left = left.add(-viewPortWidth / 2, -viewPortHeight/ 2, 0);
				if(bottom.dotProduct(yvec) < -this.height / 2) continue;
				if(top.dotProduct(yvec) > this.height / 2) continue;
				if(right.dotProduct(xvec) < -this.width / 2) continue;
				if(left.dotProduct(xvec) > this.width / 2) continue;

				neededTiles.add(bestTile);
				boolean lowerResRender = false;
				boolean unlockedZoomRender = false;
				if(!bestTile.isTextureAvailable()) {
					lowerResRender = true;
					perfectDraw = false;
					if(zoomLevel <= this.map.getMaxZoom()) {
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

				double renderSizedSize = renderSize;

				double dX = 0;
				double dY = 0;

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

		GlStateManager.popMatrix();
		profiler.endSection();

	}

}
