package fr.smyler.terramap.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.lwjgl.input.Mouse;

import fr.smyler.terramap.GeoServices;
import fr.smyler.terramap.TerramapConfiguration;
import fr.smyler.terramap.TerramapMod;
import fr.smyler.terramap.gui.widgets.RightClickMenu;
import fr.smyler.terramap.gui.widgets.poi.EntityPOI;
import fr.smyler.terramap.input.KeyBindings;
import fr.smyler.terramap.maps.TiledMap;
import fr.smyler.terramap.maps.tiles.RasterWebTile;
import fr.smyler.terramap.maps.tiles.RasterWebTile.InvalidTileCoordinatesException;
import fr.smyler.terramap.maps.utils.TerramapUtils;
import fr.smyler.terramap.maps.utils.WebMercatorUtils;
import io.github.terra121.EarthGeneratorSettings;
import io.github.terra121.projection.GeographicProjection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.World;

//TODO Better zoom
//TODO Localization
public class GuiTiledMap extends GuiScreen {

	protected TiledMap<?> map;

	protected double focusLatitude;
	protected double focusLongitude;
	protected int zoomLevel;
	protected double lastMouseLong, lastMouseLat = 0;
	protected EarthGeneratorSettings genSettings;
	protected GeographicProjection projection;
	protected boolean debug = false; //Show tiles borders or not

	protected RightClickMenu rclickMenu;

	protected Map<UUID, EntityPOI> entityPOIs;
	protected World world; 

	public GuiTiledMap(TiledMap<?> map, World world) {
		this.map = map;
		this.zoomLevel = map.getZoomLevel();
		this.focusLatitude = 0;
		this.focusLongitude = 0;
		this.rclickMenu = new RightClickMenu();
		this.world = world;
	}

	@Override
	public void initGui() {
		EntityPlayerSP p = Minecraft.getMinecraft().player;
		this.genSettings = TerramapMod.proxy.getCurrentEarthGeneratorSettings(null); //We are on client, world is not needed
		if(this.genSettings != null) {
			this.projection = this.genSettings.getProjection();
			double coords[] = this.projection.toGeo(p.posX, p.posZ);
			this.focusLatitude = coords[1];
			this.focusLongitude = coords[0];
			this.setZoom(13);
		} else {
			TerramapMod.logger.info("Projection was not available");
			this.focusLatitude = 0;
			this.focusLongitude = 0;
			this.setZoomToMinimum();
		}
		this.entityPOIs = new HashMap<UUID, EntityPOI>();
		this.entityPOIs.put(p.getPersistentID(), new EntityPOI(p)); //TODO TEMP TEST
		this.rclickMenu.init(fontRenderer);
		this.rclickMenu.addEntry("Teleport here", () -> {this.teleportPlayerTo(this.lastMouseLong, this.lastMouseLat);}); //TODO implement teleport from map
		this.rclickMenu.addEntry("Center map here", () -> {this.setPosition(this.lastMouseLong, this.lastMouseLat);});
		this.rclickMenu.addEntry("Copy location to clipboard", () -> {GuiScreen.setClipboardString("" + this.lastMouseLong + " " + this.lastMouseLat);});
		this.rclickMenu.addEntry("Open location in Google Maps", () -> {GeoServices.openInGoogleMaps(this.zoomLevel, this.lastMouseLong, this.lastMouseLat);});
		this.rclickMenu.addEntry("Open location in OpenStreetMaps", () -> {GeoServices.openInOSMWeb(this.zoomLevel, this.lastMouseLong, this.lastMouseLat);});
		//TODO Open in google Earth
		//TODO Copy Minecraft coordinates to clipboard
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.handleMouseInput(mouseX, mouseY, partialTicks);
		this.drawMap(mouseX, mouseY, partialTicks);
		this.updatePOIs(); //TODO No needed at each frame
		this.drawPOIs(mouseX, mouseY, partialTicks);
		this.drawInformation(mouseX, mouseY, partialTicks);
		this.drawCopyright(mouseX, mouseY, partialTicks);
		this.rclickMenu.draw(mouseX, mouseY, partialTicks);
	}

