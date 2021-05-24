package fr.thesmyler.terramap.gui.widgets.map;

import java.util.HashSet;
import java.util.Set;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.Font;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.smylibgui.util.Util;
import fr.thesmyler.terramap.maps.ICopyrightHolder;
import fr.thesmyler.terramap.maps.IRasterTile;
import fr.thesmyler.terramap.maps.IRasterTiledMap;
import fr.thesmyler.terramap.maps.imp.UrlRasterTile;
import fr.thesmyler.terramap.maps.utils.TilePos;
import fr.thesmyler.terramap.maps.utils.TilePos.InvalidTilePositionException;
import fr.thesmyler.terramap.maps.utils.WebMercatorUtils;
import fr.thesmyler.terramap.util.GeoServices;
import fr.thesmyler.terramap.util.Mat2d;
import fr.thesmyler.terramap.util.Vec2d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class RasterMapLayer extends MapLayer implements ICopyrightHolder {

	protected IRasterTiledMap map;
	protected Set<IRasterTile> lastNeededTiles = new HashSet<>();

	public RasterMapLayer(IRasterTiledMap map, double tileScaling) {
		super(tileScaling);
		this.map = map;
	}

	public IRasterTiledMap getMap() {
		return this.map;
	}

	@Override
	public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {

		Font smallFont = Util.getSmallestFont();
		Minecraft mc = Minecraft.getMinecraft();
		TextureManager textureManager = mc.getTextureManager();
		float rotation = this.getRotation();

		boolean perfectDraw = true;
		Set<IRasterTile> neededTiles = new HashSet<>();

		MapWidget parentMap = (MapWidget) parent;
		boolean debug = parentMap.isDebugMode();
		Profiler profiler = parentMap.getProfiler();

		profiler.startSection("render-raster-layer_" + this.map.getId());

		GlStateManager.pushMatrix();
		float widthViewPort = this.getWidth();
		float heightViewPort = this.getHeight();
		double zoom = this.getZoom();
		
		// The width and height of the rotated map that covers the area of the non-rotated one
		double extendedWidth = this.getExtendedWidth();
		double extendedHeight = this.getExtendedHeight();

		// These are the x and y original elementary vectors expressed in the rotated coordinate system
		Mat2d rotationMatrix = this.getRotationMatrix();
		Vec2d xvec = rotationMatrix.line1();
		Vec2d yvec = rotationMatrix.line2();

		this.applyRotationGl(x, y);

		double upperLeftX = this.getUpperLeftX();
		double upperLeftY = this.getUpperLeftY();

		int zoomLevel = (int) Math.round(zoom);
		double zoomSizeFactor = zoom - zoomLevel;

		double renderSize = WebMercatorUtils.TILE_DIMENSIONS / this.getTileScaling() * Math.pow(2, zoomSizeFactor);

		int maxTileXY = (int) WebMercatorUtils.getDimensionsInTile(zoomLevel);
		double maxX = upperLeftX + extendedWidth;
		double maxY = upperLeftY + extendedHeight;

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
				 * To do that, we project each corner of the tile onto the corresponding unit vector or the non-rotated coordinate system,
				 * and if the result of the projection is further than the limit, we skip the tile.
				 */
				Vec2d left;
				Vec2d right;
				Vec2d top;
				Vec2d bottom;
				if(rotation < 90) {
					top = new Vec2d(dispX, dispY);
					right = new Vec2d(dispX + displayWidth, dispY);
					bottom = new Vec2d(dispX + displayWidth, dispY + displayHeight);
					left  = new Vec2d(dispX, dispY + displayHeight);
				} else if(rotation < 180){
					right = new Vec2d(dispX, dispY);
					bottom = new Vec2d(dispX + displayWidth, dispY);
					left = new Vec2d(dispX + displayWidth, dispY + displayHeight);
					top = new Vec2d(dispX, dispY + displayHeight);
				} else if(rotation < 270){
					bottom = new Vec2d(dispX, dispY);
					left = new Vec2d(dispX + displayWidth, dispY);
					top = new Vec2d(dispX + displayWidth, dispY + displayHeight);
					right = new Vec2d(dispX, dispY + displayHeight);
				} else {
					left = new Vec2d(dispX, dispY);
					top = new Vec2d(dispX + displayWidth, dispY);
					right= new Vec2d(dispX + displayWidth, dispY + displayHeight);
					bottom = new Vec2d(dispX, dispY + displayHeight);
				}
				top = top.add(-extendedWidth / 2, -extendedHeight/ 2);
				right = right.add(-extendedWidth / 2, -extendedHeight/ 2);
				bottom = bottom.add(-extendedWidth / 2, -extendedHeight/ 2);
				left = left.add(-extendedWidth / 2, -extendedHeight/ 2);
				if(bottom.dotProd(yvec) < -heightViewPort / 2) continue;
				if(top.dotProd(yvec) > heightViewPort / 2) continue;
				if(right.dotProd(xvec) < -widthViewPort / 2) continue;
				if(left.dotProd(xvec) > widthViewPort / 2) continue;

				neededTiles.add(bestTile);
				boolean lowerResRender = false;
				boolean unlockedZoomRender = false;
				if(!bestTile.isTextureAvailable()) {
					lowerResRender = true;
					perfectDraw = false;
					if(zoomLevel > this.map.getMaxZoom()) {
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
						dispX,
						dispY,
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

		if(zoomLevel <= this.map.getMaxZoom()) {
			double centerX = WebMercatorUtils.getXFromLongitude(this.getCenterLongitude(), 0d) / 256d;
			double centerY = WebMercatorUtils.getYFromLatitude(this.getCenterLatitude(), 0d) / 256d;
			Vec2d minusCenterPos = new Vec2d(-centerX, -centerY);
			neededTiles.stream().filter(t -> !t.isTextureAvailable()).sorted((t1, t2) -> {
				TilePos pos1 = t1.getPosition();
				TilePos pos2 = t2.getPosition();
				int dz = Integer.compare(pos1.getZoom(), pos2.getZoom());
				if(dz != 0) return dz;
				double factor = 1d / (1 << pos1.getZoom());
				Vec2d dis1 = new Vec2d(pos1.getX() + 0.5d, pos1.getY() + 0.5d).scale(factor).add(minusCenterPos);
				Vec2d dis2 = new Vec2d(pos2.getX() + 0.5d, pos2.getY() + 0.5d).scale(factor).add(minusCenterPos);
				return Double.compare(dis1.normSquared(), dis2.normSquared());
			}).forEachOrdered(tile -> {
				try {
					tile.getTexture(); // Will start loading the tile
				} catch (Throwable e) {
					if(parentMap != null) {
						parentMap.reportError(this, e.toString());
					}
				}
			});
		}
		if(perfectDraw && parentMap != null) parentMap.discardPreviousErrors(this);
		this.lastNeededTiles.removeAll(neededTiles);
		this.lastNeededTiles.forEach(tile -> tile.cancelTextureLoading());
		this.lastNeededTiles = neededTiles;

		GlStateManager.popMatrix();
		profiler.endSection();

	}

	@Override
	public ITextComponent getCopyright(String localeKey) {
		if(this.map instanceof ICopyrightHolder) {
			return ((ICopyrightHolder)this.map).getCopyright(localeKey);
		}
		return new TextComponentString("");
	}

}
