package fr.smyler.terramap.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.lwjgl.input.Mouse;

import fr.smyler.terramap.GeoServices;
import fr.smyler.terramap.TerramapConfiguration;
import fr.smyler.terramap.TerramapMod;
import fr.smyler.terramap.gui.widgets.RightClickMenu;
import fr.smyler.terramap.gui.widgets.poi.EntityPOI;
import fr.smyler.terramap.gui.widgets.poi.PlayerPOI;
import fr.smyler.terramap.gui.widgets.poi.PointOfInterest;
import fr.smyler.terramap.input.KeyBindings;
import fr.smyler.terramap.maps.TiledMap;
import fr.smyler.terramap.maps.tiles.RasterWebTile;
import fr.smyler.terramap.maps.tiles.RasterWebTile.InvalidTileCoordinatesException;
import fr.smyler.terramap.maps.utils.TerramapUtils;
import fr.smyler.terramap.maps.utils.WebMercatorUtils;
import io.github.terra121.EarthGeneratorSettings;
import io.github.terra121.projection.GeographicProjection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

//TODO Better zoom
//TODO Localization
public class GuiTiledMap extends GuiScreen {

	protected TiledMap<?> map;

	protected double focusLatitude;
	protected double focusLongitude;
	protected int zoomLevel;
	protected double mouseLong, mouseLat = 0;
	protected int lastMouseClickX, lastMouseClickY = -1;
	protected EarthGeneratorSettings genSettings;
	protected GeographicProjection projection;

	protected boolean debug = false; //Show tiles borders or not

	protected RightClickMenu rclickMenu;

