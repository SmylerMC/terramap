package fr.smyler.terramap.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import fr.smyler.terramap.TerramapMod;
import fr.smyler.terramap.gui.widgets.RightClickMenu;
import fr.smyler.terramap.input.KeyBindings;
import fr.smyler.terramap.maps.TiledMap;
import fr.smyler.terramap.maps.tiles.RasterWebTile;
import fr.smyler.terramap.maps.tiles.RasterWebTile.InvalidTileCoordinatesException;
import fr.smyler.terramap.maps.utils.TerramapUtils;
import fr.smyler.terramap.maps.utils.WebMercatorUtils;
import io.github.terra121.EarthBiomeProvider;
import io.github.terra121.projection.GeographicProjection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;

//TODO Better zoom
//TODO Custom scaling
//TODO Localization
public class GuiTiledMap extends GuiScreen {

	protected boolean visible;
	protected boolean hovered;

	protected TiledMap<?> map;

	protected double focusLatitude;
	protected double focusLongitude;
	protected double lastMouseLong, lastMouseLat = 0;
	protected boolean debug = false; //Show tiles borders or not

	protected int zoomLevel;

	private RightClickMenu rclickMenu;

	public GuiTiledMap(TiledMap<?> map) {
		this.visible = true;
		this.hovered = false;
		this.map = map;
		this.zoomLevel = map.getZoomLevel();
		this.setZoom(13);
		this.focusLatitude = 0;
		this.focusLongitude = 0;
		this.rclickMenu = new RightClickMenu();
	}

	@Override
	public void initGui() {
		GeographicProjection proj = ((EarthBiomeProvider)Minecraft.getMinecraft().getIntegratedServer().getWorld(0).getBiomeProvider()).projection;
		EntityPlayerSP p = Minecraft.getMinecraft().player;
		double coords[] = proj.toGeo(p.posX, p.posZ);
		this.focusLatitude = coords[1];
		this.focusLongitude = coords[0];
		this.rclickMenu.init(fontRenderer);
		this.rclickMenu.addEntry("Teleport here", () -> {TerramapMod.logger.info("teleport!");});
		this.rclickMenu.addEntry("Center here", () -> {this.setPosition(this.lastMouseLong, this.lastMouseLat);});
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.handleMouseInput(mouseX, mouseY, partialTicks);
		this.drawMap(mouseX, mouseY, partialTicks);
		this.drawInformation(mouseX, mouseY, partialTicks);
		this.drawCopyright(mouseX, mouseY, partialTicks);
		this.rclickMenu.draw(mouseX, mouseY, partialTicks);
	}