	private void drawMap(int mouseX, int mouseY, float partialTicks) {

		if((int)this.zoomLevel != this.map.getZoomLevel()) {
			TerramapMod.logger.info("Zooms are differents: GUI: " + this.zoomLevel + " | Map: " + this.map.getZoomLevel());
		}
		int renderSize = (int) (WebMercatorUtils.TILE_DIMENSIONS * TerramapConfiguration.tileScaling);

		long upperLeftX = this.getUpperLeftX(this.focusLongitude);
		long upperLeftY = this.getUpperLeftY(this.focusLatitude);

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
		lines.add("Mouse position: " + dispLat + " " + dispLong);
		lines.add("Zoom level: " + this.zoomLevel);
		if(this.debug) {
			lines.add("Cache queue: " + TerramapMod.cacheManager.getQueueSize());
			lines.add("Loaded tiles: " + this.map.getLoadedCount() + "/" + this.map.getMaxLoad());
			lines.add("Projection: " + this.projection);
			lines.add(Minecraft.getDebugFPS() + "FPS");
		}

		Gui.drawRect(0, 0, 200, lines.size() * (this.fontRenderer.FONT_HEIGHT + 10) + 10 , 0x80000000);
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

	private void drawPOIs(int mouseX, int mouseY, float partialTicks) {
		for(EntityPOI poi: this.entityPOIs.values()) {
			long x = this.getScreenX(poi.getLongitude());
			if(x > this.width || x < 0) continue;
			long y = this.getScreenY(poi.getLatitude());
			if(y > this.height || y < 0) continue;
			poi.draw((int)x, (int)y);
		}
	}

	private void updatePOIs() {
		for(EntityPOI poi: this.entityPOIs.values()) poi.updatePosition(this.projection);
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

		//TODO Use vanilla methods

		boolean lclick = Mouse.isButtonDown(0);
		boolean rclick = Mouse.isButtonDown(1);

		if(rclick) {

			int displayX = mouseX;
			int displayY = mouseY;
			int rClickWidth = this.rclickMenu.getWidth();
			int rClickHeight = this.rclickMenu.getHeight();
			displayX = mouseX + rClickWidth > this.width ? this.width - rClickWidth: mouseX;
			displayY = mouseY + rClickHeight > this.height ? this.height - rClickHeight: mouseY;
			this.rclickMenu.showAt(displayX, displayY); //TODO Make sure it fits on screen
		}

		if(this.rclickMenu.isDisplayed()) {
			if(lclick) {
				this.rclickMenu.onMouseClick(mouseX, mouseY);
				this.rclickMenu.hide();
			}
		} else {
			//Moving
			if(lclick) {
				//TODO This should adapt to the zoom level and should be in its own method to add keyboard controls
				int dX = (int) (Mouse.getDX() / TerramapConfiguration.tileScaling);
				int dY = (int) (Mouse.getDY() / TerramapConfiguration.tileScaling);

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
				this.lastMouseLong = (float)Math.round(this.getScreenLong(mouseX) * 100000) / 100000;
				this.lastMouseLat = (float)Math.round(this.getScreenLat(mouseY) * 100000) / 100000;
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
	 * @return The size of the full map, in pixel on screen
	 */
	private long getMaxMapSize(int zoom) {
		return (long) (WebMercatorUtils.getDimensionsInTile(zoom) * WebMercatorUtils.TILE_DIMENSIONS * TerramapConfiguration.tileScaling);
	}

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

	private long getMapX(double longitude) {
		return this.getMapX(this.zoomLevel, longitude);
	}

	private long getMapY(double latitude) {
		return this.getMapY(this.zoomLevel, latitude);
	}

	private long getMapX(int zoomLevel, double longitude) {
		return (long) (WebMercatorUtils.getXFromLongitude(longitude, zoomLevel) * TerramapConfiguration.tileScaling);

	}

	private long getMapY(int zoomLevel, double latitude) {
		return (long)(WebMercatorUtils.getYFromLatitude(latitude, zoomLevel) * TerramapConfiguration.tileScaling);
	}

	private long getScreenX(double longitude) {
		return this.getMapX(longitude) -  this.getUpperLeftX();
	}

	private long getScreenY(double latitude) {
		return this.getMapY(latitude) - this.getUpperLeftY();
	}

	private long getUpperLeftX(double centerLon) {
		return this.getUpperLeftX(this.zoomLevel, centerLon);
	}

	private long getUpperLeftY(double centerLat) {
		return this.getUpperLeftY(this.zoomLevel, centerLat);
	}

	private long getUpperLeftX(int zoomLevel, double centerLon) {
		return this.getMapX(centerLon) - this.width / 2;
	}

	private long getUpperLeftY(int zoomLevel, double centerLat) {
		return this.getMapY(centerLat) - this.height / 2;
	}
	
	private long getUpperLeftX() {
		return this.getUpperLeftX(this.focusLongitude);
	}

	private long getUpperLeftY() {
		return this.getUpperLeftY(this.focusLatitude);
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
		long xOnMap = (long) ((this.getUpperLeftX(this.zoomLevel, this.focusLongitude) + xOnScreen) / TerramapConfiguration.tileScaling);
		return WebMercatorUtils.getLongitudeFromX(xOnMap, this.zoomLevel);
	}

	private double getScreenLat(int yOnScreen) {
		long yOnMap = (long) ((this.getUpperLeftY(this.zoomLevel, this.focusLatitude) + yOnScreen) / TerramapConfiguration.tileScaling);
		return WebMercatorUtils.getLatitudeFromY(yOnMap, this.zoomLevel);
	}

	private void teleportPlayerTo(double longitude, double latitude) {
		Minecraft.getMinecraft().player.sendChatMessage(TerramapConfiguration.tpllcmd.replace("{latitude}", "" + latitude).replace("{longitude}", "" + longitude));
	}

}