	protected Map<UUID, EntityPOI> entityPOIs;
	protected Map<UUID, PlayerPOI> playerPOIs; //Tracked players, excluding ourself
	protected PlayerPOI thePlayerPOI;
	protected int lastEntityPoiRenderedCount = 0;
	protected int lastPlayerPoiRenderedCount = 0;
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
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		this.genSettings = TerramapMod.proxy.getCurrentEarthGeneratorSettings(null); //We are on client, world is not needed
		if(this.genSettings != null) {
			this.projection = this.genSettings.getProjection();
			double coords[] = this.projection.toGeo(player.posX, player.posZ);
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
		this.playerPOIs = new HashMap<UUID, PlayerPOI>();
		this.thePlayerPOI = new PlayerPOI((AbstractClientPlayer)player);
		this.rclickMenu.init(fontRenderer);
		this.rclickMenu.addEntry("Teleport here", () -> {this.teleportPlayerTo(this.mouseLong, this.mouseLat);});
		this.rclickMenu.addEntry("Center map here", () -> {this.setPosition(this.mouseLong, this.mouseLat);});
		this.rclickMenu.addEntry("Copy location to clipboard", () -> {GuiScreen.setClipboardString("" + this.mouseLong + " " + this.mouseLat);});
		this.rclickMenu.addEntry("Open location in Google Maps", () -> {GeoServices.openInGoogleMaps(this.zoomLevel, this.mouseLong, this.mouseLat);});
		this.rclickMenu.addEntry("Open location in OpenStreetMaps", () -> {GeoServices.openInOSMWeb(this.zoomLevel, this.mouseLong, this.mouseLat);});
		//TODO Open in google Earth
		//TODO Copy Minecraft coordinates to clipboard
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawMap(mouseX, mouseY, partialTicks);
		if(this.projection != null) this.drawPOIs(mouseX, mouseY, partialTicks);
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
		String dispLat = GeoServices.formatGeoCoordForDisplay(this.mouseLat) + "°";
		String dispLong = GeoServices.formatGeoCoordForDisplay(this.mouseLong)  + "°";
		lines.add("Mouse position: " + dispLat + " " + dispLong);
		lines.add("Zoom level: " + this.zoomLevel);
		if(this.debug) {
			lines.add("Cache queue: " + TerramapMod.cacheManager.getQueueSize());
			lines.add("Loaded tiles: " + this.map.getLoadedCount() + "/" + this.map.getMaxLoad());
			if(this.genSettings != null) lines.add("Projection: " + this.genSettings.settings.projection);
			int playerPOICount = this.playerPOIs.size();
			if(this.thePlayerPOI != null) playerPOICount++;
			lines.add("FPS:" + Minecraft.getDebugFPS() +
					" ePOIs: " + this.lastEntityPoiRenderedCount +"/" + this.entityPOIs.size() +
					" pPOIs: " + this.lastPlayerPoiRenderedCount +"/" + playerPOICount);
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
		this.lastEntityPoiRenderedCount = 0;
		this.lastPlayerPoiRenderedCount = 0;
		boolean mainPlayerRendered = false;
		long playerX = 0;
		long playerY = 0;
		int hoverPOIX = 0;
		int hoverPOIY = 0;
		PointOfInterest hoveredPOI = null;
		if(this.thePlayerPOI != null) {
			playerX = this.getScreenX(this.thePlayerPOI.getLongitude());
			playerY = this.getScreenY(this.thePlayerPOI.getLatitude());
			mainPlayerRendered = this.isPoiBBOnScreen(playerX, playerY, this.thePlayerPOI);
		}
		for(EntityPOI poi: this.entityPOIs.values()) {
			long lx = this.getScreenX(poi.getLongitude());
			long ly = this.getScreenY(poi.getLatitude());
			if(!this.isPoiBBOnScreen(lx, ly, poi)) continue;
			int ix = (int)lx;
			int iy = (int)ly;
			if(mainPlayerRendered && this.poiBBCollide(ix, iy, poi, (int)playerX, (int)playerY, this.thePlayerPOI)) continue; 
			boolean h = this.isPointOverPOI(ix, iy, mouseX, mouseY, poi);
			poi.draw(ix, iy, h);
			this.lastEntityPoiRenderedCount ++;
			if(h && hoveredPOI == null) {
				hoverPOIX = ix;
				hoverPOIY = iy;
				hoveredPOI = poi;
			}
		}
		for(PlayerPOI poi: this.playerPOIs.values()) {
			long lx = this.getScreenX(poi.getLongitude());
			long ly = this.getScreenY(poi.getLatitude());
			if(!this.isPoiBBOnScreen(lx, ly, poi)) continue;
			int ix = (int)lx;
			int iy = (int)ly;
			if(mainPlayerRendered && this.poiBBCollide(ix, iy, poi, (int)playerX, (int)playerY, this.thePlayerPOI)) continue; 
			boolean h = this.isPointOverPOI(ix, iy, mouseX, mouseY, poi);
			poi.draw(ix, iy, h);
			poi.drawName(ix, iy, h);
		}
		if(hoveredPOI != null) hoveredPOI.drawName(hoverPOIX, hoverPOIY, true);
		if(mainPlayerRendered) {
			int px = (int)playerX;
			int py = (int)playerY;
			boolean h = this.isPointOverPOI(px, py, mouseX, mouseY, this.thePlayerPOI);
			this.thePlayerPOI.draw(px, py, h);
			this.thePlayerPOI.drawName(px, py, h);
			this.lastPlayerPoiRenderedCount++;
		}
	}

	private void updatePOIs() {
		Set<UUID> toUntrackEntities = new HashSet<UUID>();
		toUntrackEntities.addAll(this.entityPOIs.keySet()); //The key set is backed by the map, we can't mute it while iterating
		Set<UUID> toUntrackPlayers = new HashSet<UUID>();
		toUntrackPlayers.addAll(this.playerPOIs.keySet());
		Set<Entity> toTrackEntities = new HashSet<Entity>();
		Set<AbstractClientPlayer> toTrackPlayers = new HashSet<AbstractClientPlayer>();
		for(Entity entity: this.world.loadedEntityList) {
			if(!toUntrackEntities.remove(entity.getPersistentID())
					&& this.shouldTrackEntity(entity)
					&& !(entity instanceof EntityPlayer)) {
				toTrackEntities.add(entity);
			}
		}
		for(EntityPlayer player: this.world.playerEntities) {
			if(!toUntrackPlayers.remove(player.getPersistentID())
					&& !player.getPersistentID().equals(this.thePlayerPOI.getEntity().getPersistentID())){
				toTrackPlayers.add((AbstractClientPlayer)player);
			}
		}
		for(Entity entity: toTrackEntities) {
			this.entityPOIs.put(entity.getPersistentID(), new EntityPOI(entity));
		}
		for(AbstractClientPlayer player: toTrackPlayers) {
			this.playerPOIs.put(player.getPersistentID(), new PlayerPOI(player));
		}
		for(UUID uid: toUntrackEntities) this.entityPOIs.remove(uid);
		for(UUID uid: toUntrackPlayers) this.playerPOIs.remove(uid);
		for(EntityPOI poi: this.entityPOIs.values()) {
			poi.updatePosition(this.projection);
		}
		for(PlayerPOI poi: this.playerPOIs.values()) {
			poi.updatePosition(this.projection);
		}
		if(this.thePlayerPOI != null) this.thePlayerPOI.updatePosition(this.projection);

	}

	@Override
	public void updateScreen(){
		if(!this.isPositionValid(this.zoomLevel, this.focusLongitude, this.focusLatitude)) {
			TerramapMod.logger.error("Map is in an invalid state! Reseting!");
			this.setZoomToMinimum();
		}
		if(this.projection != null) this.updatePOIs();
	}		

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		if(keyCode == KeyBindings.TOGGLE_DEBUG.getKeyCode()) this.debug = !this.debug;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		switch(mouseButton) {
		case 0: //Left click
			if(this.rclickMenu.isDisplayed()) {
				this.rclickMenu.onMouseClick(mouseX, mouseY);
				this.rclickMenu.hide();
			} else {

			}
			break;
		case 1: //Right click
			int displayX = mouseX;
			int displayY = mouseY;
			int rClickWidth = this.rclickMenu.getWidth();
			int rClickHeight = this.rclickMenu.getHeight();
			displayX = mouseX + rClickWidth > this.width ? this.width - rClickWidth: mouseX;
			displayY = mouseY + rClickHeight > this.height ? this.height - rClickHeight: mouseY;
			this.rclickMenu.showAt(displayX, displayY); //TODO Added a poi where the map was clicked
			break;
		}
		this.lastMouseClickX = mouseX;
		this.lastMouseClickY = mouseY;
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick){
		if(this.lastMouseClickX != -1 && this.lastMouseClickY != -1 && clickedMouseButton == 0) {
			int dX = mouseX - this.lastMouseClickX;
			int dY = mouseY - this.lastMouseClickY;
			double nlon = this.focusLongitude - (double)dX * Math.PI/ (1<<(1 + this.zoomLevel)) / TerramapConfiguration.tileScaling;
			double nlat = this.focusLatitude + dY/Math.pow(2, this.zoomLevel)/2 / TerramapConfiguration.tileScaling;
			this.setLongitude(nlon);
			this.setLatitude(nlat);
		}
		this.lastMouseClickX = mouseX;
		this.lastMouseClickY = mouseY;
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int button) {
		super.mouseReleased(mouseX, mouseY, button);
	}