	private void drawMap(int mouseX, int mouseY, float partialTicks) {

		if((int)this.zoomLevel != this.map.getZoomLevel()) {
			TerramapMod.logger.info("Zooms are differents: GUI: " + this.zoomLevel + " | Map: " + this.map.getZoomLevel());
		}
		int renderSize = WebMercatorUtils.TILE_DIMENSIONS;

		long upperLeftX = this.getUpperLeftX(this.zoomLevel, this.focusLongitude);
		long upperLeftY = this.getUpperLeftY(this.zoomLevel, this.focusLatitude);

		Minecraft mc = Minecraft.getMinecraft();
		TextureManager textureManager = mc.getTextureManager();

		int maxTileXY = (int) map.getSizeInTiles();
		long maxX = (long) (upperLeftX + this.width);
		long maxY = (long) (upperLeftY + this.height);

		int lowerTX = (int) Math.floor((double)upperLeftX / (double)renderSize);
		int lowerTY = (int) Math.floor((double)upperLeftY / (double)renderSize);

		for(int tX = lowerTX; tX * renderSize < maxX; tX++) {

			for(int tY = lowerTY; tY * renderSize < maxY; tY++) {

				RasterWebTile tile;

				try {
					tile = map.getTile(TerramapUtils.modulus(tX, maxTileXY), tY, this.zoomLevel);
				} catch(InvalidTileCoordinatesException e) { continue ;}
				//This is the tile we would like to render, but it is not possible if it hasn't been cached yet
				RasterWebTile bestTile = tile;
				boolean lowerResRender = false;

				if(!TerramapMod.cacheManager.isCached(tile)) {
					lowerResRender = true;
					if(!TerramapMod.cacheManager.isBeingCached(tile))
						TerramapMod.cacheManager.cacheAsync(tile);
					while(tile.getZoom() > 0 && !TerramapMod.cacheManager.isCached(tile)) {
						tile = this.map.getTile(tile.getX() /2, tile.getY() /2, tile.getZoom()-1);
					}
				}

				int dispX = Math.round(tX * renderSize - upperLeftX);
				int displayWidth = (int) Math.min(renderSize, maxX - tX * renderSize);

				int displayHeight = (int) Math.min(renderSize, maxY - tY * renderSize);
				int dispY = Math.round(tY * renderSize - upperLeftY);

				int renderSizedSize = renderSize;

				int dX = 0;
				int dY = 0;

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

				if(tX == lowerTX) {
					dX -= dispX;
					dispX = 0;
				}

				if(tY == lowerTY) {
					dY -= dispY;
					dispY = 0;
				}

				textureManager.bindTexture(tile.getTexture());
				drawModalRectWithCustomSizedTexture(
						dispX,
						dispY,
						dX, dY,
						displayWidth,
						displayHeight,
						renderSizedSize,
						renderSizedSize);

				if(this.debug) {
					final int RED = 0xFFFF0000;
					final int WHITE = 0xFFFFFFFF;
					this.drawHorizontalLine(
							dispX,
							dispX + displayWidth - 1,
							dispY,
							lowerResRender? RED : WHITE);
					this.drawHorizontalLine(
							dispX,
							dispX + displayWidth - 1,
							dispY + displayHeight - 1,
							lowerResRender? RED : WHITE);
					this.drawVerticalLine(
							dispX,
							dispY,
							dispY + displayHeight - 1,
							lowerResRender? RED : WHITE);
					this.drawVerticalLine(
							dispX + displayWidth - 1,
							dispY,
							dispY + displayHeight - 1,
							lowerResRender? RED : WHITE);
				}
				GlStateManager.color(255, 255, 255, 255);


			}

		}

	}

	private void drawInformation(int mouseX, int mouseY, float partialTicks) {
		List<String> lines = new ArrayList<String>();
		String dispLat = "" + (float)Math.round(this.lastMouseLat * 100000) / 100000; //TODO Better formating
		String dispLong = "" + (float)Math.round(this.lastMouseLong * 100000) / 100000;
		lines.add("Map position: " + dispLat + " " + dispLong);
		lines.add("Zoom level: " + this.zoomLevel);
		if(this.debug) {
			lines.add("Cache queue: " + TerramapMod.cacheManager.getQueueSize());
			lines.add("Loaded tiles: " + this.map.getLoadedCount() + "/" + this.map.getMaxLoad());
		}

		Gui.drawRect(0, 0, 180, lines.size() * (this.fontRenderer.FONT_HEIGHT + 10) + 10 , 0x80000000);
		int i = 0;
		for(String line: lines) this.drawString(this.fontRenderer, line, 10, 10*i++ + this.fontRenderer.FONT_HEIGHT * i, 0xFFFFFF);
	}

	private void drawCopyright(int mouseX, int mouseY, float partialTicks) {
		String copyrightString = "© OpenStreetMap contributors";
		int rectWidth = 10 + this.fontRenderer.getStringWidth(copyrightString);
		int rectHeight = this.fontRenderer.FONT_HEIGHT + 10;
		Gui.drawRect(this.width - rectWidth, this.height - rectHeight, this.width, this.height, 0x50000000);
		this.drawString(this.fontRenderer, copyrightString, this.width - rectWidth + 5, this.height - rectHeight + 5, 0xFFFFFF);
	}

