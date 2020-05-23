package fr.smyler.terramap.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import fr.smyler.terramap.GeoServices;
import fr.smyler.terramap.TerramapMod;
import fr.smyler.terramap.TerramapUtils;
import fr.smyler.terramap.config.TerramapConfiguration;
import fr.smyler.terramap.config.TerramapServerPreferences;
import fr.smyler.terramap.gui.widgets.GuiTexturedButton;
import fr.smyler.terramap.gui.widgets.RightClickMenu;
import fr.smyler.terramap.gui.widgets.poi.EntityPOI;
import fr.smyler.terramap.gui.widgets.poi.LocationPOI;
import fr.smyler.terramap.gui.widgets.poi.PlayerPOI;
import fr.smyler.terramap.gui.widgets.poi.PointOfInterest;
import fr.smyler.terramap.gui.widgets.CopyrightNoticeWidget;
import fr.smyler.terramap.input.KeyBindings;
import fr.smyler.terramap.maps.TiledMap;
import fr.smyler.terramap.maps.tiles.RasterWebTile;
import fr.smyler.terramap.maps.tiles.RasterWebTile.InvalidTileCoordinatesException;
import fr.smyler.terramap.maps.utils.WebMercatorUtils;
import io.github.terra121.EarthGeneratorSettings;
import io.github.terra121.projection.GeographicProjection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class GuiTiledMap extends GuiScreen {

	public static final ResourceLocation WIDGET_TEXTURES = new ResourceLocation(TerramapMod.MODID, "textures/gui/mapwidgets.png");
	protected TiledMap<?> map;
	protected TiledMap<?>[] availableMaps;

	protected double focusLatitude = 0; //Center of the screen
	protected double focusLongitude = 0;
	protected int zoomLevel = 0;
	protected double mouseLong, mouseLat = 0;
	protected int lastMouseClickX, lastMouseClickY = -1;
	protected double mapVelocityX, mapVelocityY = 0;
	protected EarthGeneratorSettings genSettings = null;
	protected GeographicProjection projection;
	protected boolean manualProjection = false;

	protected boolean debug = false; //Show tiles borders or not
	protected PointOfInterest followedPOI = null;
	private long lastClickTime = 0;
	private boolean buttonWasClicked = false; // Used to know when handling mouse if super has triggered a button

	protected RightClickMenu rclickMenu;
	protected LocationPOI rightClickPOI = null;
	protected GuiButton zoomInButton;
	protected GuiButton zoomOutButton;
	protected GuiButton centerOnPlayerButton;
	protected GuiButton tilesetButton;
	protected CopyrightNoticeWidget copyright;
	protected RightClickMenu tilesetMenu;

	protected Map<UUID, EntityPOI> entityPOIs;
	protected Map<UUID, PlayerPOI> playerPOIs; //Tracked players, excluding ourself
	protected PlayerPOI thePlayerPOI; //Ourself
	protected int lastEntityPoiRenderedCount = 0; //Used for debug output
	protected int lastPlayerPoiRenderedCount = 0;
	protected World world; 
	
	public GuiTiledMap(TiledMap<?>[] maps, World world) {
		this.map = maps[0];
		this.availableMaps = maps;
		this.world = world;
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if(this.manualProjection == false) {
			this.genSettings = TerramapMod.proxy.getCurrentEarthGeneratorSettings(null); //We are on client, world is not needed
			if(this.genSettings == null) {
				String savedSettings = TerramapServerPreferences.getServerGenSettings(Minecraft.getMinecraft().getCurrentServerData().serverIP);
				if(savedSettings.length() != 0) {
					this.genSettings = new EarthGeneratorSettings(savedSettings);
					this.manualProjection = true;
					TerramapMod.logger.info("Used local server preference for the projection");
				}
			}
		}
		if(this.genSettings != null) {
			this.projection = this.genSettings.getProjection();
			double coords[] = this.projection.toGeo(player.posX, player.posZ);
			this.focusLatitude = coords[1];
			this.focusLongitude = coords[0];
		} else {
			TerramapMod.logger.info("Projection was not available");
			this.focusLatitude = 0;
			this.focusLongitude = 0;
			this.setZoomToMinimum();
			this.manualProjection = true;
		}
		this.entityPOIs = new HashMap<UUID, EntityPOI>();
		this.playerPOIs = new HashMap<UUID, PlayerPOI>();
		this.thePlayerPOI = new PlayerPOI((AbstractClientPlayer)player);
		this.setZoom(17);
	}

	@Override
	public void initGui() {
		int buttonId = 0;
		this.rclickMenu = new RightClickMenu();
		this.rclickMenu.init(fontRenderer);
		this.rclickMenu.addEntry(I18n.format("terramap.mapgui.rclickmenu.teleport"), () -> {this.teleportPlayerTo(this.mouseLong, this.mouseLat);});
		this.rclickMenu.addEntry(I18n.format("terramap.mapgui.rclickmenu.center"), () -> {this.setPosition(this.mouseLong, this.mouseLat);});
		this.rclickMenu.addEntry(I18n.format("terramap.mapgui.rclickmenu.copy_geo"), () -> {GuiScreen.setClipboardString("" + this.mouseLong + " " + this.mouseLat);});
		if(this.projection != null) {
			this.rclickMenu.addEntry(I18n.format("terramap.mapgui.rclickmenu.copy_mc"), ()->{
				double[] coords = this.projection.fromGeo(this.mouseLong, this.mouseLat);
				String dispX = "" + Math.round(coords[0]);
				String dispY = "" + Math.round(coords[1]);
				GuiScreen.setClipboardString(dispX + " " + dispY);
			});
		}
		this.rclickMenu.addEntry(I18n.format("terramap.mapgui.rclickmenu.open_osm"), () -> {GeoServices.openInOSMWeb(this.zoomLevel, this.mouseLong, this.mouseLat);});
		this.rclickMenu.addEntry(I18n.format("terramap.mapgui.rclickmenu.open_gmaps"), () -> {GeoServices.openInGoogleMaps(this.zoomLevel, this.mouseLong, this.mouseLat);});
		this.rclickMenu.addEntry(I18n.format("terramap.mapgui.rclickmenu.open_gearth_web"), () -> {GeoServices.opentInGoogleEarthWeb(this.mouseLong, this.mouseLat);});
		//TODO Open in google Earth pro
		if(this.manualProjection) {
			this.rclickMenu.addEntry(I18n.format("terramap.mapgui.rclickmenu.set_proj"), ()-> {
				Minecraft.getMinecraft().displayGuiScreen(new EarthMapConfigGui(this, Minecraft.getMinecraft()));	
			});
		}
		this.tilesetMenu = new RightClickMenu();
		this.tilesetMenu.init(this.fontRenderer);
		for(TiledMap<?> map: this.availableMaps) {
			this.tilesetMenu.addEntry(map.getName(), () -> {this.setMap(map);});
		}
		this.closeRightClickMenu();
		this.zoomInButton = new GuiTexturedButton(buttonId++, this.width - 30, 15, 15, 15, 40, 0, 40, 15, 40, 30, GuiTiledMap.WIDGET_TEXTURES);
		this.zoomOutButton = new GuiTexturedButton(buttonId++, this.width - 30, 40 + this.fontRenderer.FONT_HEIGHT, 15, 15, 55, 0, 55, 15, 55, 30, GuiTiledMap.WIDGET_TEXTURES);
		this.centerOnPlayerButton = new GuiTexturedButton(buttonId++, this.width - 30,  65 + this.fontRenderer.FONT_HEIGHT, 15, 15, 70, 0, 70, 15, 70, 30, GuiTiledMap.WIDGET_TEXTURES);
		this.copyright = new CopyrightNoticeWidget(buttonId++, 0, 0, this.map);
		this.tilesetButton = new GuiTexturedButton(buttonId++, this.width - 30,  90 + this.fontRenderer.FONT_HEIGHT, 15, 15, 85, 0, 85, 15, 85, 30, GuiTiledMap.WIDGET_TEXTURES);
		this.addButton(this.zoomInButton);
		this.addButton(this.zoomOutButton);
		this.addButton(this.centerOnPlayerButton);
		this.addButton(this.copyright);
		if(this.availableMaps.length > 1) this.addButton(this.tilesetButton);
		this.updateMouseGeoPos(this.width/2, this.height/2);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawBackground(0);
		this.drawMap(mouseX, mouseY, partialTicks);
		if(this.projection != null) this.drawPOIs(mouseX, mouseY, partialTicks);
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.drawInformation(mouseX, mouseY, partialTicks);
		if(this.projection == null && !TerramapConfiguration.ignoreProjectionWarning) this.drawProjectionWarning(mouseX, mouseY, partialTicks);
		this.rclickMenu.draw(mouseX, mouseY, partialTicks);
		this.tilesetMenu.draw(mouseX, mouseY, partialTicks);
	}

	private void drawMap(int mouseX, int mouseY, float partialTicks) {

		int renderSize = (int) (WebMercatorUtils.TILE_DIMENSIONS * TerramapConfiguration.tileScaling);

		long upperLeftX = (long) this.getUpperLeftX(this.focusLongitude);
		long upperLeftY = (long) this.getUpperLeftY(this.focusLatitude);

		Minecraft mc = Minecraft.getMinecraft();
		TextureManager textureManager = mc.getTextureManager();

		int maxTileXY = (int) map.getSizeInTiles(this.zoomLevel);
		long maxX = (long) (upperLeftX + this.width);
		long maxY = (long) (upperLeftY + this.height);

		int lowerTX = (int) Math.floor((double)upperLeftX / (double)renderSize);
		int lowerTY = (int) Math.floor((double)upperLeftY / (double)renderSize);

		for(int tX = lowerTX; tX * renderSize < maxX; tX++) {

			for(int tY = lowerTY; tY * renderSize < maxY; tY++) {

				RasterWebTile tile;

				try {
					tile = map.getTile(this.zoomLevel, TerramapUtils.modulus(tX, maxTileXY), tY);
				} catch(InvalidTileCoordinatesException e) { continue ;}
				//This is the tile we would like to render, but it is not possible if it hasn't been cached yet
				RasterWebTile bestTile = tile;
				boolean lowerResRender = false;

				if(!TerramapMod.cacheManager.isCached(tile)) {
					lowerResRender = true;
					if(!TerramapMod.cacheManager.isBeingCached(tile))
						TerramapMod.cacheManager.cacheAsync(tile);
					while(tile.getZoom() > 0 && !TerramapMod.cacheManager.isCached(tile)) {
						tile = this.map.getTile(tile.getZoom()-1, tile.getX() /2, tile.getY() /2);
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

				//The following code is needed for a full screen map,
				//but will be needed if we ever decide to make a map widget
				//to embed in other guis
//				if(tX == lowerTX) {
//					dX -= dispX;
//					dispX = 0;
//				}
//
//				if(tY == lowerTY) {
//					dY -= dispY;
//					dispY = 0;
//				}

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
		String dispLat = GeoServices.formatGeoCoordForDisplay(this.mouseLat);
		String dispLong = GeoServices.formatGeoCoordForDisplay(this.mouseLong);
		lines.add(I18n.format("terramap.mapgui.information.mouse_geo", dispLat, dispLong));
		if(this.projection != null) {
			double[] coords = this.projection.fromGeo(this.mouseLong, this.mouseLat);
			String dispX = "" + Math.round(coords[0]);
			String dispZ = "" + Math.round(coords[1]);
			lines.add(I18n.format("terramap.mapgui.information.mouse_mc", dispX, dispZ));
		}
		if(this.followedPOI != null) {
			lines.add(I18n.format("terramap.mapgui.information.followed", this.followedPOI.getDisplayName()));
		}
		if((this.debug || this.manualProjection) && this.genSettings != null) {
			lines.add(I18n.format("terramap.mapgui.information.projection", this.genSettings.settings.projection));
			lines.add(I18n.format("terramap.mapgui.information.orientation", this.genSettings.settings.orentation));
		}
		if(this.debug) {
			String mapLa = GeoServices.formatGeoCoordForDisplay(this.focusLatitude);
			String mapLo = GeoServices.formatGeoCoordForDisplay(this.focusLongitude);
			lines.add("Map location: " + mapLo + " " + mapLa); //Not translated, that's debug
			lines.add("Cache queue: " + TerramapMod.cacheManager.getQueueSize());
			lines.add("Loaded tiles: " + this.map.getLoadedCount() + "/" + this.map.getMaxLoad());
			int playerPOICount = this.playerPOIs.size();
			if(this.thePlayerPOI != null) playerPOICount++;
			lines.add("FPS:" + Minecraft.getDebugFPS() +
					" ePOIs: " + this.lastEntityPoiRenderedCount +"/" + this.entityPOIs.size() +
					" pPOIs: " + this.lastPlayerPoiRenderedCount +"/" + playerPOICount);
		}
		Gui.drawRect(0, 0, 220, lines.size() * (this.fontRenderer.FONT_HEIGHT + 10) + 10 , 0x80000000);
		int i = 0;
		for(String line: lines) this.drawString(this.fontRenderer, line, 10, 10*i++ + this.fontRenderer.FONT_HEIGHT * i, 0xFFFFFF);
		Gui.drawRect(this.width - 30 , 30, this.width - 15, 30 + this.fontRenderer.FONT_HEIGHT + 10 , 0x80000000);
		this.drawCenteredString(this.fontRenderer, "" + this.zoomLevel, this.width - 22, 36, 0xFFFFFF);
	}
	
	private void drawProjectionWarning(int mouseX, int mouseY, float partialTicks) {
		List<String> warning = new ArrayList<String>();
		int i = 1;
		while(true) {
			String key = "terramap.mapgui.projection_warning.line" + i;
			if(I18n.hasKey(key)) warning.add(I18n.format(key));
			else break;
		}
		int width = 0;
		for(String line: warning) width = Math.max(width, this.fontRenderer.getStringWidth(line));
		int height = 20 + warning.size() * (5 + this.fontRenderer.FONT_HEIGHT);
		width += 20;
		int centerX = width/2;
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GuiScreen.drawRect(0, this.height - height, width, this.height, 0xAA000000);
		int y = this.height - height + 10;
		for(String line: warning) {
			this.drawCenteredString(this.fontRenderer, line, centerX, y, 0xFFCC00);
			y += this.fontRenderer.FONT_HEIGHT + 5;
		}
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
			playerX = (long) this.getScreenX(this.thePlayerPOI.getLongitude());
			playerY = (long) this.getScreenY(this.thePlayerPOI.getLatitude());
			mainPlayerRendered = this.isPoiBBOnScreen(playerX, playerY, this.thePlayerPOI);
		}
		for(EntityPOI poi: this.entityPOIs.values()) {
			long lx = (long) this.getScreenX(poi.getLongitude());
			long ly = (long) this.getScreenY(poi.getLatitude());
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
			long lx = (long) this.getScreenX(poi.getLongitude());
			long ly = (long) this.getScreenY(poi.getLatitude());
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
		if(this.rightClickPOI != null) {
			int x = (int) this.getScreenX(this.rightClickPOI.getLongitude());
			int y = (int) this.getScreenY(this.rightClickPOI.getLatitude());
			boolean h = this.isPointOverPOI(x, y, mouseX, mouseY, this.rightClickPOI);
			this.rightClickPOI.draw(x, y, h);
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

		this.focusLongitude = WebMercatorUtils.getLongitudeInRange(this.focusLongitude);
		if(!this.isPositionValid(this.zoomLevel, this.focusLongitude, this.focusLatitude)) {
			TerramapMod.logger.error("Map is in an invalid state! Reseting!");
			this.setZoomToMinimum();
		}
		if(this.projection != null) this.updatePOIs();
		if(this.followedPOI != null) {
			this.setPosition(this.followedPOI.getLongitude(), this.followedPOI.getLatitude());
		} else {
			// Moves the map from its inertia if we are not moving it manually
			// It would have been nice if this could have changed according to the time,
			// TODO But last attempt was not a success and it's not a priority
			if(!Mouse.isButtonDown(0) && (this.mapVelocityX != 0 || this.mapVelocityY != 0)){
				this.moveMap((int)(this.mapVelocityX), (int)(this.mapVelocityY));
				float f = .1f;
				this.mapVelocityX -= f * this.mapVelocityX;
				this.mapVelocityY -= f * this.mapVelocityY;
				if(this.mapVelocityX < 0.1) this.mapVelocityX = 0;
				if(this.mapVelocityY < 0.1) this.mapVelocityY = 0;
			}
		}
		this.zoomInButton.enabled = this.zoomLevel < this.map.getMaxZoom();
		this.zoomOutButton.enabled = this.zoomLevel > this.map.getMinZoom();
		this.centerOnPlayerButton.enabled = this.followedPOI == null;
		this.copyright.x = this.width - this.copyright.getWidth();
		this.copyright.y = this.height - this.copyright.getHeight();
	}		

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		if(keyCode == KeyBindings.TOGGLE_DEBUG.getKeyCode()) this.debug = !this.debug;
		if(keyCode == Minecraft.getMinecraft().gameSettings.keyBindForward.getKeyCode() || keyCode == Keyboard.KEY_UP) this.moveMap(0, 10);
		if(keyCode == Minecraft.getMinecraft().gameSettings.keyBindBack.getKeyCode() || keyCode == Keyboard.KEY_DOWN) this.moveMap(0, -10);
		if(keyCode == Minecraft.getMinecraft().gameSettings.keyBindRight.getKeyCode() || keyCode == Keyboard.KEY_RIGHT) this.moveMap(-10, 0);
		if(keyCode == Minecraft.getMinecraft().gameSettings.keyBindLeft.getKeyCode() || keyCode == Keyboard.KEY_LEFT) this.moveMap(10, 0);
		if(keyCode == KeyBindings.OPEN_MAP.getKeyCode()) Minecraft.getMinecraft().displayGuiScreen(null);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
//		this.updateMouseGeoPos(mouseX, mouseY);
		if(!this.buttonWasClicked) this.followedPOI = null;
		switch(mouseButton) {
		case 0: //Left click
			if(this.rclickMenu.isDisplayed()) {
				this.rclickMenu.onMouseClick(mouseX, mouseY);
				this.closeRightClickMenu();
			} else if(this.tilesetMenu.isDisplayed()) {
				this.tilesetMenu.onMouseClick(mouseX, mouseY);
			} else {
				this.closeRightClickMenu();
			}
			long ctime = System.currentTimeMillis();
			if(ctime - this.lastClickTime < TerramapConfiguration.doubleClickDelay && mouseButton == 0) this.mouseDoubleClick(mouseX, mouseY);
			this.lastClickTime = ctime;
			break;
		case 1: //Right click
			this.closeRightClickMenu();
			this.updateMouseGeoPos(mouseX, mouseY);
			this.mapVelocityX = 0;
			this.mapVelocityY = 0;
			int displayX = mouseX;
			int displayY = mouseY;
			int rClickWidth = this.rclickMenu.getWidth();
			int rClickHeight = this.rclickMenu.getHeight();
			displayX = mouseX + rClickWidth > this.width ? mouseX - rClickWidth: mouseX;
			displayY = mouseY + rClickHeight > this.height ? mouseY - rClickHeight: mouseY;
			this.rightClickPOI = new LocationPOI(this.mouseLong, this.mouseLat, "Right Click");
			this.rclickMenu.showAt(displayX, displayY);
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
			this.mapVelocityX = (double)dX;
			this.mapVelocityY = (double)dY;
			this.moveMap(dX, dY);
		}
		this.lastMouseClickX = mouseX;
		this.lastMouseClickY = mouseY;
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int button) {
		super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public void handleMouseInput() throws IOException {
		this.buttonWasClicked = false;
		int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

		if(!this.rclickMenu.isDisplayed()) this.updateMouseGeoPos(mouseX, mouseY);

		super.handleMouseInput();

		int scroll = Mouse.getDWheel();
		if(scroll != 0) this.mouseScrolled(mouseX, mouseY, scroll);
		this.buttonWasClicked = false;
	}
	
	public void updateMouseGeoPos(int mouseX, int mouseY) {
		this.mouseLong = this.getScreenLong((double)mouseX);
		this.mouseLat = this.getScreenLat((double)mouseY);
	}

	public void mouseScrolled(int mouseX, int mouseY, int amount) {
		this.closeRightClickMenu();
		int z;
		if (amount > 0) z = 1;
		else z = - 1;
		this.zoom(mouseX, mouseY, z);
	}

	public void mouseDoubleClick(int mouseX, int mouseY) {
		if(this.buttonWasClicked) return;
		this.closeRightClickMenu();
		if(this.thePlayerPOI != null) {
			int px = (int) this.getScreenX(this.thePlayerPOI.getLongitude());
			int py = (int) this.getScreenY(this.thePlayerPOI.getLatitude());
			if(this.isPointOverPOI(px, py, mouseX, mouseY, this.thePlayerPOI)) this.followedPOI = this.thePlayerPOI;
		}
		for(PlayerPOI poi: this.playerPOIs.values()) {
			if(this.followedPOI != null) break;
			int px = (int) this.getScreenX(poi.getLongitude());
			int py = (int) this.getScreenY(poi.getLatitude());
			if(this.isPointOverPOI(px, py, mouseX, mouseY, poi)) this.followedPOI = poi;
		}
		for(EntityPOI poi: this.entityPOIs.values()) {
			if(this.followedPOI != null) break;
			int px = (int) this.getScreenX(poi.getLongitude());
			int py = (int) this.getScreenY(poi.getLatitude());
			if(this.isPointOverPOI(px, py, mouseX, mouseY, poi)) this.followedPOI = poi;
		}
		if(this.followedPOI == null) this.zoom(mouseX, mouseY, 1);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		this.buttonWasClicked = true;
		if(button.id == this.zoomInButton.id) {
			this.zoom(1);
		} else if(button.id == this.zoomOutButton.id) {
			this.zoom(-1);
		} else if(button.id == this.centerOnPlayerButton.id) {
			this.setPosition(this.thePlayerPOI.getLongitude(), this.thePlayerPOI.getLatitude());
		} else if(button.id == this.copyright.id && this.map.getCopyRightURL().length() > 0) {
			GeoServices.openURI(this.map.getCopyRightURL());
		} else if(button.id == this.tilesetButton.id) {
			this.mapVelocityX = 0;
			this.mapVelocityY = 0;
			if(this.tilesetMenu.isDisplayed()) this.tilesetMenu.hide();
			else {
				this.tilesetMenu.showAt(this.tilesetButton.x - this.tilesetMenu.getWidth(), this.tilesetButton.y);
			}
		}
	}
	
	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		this.map.unloadAll();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	private void setMap(TiledMap<?> map) {
		TerramapMod.logger.debug("Changing map");
		TerramapMod.cacheManager.clearQueue();
		this.map.unloadAll();
		if(this.map.getMaxZoom() > map.getMaxZoom()) this.zoomLevel = map.getMaxZoom();
		if(this.map.getMinZoom() < map.getMinZoom()) this.zoomLevel = map.getMinZoom();
		this.map = map;
		this.copyright.map = this.map;
	}
	
	public void zoom(int val) {
		this.zoom(this.width/2, this.height/2, val);
	}
	
	public void zoom(int mouseX, int mouseY, int zoom) {

		int nzoom = this.zoomLevel + zoom;
		if(!this.isZoomValid(nzoom)) return;
		
		this.zoomLevel = nzoom;
		double factor = Math.pow(2, zoom);
		double ndX = ((double)this.width/2 - mouseX) * factor;
		double ndY = ((double)this.height/2 - mouseY) * factor;
		if(factor > 1) {
			ndX = -ndX / 2;
			ndY = -ndY / 2;
		}
		this.setLongitude(this.getScreenLong((double)this.width/2 + ndX));
		this.setLatitude(this.getScreenLat((double)this.height/2 + ndY));
		this.mapVelocityX *= factor;
		this.mapVelocityY *= factor;
		this.updateMouseGeoPos(mouseX, mouseY);

		TerramapMod.cacheManager.clearQueue(); // We are displaying new tiles, we don't need what we needed earlier

	}

	public void setZoomToMinimum() {
		this.setZoom(0);
	}

	public void moveMap(int dX, int dY) {
		this.closeRightClickMenu();
		this.followedPOI = null;
		double nlon = this.getScreenLong((double)this.width/2 - dX);
		double nlat = this.getScreenLat((double)this.height/2 - dY);
		this.setLongitude(nlon);
		this.setLatitude(nlat);
	}
	
	private boolean isPositionValid(int zoomLevel, double centerLong, double centerLat) {
		return this.isZoomValid(zoomLevel);
	}
	
	private boolean isZoomValid(int zoom) {
		return zoom >= this.map.getMinZoom() && zoom <= this.map.getMaxZoom();
	}

	private boolean shouldTrackEntity(Entity entity) {
		if(entity instanceof EntityItem) return false;
		return TerramapConfiguration.showEntities && entity instanceof EntityLiving;
	}
	
	private double getScreenLong(double xOnScreen) {
		double xOnMap = (this.getUpperLeftX(this.zoomLevel, this.focusLongitude) + xOnScreen) / TerramapConfiguration.tileScaling;
		return WebMercatorUtils.getLongitudeInRange(WebMercatorUtils.getLongitudeFromX(xOnMap, this.zoomLevel));
	}
	
	private double getScreenLat(double yOnScreen) {
		double yOnMap = (this.getUpperLeftY(this.zoomLevel, this.focusLatitude) + yOnScreen) / TerramapConfiguration.tileScaling;
		return WebMercatorUtils.getLatitudeFromY(yOnMap, this.zoomLevel);
	}

	private void teleportPlayerTo(double longitude, double latitude) {
		String cmd = TerramapConfiguration.tpllcmd.replace("{longitude}", ""+longitude).replace("{latitude}", ""+latitude);
		if(this.projection != null) {
			double[] xz = projection.fromGeo(longitude, latitude);
			cmd = cmd.replace("{x}", "" + xz[0]).replace("{z}", "" + xz[1]);
		}
		this.sendChatMessage(cmd, false);
	}
	
	protected void closeRightClickMenu() {
		this.rclickMenu.hide();
		this.rightClickPOI = null;
		this.tilesetMenu.hide();
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
	}
	
	protected double getMapX(double longitude) {
		return this.getMapX(this.zoomLevel, longitude);
	}

	protected double getMapY(double latitude) {
		return this.getMapY(this.zoomLevel, latitude);
	}

	protected double getMapX(int zoomLevel, double longitude) {
		return WebMercatorUtils.getXFromLongitude(longitude, zoomLevel) * TerramapConfiguration.tileScaling;
	}

	protected double getMapY(int zoomLevel, double latitude) {
		return WebMercatorUtils.getYFromLatitude(latitude, zoomLevel) * TerramapConfiguration.tileScaling;
	}

	protected double getScreenX(double longitude) {
		return this.getMapX(longitude) -  this.getUpperLeftX();
	}

	protected double getScreenY(double latitude) {
		return this.getMapY(latitude) - this.getUpperLeftY();
	}

	protected double getUpperLeftX(double centerLon) {
		return this.getUpperLeftX(this.zoomLevel, centerLon);
	}

	protected double getUpperLeftY(double centerLat) {
		return this.getUpperLeftY(this.zoomLevel, centerLat);
	}

	protected double getUpperLeftX(int zoomLevel, double centerLon) {
		return this.getMapX(centerLon) - (double)this.width / 2;
	}

	protected double getUpperLeftY(int zoomLevel, double centerLat) {
		return this.getMapY(centerLat) - (double)this.height / 2;
	}

	protected double getUpperLeftX() {
		return this.getUpperLeftX(this.focusLongitude);
	}

	protected double getUpperLeftY() {
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

	public EarthGeneratorSettings getGenerationSettings() {
		return genSettings;
	}

	public void setGenerationSettings(EarthGeneratorSettings genSettings) {
		this.genSettings = genSettings;
	}
	
	
	
}

//Thrown when trying to call a method which needs the projection but it is null
class NoProjection extends Exception {
	private static final long serialVersionUID = 5954255037351370636L;
}