	/**
	 * Handles mouse input.
	 */
	@Override
	public void handleMouseInput() throws IOException {
		int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

		this.mouseLong = this.getScreenLong(mouseX);
		this.mouseLat = this.getScreenLat(mouseY);

		super.handleMouseInput();

		int scroll = Mouse.getDWheel();
		if(scroll != 0) this.mouseScrolled(mouseX, mouseY, scroll);

	}

	public void mouseScrolled(int mouseX, int mouseY, int amount) {
		this.rclickMenu.hide();
		int z;
		if (amount > 0) z = 1;
		else z = - 1;
		this.zoom(z);
	}

	@Override
	public void onGuiClosed() {
		this.map.unloadAll();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
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

	private boolean isPositionValid(int zoomLevel, double centerLong, double centerLat) {
		if(zoomLevel < 0) return false;
		if(zoomLevel > 19) return false;
		long upperY = this.getUpperLeftY(zoomLevel, centerLat);
		long lowerY = (long) (upperY + this.height);
		if(upperY < 0) return false;
		if(lowerY > this.getMaxMapSize(zoomLevel)) return false;
		return true;
	}

	private void setTiledMapZoom() {
		this.map.setZoomLevel((int)this.zoomLevel);
	}

	private boolean shouldTrackEntity(Entity entity) {
		if(entity instanceof EntityItem) return false;
		return TerramapConfiguration.showEntities && entity instanceof EntityLiving;
	}
	/**
	 * 
	 * @param zoom
	 * @return The size of the full map, in pixel on screen
	 */
	private long getMaxMapSize(int zoom) {
		return (long) (WebMercatorUtils.getDimensionsInTile(zoom) * WebMercatorUtils.TILE_DIMENSIONS * TerramapConfiguration.tileScaling);
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
		String cmd = GeoServices.formatStringWithCoords(TerramapConfiguration.tpllcmd, 0, longitude, latitude);
		this.sendChatMessage(cmd, false);
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

	protected void setZoom(int zoom) {
		this.zoomLevel = zoom;
		this.setTiledMapZoom();
	}

	protected long getMapX(double longitude) {
		return this.getMapX(this.zoomLevel, longitude);
	}

	protected long getMapY(double latitude) {
		return this.getMapY(this.zoomLevel, latitude);
	}

	protected long getMapX(int zoomLevel, double longitude) {
		return (long) (WebMercatorUtils.getXFromLongitude(longitude, zoomLevel) * TerramapConfiguration.tileScaling);
	}

	protected long getMapY(int zoomLevel, double latitude) {
		return (long)(WebMercatorUtils.getYFromLatitude(latitude, zoomLevel) * TerramapConfiguration.tileScaling);
	}

	protected long getScreenX(double longitude) {
		return this.getMapX(longitude) -  this.getUpperLeftX();
	}

	protected long getScreenY(double latitude) {
		return this.getMapY(latitude) - this.getUpperLeftY();
	}

	protected long getUpperLeftX(double centerLon) {
		return this.getUpperLeftX(this.zoomLevel, centerLon);
	}

	protected long getUpperLeftY(double centerLat) {
		return this.getUpperLeftY(this.zoomLevel, centerLat);
	}

	protected long getUpperLeftX(int zoomLevel, double centerLon) {
		return this.getMapX(centerLon) - this.width / 2;
	}

	protected long getUpperLeftY(int zoomLevel, double centerLat) {
		return this.getMapY(centerLat) - this.height / 2;
	}

	protected long getUpperLeftX() {
		return this.getUpperLeftX(this.focusLongitude);
	}

	protected long getUpperLeftY() {
		return this.getUpperLeftY(this.focusLatitude);
	}

	protected boolean isPoiBBOnScreen(long x, long y, PointOfInterest poi) {
		return x + poi.getXOffset() <= this.width
				&& x + poi.getXOffset() + poi.getWidth() >= 0
				&& y + poi.getYOffset() <= this.height
				&& y + poi.getYOffset() + poi.getHeight() >= 0;
	}

	protected boolean isPointOverPOI(int x, int y, int mouseX, int mouseY, PointOfInterest poi) {
		return x + poi.getXOffset() <= mouseX
				&& x + poi.getXOffset() + poi.getWidth() >= mouseX
				&& y + poi.getYOffset() <= mouseY
				&& y + poi.getYOffset() + poi.getHeight() >= mouseY;
	}

	protected boolean poiBBCollide(int x1, int y1, PointOfInterest poi1, int x2, int y2, PointOfInterest poi2) {
		return this.isPointOverPOI(x2, y2, x1 + poi1.getXOffset(), y1 + poi1.getYOffset(), poi2)
				|| this.isPointOverPOI(x2, y2, x1 + poi1.getXOffset() + poi1.getWidth(), y1 + poi1.getYOffset(), poi2)
				|| this.isPointOverPOI(x2, y2, x1 + poi1.getXOffset(), y1 + poi1.getYOffset() + poi1.getHeight(), poi2)
				|| this.isPointOverPOI(x2, y2, x1 + poi1.getXOffset() + poi1.getWidth(), y1 + poi1.getYOffset() + poi1.getHeight(), poi2);
	}
	
	
}