	@Override
	public void updateScreen(){
		if(!this.isPositionValid(this.zoomLevel, this.focusLongitude, this.focusLatitude)) {
			TerramapMod.logger.error("Map is in an invalid state! Reseting!");
			this.setZoomToMinimum();
		}
	}		

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		if(keyCode == KeyBindings.TOGGLE_DEBUG.getKeyCode()) this.debug = !this.debug;
	}

	/**
	 * Handles mouse input.
	 */
	public void handleMouseInput(int mouseX, int mouseY, float partialTicks){

		boolean lclick = Mouse.isButtonDown(0);
		boolean rclick = Mouse.isButtonDown(1);

		if(rclick) {
			this.rclickMenu.showAt(mouseX, mouseY); //TODO Make sure it fits on screen
		}
		
		if(this.rclickMenu.isDisplayed()) {
			if(lclick) {
				this.rclickMenu.onMouseClick(mouseX, mouseY);
				this.rclickMenu.hide();
			}
		} else {
			//Moving
			if(lclick) {
				//TODO This should adapt to the zoom level
				int dX = Mouse.getDX();
				int dY = Mouse.getDY();

				double nlon = this.focusLongitude - dX/Math.pow(2, this.zoomLevel)/2;
				double nlat = this.focusLatitude - dY/Math.pow(2, this.zoomLevel)/2;
				this.setLongitude(nlon);
				this.setLatitude(nlat);
			} else {
				//Scrolling
				int i = Mouse.getDWheel();
				int z;
				if (i != 0){
					if (i > 0) z = 1;
					else z = - 1;
					this.zoom(z);
				}
				this.lastMouseLong = this.getScreenLong(mouseX);
				this.lastMouseLat = this.getScreenLat(mouseY);
			}

		}

	}

	@Override
	public void onGuiClosed() {
		this.map.unloadAll();
	}

	public void zoom(int val) {

		int nzoom = this.zoomLevel + val;
		if(!this.isPositionValid(nzoom, this.focusLongitude, this.focusLatitude)) return;

		TerramapMod.cacheManager.clearQueue(); // We are displaying new tiles, we don't need what we needed earlier
		this.zoomLevel = nzoom;
		//}
		this.setTiledMapZoom();

		//FIXME
	}


	public void setZoomToMinimum() {
		int i = this.zoomLevel;
		while(!this.isPositionValid(i, 0, 0)) i++;
		this.setZoom(i);
		this.setPosition(0, 0);
	}

	private void setTiledMapZoom() {
		this.map.setZoomLevel((int)this.zoomLevel);
	}

	/**
	 * 
	 * @param zoom
	 * @return The size of the full map, in pixel
	 */
	private long getMaxMapSize(int zoom) {
		return (long) (WebMercatorUtils.getDimensionsInTile(zoom) * WebMercatorUtils.TILE_DIMENSIONS);
	}

	/* === Getters and Setters from this point === */

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public boolean setPosition(double lon, double lat) {
		if(!this.isPositionValid(this.zoomLevel, lon, lat)) return false;
		this.focusLongitude = lon;
		this.focusLatitude = lat;
		return true;
	}

	public boolean setLongitude(double lon) {
		if(!this.isPositionValid(this.zoomLevel, lon, this.focusLatitude)) return false;
		this.focusLongitude = lon;
		return true;
	}

	public boolean setLatitude(double lat) {
		if(!this.isPositionValid(this.zoomLevel, this.focusLongitude, lat)) return false;
		this.focusLatitude = lat;
		return true;
	}

	private void setZoom(int zoom) {
		this.zoomLevel = zoom;
		this.setTiledMapZoom();
	}

	private long getUpperLeftX(int zoomLevel, double centerLong) {
		return (long)(
				(double)(WebMercatorUtils.getXFromLongitude(centerLong, zoomLevel))
				- ((double)this.width) / 2f);
	}

	private long getUpperLeftY(int zoomLevel, double centerLat) {
		return (long)(
				(double)WebMercatorUtils.getYFromLatitude(centerLat, zoomLevel)
				- (double)this.height / 2f);
	}

	private boolean isPositionValid(int zoomLevel, double centerLong, double centerLat) {
		if(zoomLevel < 0) return false;
		if(zoomLevel > 19) return false;
		long upperLeftY = this.getUpperLeftY(zoomLevel, centerLat);
		long lowerLeftY = (long) (upperLeftY + this.height);
		if(upperLeftY < 0) return false;
		if(lowerLeftY > this.getMaxMapSize(zoomLevel)) return false;
		return true;
	}

	private double getScreenLong(int xOnScreen) {
		long xOnMap = this.getUpperLeftX(this.zoomLevel, this.focusLongitude) + xOnScreen;
		return WebMercatorUtils.getLongitudeFromX(xOnMap, this.zoomLevel);
	}

	private double getScreenLat(int yOnScreen) {
		long yOnMap = this.getUpperLeftY(this.zoomLevel, this.focusLatitude) + yOnScreen;
		return WebMercatorUtils.getLatitudeFromY(yOnMap, this.zoomLevel);
	}

}
