package fr.thesmyler.terramap.gui.widgets.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.smylibgui.widgets.MenuWidget;
import fr.thesmyler.smylibgui.widgets.MenuWidget.MenuEntry;
import fr.thesmyler.smylibgui.widgets.text.FontRendererContainer;
import fr.thesmyler.smylibgui.widgets.text.TextAlignment;
import fr.thesmyler.smylibgui.widgets.text.TextComponentWidget;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
import fr.thesmyler.terramap.GeoServices;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.gui.screens.config.TerramapEarthGui;
import fr.thesmyler.terramap.gui.widgets.markers.MarkerControllerManager;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.FeatureVisibilityController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MainPlayerMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.OtherPlayerMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.PlayerDirectionsVisibilityController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.PlayerNameVisibilityController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.RightClickMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.markers.Marker;
import fr.thesmyler.terramap.gui.widgets.markers.markers.entities.MainPlayerMarker;
import fr.thesmyler.terramap.input.KeyBindings;
import fr.thesmyler.terramap.maps.IRasterTiledMap;
import fr.thesmyler.terramap.maps.utils.WebMercatorUtils;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;

public class MapWidget extends Screen {

	private boolean interactive = true;
	private boolean focusedZoom = true; // Zoom where the cursor is (true) or at the center of the map (false) when using the wheel
	private boolean enableRightClickMenu = true;
	private boolean showCopyright = true;
	private boolean debugMode = false;
	private boolean visible = true;

	private ControllerMapLayer controller;
	protected RasterMapLayerWidget background;
	private final Map<String, MarkerController<?>> markerControllers = new LinkedHashMap<String, MarkerController<?>>();
	private RightClickMarkerController rcmMarkerController;
	private MainPlayerMarkerController mainPlayerMarkerController;
	private OtherPlayerMarkerController otherPlayerMarkerController;
	private MainPlayerMarker mainPlayerMarker;
	private Marker trackingMarker;
	private String restoreTrackingId;
	private PlayerDirectionsVisibilityController directionVisibility;
	private PlayerNameVisibilityController nameVisibility;

	private double mouseLongitude, mouseLatitude;

	private MenuWidget rightClickMenu;
	private MenuEntry teleportMenuEntry;
	private MenuEntry copyBlockMenuEntry;
	private MenuEntry copyChunkMenuEntry;
	private MenuEntry copyRegionMenuEntry;
	private MenuEntry copy3drMenuEntry;
	private MenuEntry copy2drMenuEntry;
	private MenuEntry setProjectionMenuEntry;
	
	private TextComponentWidget copyright;
	private ScaleIndicatorWidget scale = new ScaleIndicatorWidget(-1);

	protected double tileScaling;

	private TextWidget errorText;

	private List<ReportedError> reportedErrors = new ArrayList<>();
	private static final int MAX_ERRORS_KEPT = 10;
	
	private final MapContext context;

	public static final int BACKGROUND_Z = Integer.MIN_VALUE;
	public static final int CONTROLLER_Z = 0;

	public MapWidget(int x, int y, int z, int width, int height, IRasterTiledMap map, MapContext context, double tileScaling) {
		super(x, y, z, width, height, BackgroundType.NONE);
		this.context = context;
		this.tileScaling = tileScaling;
		FontRendererContainer font = new FontRendererContainer(Minecraft.getMinecraft().fontRenderer);
		this.copyright = new TextComponentWidget(Integer.MAX_VALUE, new TextComponentString(""), font) {
			@Override
			public boolean isVisible(Screen parent) {
				return MapWidget.this.showCopyright;
			}
		};
		this.copyright.setBackgroundColor(0x80000000).setPadding(3).setAlignment(TextAlignment.LEFT).setShadow(false);
		super.addWidget(this.copyright);

		this.errorText = new TextWidget(Integer.MAX_VALUE, font) {
			@Override
			public boolean isVisible(Screen parent) {
				return MapWidget.this.reportedErrors.size() > 0 && MapWidget.this.context == MapContext.FULLSCREEN;
			}
		};
		this.errorText.setBackgroundColor(0xC0600000).setPadding(5).setAlignment(TextAlignment.CENTER).setShadow(false).setBaseColor(0xFFFFFFFF);
		super.addWidget(errorText);
		
		this.rightClickMenu = new MenuWidget(1500, font);
		this.teleportMenuEntry = this.rightClickMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.teleport"), () -> {
			this.teleportPlayerTo(this.mouseLongitude, this.mouseLatitude);
		});
		this.rightClickMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.center"), () -> {
			this.setCenterPosition(this.mouseLongitude, this.mouseLatitude);
		});
		MenuWidget copySubMenu = new MenuWidget(this.rightClickMenu.getZ(), font);
		copySubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.copy.geo"), () -> {
			GuiScreen.setClipboardString("" + this.mouseLatitude + " " + this.mouseLongitude);
		});
		this.copyBlockMenuEntry = copySubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.copy.block"), ()->{
			try {
				String strToCopy = "Outside projection";
				double[] coords = TerramapClientContext.getContext().getProjection().fromGeo(this.mouseLongitude, this.mouseLatitude);
				String dispX = "" + Math.round(coords[0]);
				String dispY = "" + Math.round(coords[1]);
				strToCopy = dispX + " " + dispY;
				GuiScreen.setClipboardString(strToCopy);
			} catch(OutOfProjectionBoundsException e) {
				String s = System.currentTimeMillis() + ""; //Just a random string
				this.reportError(s, I18n.format("terramap.mapwidget.error.copyblock"));
				this.scheduleWithDelay(() -> this.discardPreviousErrors(s), 5000);
			}
		});	
		this.copyChunkMenuEntry = copySubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.copy.chunk"), ()->{
			try {
				String strToCopy = "Outside projection";
				double[] coords = TerramapClientContext.getContext().getProjection().fromGeo(this.mouseLongitude, this.mouseLatitude);
				String dispX = "" + Math.floorDiv(Math.round(coords[0]), 16);
				String dispY = "" + Math.floorDiv(Math.round(coords[1]), 16);
				strToCopy = dispX + " " + dispY;
				GuiScreen.setClipboardString(strToCopy);
			} catch(OutOfProjectionBoundsException e) {
				String s = System.currentTimeMillis() + ""; //Just a random string
				this.reportError(s, I18n.format("terramap.mapwidget.error.copychunk"));
				this.scheduleWithDelay(() -> this.discardPreviousErrors(s), 5000);
			}
		});
		this.copyRegionMenuEntry = copySubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.copy.region"), ()->{
			try {
				String strToCopy = "Outside projection";
				double[] coords = TerramapClientContext.getContext().getProjection().fromGeo(this.mouseLongitude, this.mouseLatitude);
				String dispX = "" + Math.floorDiv(Math.round(coords[0]), 512);
				String dispY = "" + Math.floorDiv(Math.round(coords[1]), 512);
				strToCopy = "r." + dispX + "." + dispY + ".mca";
				GuiScreen.setClipboardString(strToCopy);
			} catch(OutOfProjectionBoundsException e) {
				String s = System.currentTimeMillis() + ""; //Just a random string
				this.reportError(s, I18n.format("terramap.mapwidget.error.copyregion"));
				this.scheduleWithDelay(() -> this.discardPreviousErrors(s), 5000);
			}
		});
		this.copy3drMenuEntry = copySubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.copy.3dr"), ()->{
			try {
				String strToCopy = "Outside projection";
				double[] coords = TerramapClientContext.getContext().getProjection().fromGeo(this.mouseLongitude, this.mouseLatitude);
				String dispX = "" + Math.floorDiv(Math.round(coords[0]), 256);
				String dispY = "" + Math.floorDiv(Math.round(coords[1]), 256);
				strToCopy = dispX + ".0." + dispY + ".3dr";
				GuiScreen.setClipboardString(strToCopy);
			} catch(OutOfProjectionBoundsException e) {
				String s = System.currentTimeMillis() + ""; //Just a random string
				this.reportError(s, I18n.format("terramap.mapwidget.error.copy2dregion"));
				this.scheduleWithDelay(() -> this.discardPreviousErrors(s), 5000);
			}
		});
		this.copy2drMenuEntry = copySubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.copy.2dr"), ()->{
			try {
				String strToCopy = "Outside projection";
				double[] coords = TerramapClientContext.getContext().getProjection().fromGeo(this.mouseLongitude, this.mouseLatitude);
				String dispX = "" + Math.floorDiv(Math.round(coords[0]), 512);
				String dispY = "" + Math.floorDiv(Math.round(coords[1]), 512);
				strToCopy = dispX + "." + dispY + ".2dr";
				GuiScreen.setClipboardString(strToCopy);
			} catch(OutOfProjectionBoundsException e) {
				String s = System.currentTimeMillis() + ""; //Just a random string
				this.reportError(s, I18n.format("terramap.mapwidget.error.copy2dregion"));
				this.scheduleWithDelay(() -> this.discardPreviousErrors(s), 5000);
			}
		});
		this.rightClickMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.copy"), copySubMenu);
		this.rightClickMenu.addSeparator();
		MenuWidget openSubMenu = new MenuWidget(this.rightClickMenu.getZ(), font);
		openSubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open_osm"), () -> {
			GeoServices.openInOSMWeb(Math.round((float)this.getZoom()), this.getMouseLongitude(), this.getMouseLatitude(), this.getMouseLongitude(), this.getMouseLatitude());
		});
		openSubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open_bte"), () -> {
			GeoServices.openInBTEMap(Math.round((float)this.getZoom()), this.getMouseLongitude(), this.getMouseLatitude(), this.getMouseLongitude(), this.getMouseLatitude());
		});
		openSubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open_gmaps"), () -> {
			if(this.getMainPlayerMarker() != null) {
				double markerLon = this.getMainPlayerMarker().getLongitude();
				double markerLat = this.getMainPlayerMarker().getLatitude();
				if(Double.isFinite(markerLon) && Double.isFinite(markerLat)) {
					GeoServices.openPlaceInGoogleMaps(Math.round((float)this.getZoom()), this.getMouseLongitude(), this.getMouseLatitude(), markerLon, markerLat);
				} else {
					GeoServices.openInGoogleMaps(Math.round((float)this.getZoom()), this.getMouseLongitude(), this.getMouseLatitude());
				}
			} else {
				GeoServices.openInGoogleMaps(Math.round((float)this.getZoom()), this.getMouseLongitude(), this.getMouseLatitude());
			}

		});
		openSubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open_gearth_web"), () -> {
			GeoServices.opentInGoogleEarthWeb(this.getMouseLongitude(), this.getMouseLatitude(), this.getMouseLongitude(), this.getMouseLatitude());
		});
		openSubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open_gearth_pro"), () -> {
			GeoServices.openInGoogleEarthPro(this.getMouseLongitude(), this.getMouseLatitude());
		});
		openSubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open_bing"), () -> {
			GeoServices.openInBingMaps((int) this.getZoom(), this.getMouseLongitude(), this.getMouseLatitude(), this.getMouseLongitude(), this.getMouseLatitude());
		});
		openSubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open_wikimapia"), () -> {
			GeoServices.openInWikimapia((int) this.getZoom(), this.getMouseLongitude(), this.getMouseLatitude(), this.getMouseLongitude(), this.getMouseLatitude());
		});
		openSubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open_yandex"), () -> {
			GeoServices.openInYandex((int) this.getZoom(), this.getMouseLongitude(), this.getMouseLatitude(), this.getMouseLongitude(), this.getMouseLatitude());
		});
		this.rightClickMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open"), openSubMenu);
		this.rightClickMenu.addSeparator();
		this.setProjectionMenuEntry = this.rightClickMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.set_proj"), ()-> {
			Minecraft.getMinecraft().displayGuiScreen(new TerramapEarthGui(null, TerramapClientContext.getContext().getGeneratorSettings()));	
		});

		this.controller = new ControllerMapLayer(this.tileScaling);
		super.addWidget(this.controller);

		this.setMapBackgroud(new RasterMapLayerWidget(map, this.tileScaling));

		this.scale.setX(15).setY(this.height - 30);
		this.addWidget(scale);
		this.updateRightClickMenuEntries();
		this.updateMouseGeoPos(this.width/2, this.height/2);

		for(MarkerController<?> controller: MarkerControllerManager.createControllers(this.context)) {
			if(controller instanceof RightClickMarkerController) {
				this.rcmMarkerController = (RightClickMarkerController) controller;
			} else if(controller instanceof MainPlayerMarkerController) {
				this.mainPlayerMarkerController = (MainPlayerMarkerController) controller;
			} else if(controller instanceof OtherPlayerMarkerController) {
				this.otherPlayerMarkerController = (OtherPlayerMarkerController) controller;
			}
			this.markerControllers.put(controller.getId(), controller);
		}
		
		if(this.mainPlayerMarkerController != null && this.otherPlayerMarkerController != null) {
			this.directionVisibility = new PlayerDirectionsVisibilityController(this.mainPlayerMarkerController, this.otherPlayerMarkerController);
			this.nameVisibility = new PlayerNameVisibilityController(this.mainPlayerMarkerController, this.otherPlayerMarkerController);
		}

	}

	public MapWidget(int z, IRasterTiledMap map, MapContext context, double tileScaling) {
		this(0, 0, z, 50, 50, map, context, tileScaling);
	}

	/**
	 * 
	 * @param layer
	 * @return this
	 * @throws InvalidLayerLevelException
	 */
	private MapWidget addMapLayer(MapLayerWidget layer) {
		switch(layer.getZ()) {
		case BACKGROUND_Z:
			throw new InvalidLayerLevelException("Z level " + layer.getZ() + " is reserved for background layer");
		case CONTROLLER_Z:
			throw new InvalidLayerLevelException("Z level " + layer.getZ() + " is reserved for controller layer");
		}
		super.addWidget(layer);
		return this;
	}

	private MapWidget setMapBackgroud(RasterMapLayerWidget background) {
		background.z = BACKGROUND_Z;
		background.tileScaling = this.tileScaling;
		super.removeWidget(this.background);
		super.addWidget(background);
		this.background = background;
		this.copyright.setComponent(background.map.getCopyright(SmyLibGui.getLanguage()));
		this.zoom(0);
		return this;
	}

	public void setBackground(IRasterTiledMap map) {
		this.discardPreviousErrors(this.background); // We don't care about errors for this background anumore
		this.setMapBackgroud(new RasterMapLayerWidget(map, this.tileScaling));
	}

	/**
	 * Adds a widget to the screen. Since this is a map before being a screen,
	 * {@link #addMapLayer(MapLayerWidget) addMapLayer} should be used instead
	 * and other types of widget should not be added to the map directly
	 * but rather on the parent screen.
	 * 
	 * @param widget to add
	 * @throws InvalidLayerLevelException if the widget has an incompatible z value
	 */
	@Override @Deprecated
	public Screen addWidget(IWidget widget) {
		if(widget instanceof MapLayerWidget) {
			this.addMapLayer((MapLayerWidget)widget);
		} else {
			switch(widget.getZ()) {
			case BACKGROUND_Z:
				throw new InvalidLayerLevelException("Z level " + widget.getZ() + " is reserved for background layer");
			case CONTROLLER_Z:
				throw new InvalidLayerLevelException("Z level " + widget.getZ() + " is reserved for controller layer");
			}
			super.addWidget(widget);
		}
		return this;
	}

	@Override
	public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean focused, Screen parent) {
		this.copyright.setAnchorX(this.getWidth() - 3).setAnchorY(this.getHeight() - this.copyright.getHeight()).setMaxWidth(this.width);
		this.scale.setX(15).setY(this.copyright.getAnchorY() - 15);
		this.errorText.setAnchorX(this.width / 2).setAnchorY(0).setMaxWidth(this.width - 40);
		if(!this.rightClickMenu.isVisible(this)) {
			int relativeMouseX = mouseX - x;
			int relativeMouseY = mouseY - y;
			this.updateMouseGeoPos(relativeMouseX, relativeMouseY);
		}
		Map<Class<?>, List<Marker>> markers = new HashMap<Class<?>, List<Marker>>();
		for(MarkerController<?> controller: this.markerControllers.values()) {
			markers.put(controller.getMarkerType(), new ArrayList<Marker>());
		}
		for(IWidget widget: this.widgets) {
			if(widget instanceof MapLayerWidget) {
				MapLayerWidget layer = (MapLayerWidget) widget;
				layer.width = this.width;
				layer.height = this.height;
				layer.tileScaling = this.tileScaling;
				if(!layer.equals(this.controller)) {
					layer.centerLongitude = this.controller.centerLongitude;
					layer.centerLatitude = this.controller.centerLatitude;
					layer.zoom = this.controller.zoom;
				}
			} else if(widget instanceof Marker) {
				for(Class<?> clazz: markers.keySet()) {
					if(clazz.isInstance(widget)) {
						markers.get(clazz).add((Marker)widget);
					}
				}
			}
		}
		for(MarkerController<?> controller: this.markerControllers.values()) {
			Marker[] existingMarkers = markers.get(controller.getMarkerType()).toArray(new Marker[] {});
			Marker[] newMarkers = controller.getNewMarkers(existingMarkers, this);
			for(Marker markerToAdd: newMarkers) {
				this.addWidget(markerToAdd);
			}
			if(controller.getMarkerType().equals(MainPlayerMarker.class) && newMarkers.length > 0) {
				this.mainPlayerMarker = (MainPlayerMarker) newMarkers[0];
			}
			if(this.restoreTrackingId != null) {
				for(Marker markerToAdd: newMarkers) {
					String id = markerToAdd.getIdentifier();
					if(id != null && id.equals(this.restoreTrackingId)) {
						this.track(markerToAdd);
						this.restoreTrackingId = null;
						TerramapMod.logger.debug("Restored tracking with " + id);
					}
				}
			}
		}

		/* The map markers have a higher priority than the background since they are on top,
		 * which means that they are updated before it moves,
		 * so they lag behind when the map moves fast if they are not updated again
		 */
		for(IWidget w: this.widgets) {
			if(w instanceof Marker) {
				w.onUpdate(this); 
			}
		}
		if(this.rcmMarkerController != null) this.rcmMarkerController.setVisibility(this.rightClickMenu.isVisible(this));
		super.draw(x, y, mouseX, mouseY, hovered, focused, parent);
	}

	@Override
	public void onUpdate(Screen parent) {
		super.onUpdate(parent);
		if(this.trackingMarker != null) {
			if(this.widgets.contains(this.trackingMarker) && Double.isFinite(this.trackingMarker.getLongitude()) && Double.isFinite(this.trackingMarker.getLatitude())) {
				this.setCenterLongitude(this.trackingMarker.getLongitude());
				this.setCenterLatitude(this.trackingMarker.getLatitude());
			} else {
				this.trackingMarker = null;
			}
		}
		if(this.reportedErrors.size() > 0) {
			String errorText = I18n.format("terramap.mapwidget.error.header") + "\n" + this.reportedErrors.get((int) ((System.currentTimeMillis() / 3000)%this.reportedErrors.size())).message;
			this.errorText.setText(errorText);
		}
	}

	private class ControllerMapLayer extends MapLayerWidget {

		public ControllerMapLayer(double tileScaling) {
			super(tileScaling);
			this.z = CONTROLLER_Z;
		}

		@Override
		public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean focused, Screen parent) {
			// Literally nothing to do here, this is strictly used to handle user input
		}

		@Override
		public boolean onClick(int mouseX, int mouseY, int mouseButton, @Nullable Screen parent) {
			if(isShortcutEnabled()) {
				MapWidget.this.teleportPlayerTo(MapWidget.this.mouseLongitude, MapWidget.this.mouseLatitude);
				if(MapWidget.this.getContext().equals(MapContext.FULLSCREEN)) {
					Minecraft.getMinecraft().displayGuiScreen(null);
				}
			}
			if(MapWidget.this.enableRightClickMenu && mouseButton == 1 && Math.abs(MapWidget.this.getMouseLatitude()) <= WebMercatorUtils.LIMIT_LATITUDE) {
				parent.showMenu(mouseX, mouseY, MapWidget.this.rightClickMenu);
			}
			return false;
		}

		@Override
		public boolean onDoubleClick(int mouseX, int mouseY, int mouseButton, @Nullable Screen parent) {

			// We don't care about double right clicks
			if(mouseButton != 0) this.onClick(mouseX, mouseY, mouseButton, parent);

			if(MapWidget.this.isInteractive() && mouseButton == 0) {
				this.zoom(mouseX, mouseY, 1);
			}
			return false;
		}

		@Override
		public void onMouseDragged(int mouseX, int mouseY, int dX, int dY, int mouseButton, @Nullable Screen parent) {
			if(MapWidget.this.isInteractive() && mouseButton == 0) {
				this.moveMap(dX, dY);
			}
		}

		@Override
		public void onKeyTyped(char typedChar, int keyCode, @Nullable Screen parent) {
		}

		@Override
		public boolean onMouseWheeled(int mouseX, int mouseY, int amount, @Nullable Screen parent) {
			if(MapWidget.this.isInteractive()) {
				int z = amount > 0? 1: -1;
				if(MapWidget.this.focusedZoom) {
					this.zoom(mouseX, mouseY, z);
				} else {
					this.zoom(z);
				}
			}
			return false;
		}

		public void zoom(int val) {
			this.zoom(this.width/2, this.height/2, val);
		}

		public void zoom(int mouseX, int mouseY, int zoom) {

			MapWidget.this.rightClickMenu.hide(null);

			double nzoom = this.zoom + zoom;
			double maxZoom = TerramapConfig.CLIENT.unlockZoom? 25: getMaxZoom();
			nzoom = Math.min(maxZoom, nzoom);
			nzoom = Math.max(getMinZoom(), nzoom);

			if(nzoom == this.zoom) return; // Do not move if we are not doing anything

			this.zoom = nzoom;
			double factor = Math.pow(2, zoom);
			double ndX = ((double)this.width/2 - mouseX) * factor;
			double ndY = ((double)this.height/2 - mouseY) * factor;
			if(factor > 1) {
				ndX = -ndX / 2;
				ndY = -ndY / 2;
			}

			//FIXME Re-implement better zoom
			this.setCenterLongitude(this.getScreenLongitude((double)this.width/2 + ndX));
			this.setCenterLatitude(this.getScreenLatitude((double)this.height/2 + ndY));
			//			this.mapVelocityX *= factor;
			//			this.mapVelocityY *= factor;
			//			this.updateMouseGeoPos(mouseX, mouseY);

		}

		public void moveMap(int dX, int dY) {
			MapWidget.this.trackingMarker = null;
			double nlon = this.getScreenLongitude((double)this.width/2 - dX);
			double nlat = this.getScreenLatitude((double)this.height/2 - dY);
			this.setCenterLongitude(nlon);
			this.setCenterLatitude(nlat);
		}

		@Override
		public String getTooltipText() {
			return isShortcutEnabled() ? I18n.format("terramap.mapwidget.shortcuts.tp"): "";
		}

		@Override
		public long getTooltipDelay() {
			return 0;
		}

	}

	private void updateMouseGeoPos(int mouseX, int mouseY) {
		this.mouseLongitude = controller.getScreenLongitude((double)mouseX);
		this.mouseLatitude = controller.getScreenLatitude((double)mouseY);
	}

	private void updateRightClickMenuEntries() {
		boolean hasProjection = TerramapClientContext.getContext().getProjection() != null;
		this.teleportMenuEntry.enabled = hasProjection;
		this.copyBlockMenuEntry.enabled = hasProjection;
		this.copyChunkMenuEntry.enabled = hasProjection;
		this.copyRegionMenuEntry.enabled = hasProjection;
		this.copy3drMenuEntry.enabled = hasProjection;
		this.copy2drMenuEntry.enabled = hasProjection;
		this.setProjectionMenuEntry.enabled = !TerramapClientContext.getContext().isInstalledOnServer();
	}

	private void teleportPlayerTo(double longitude, double latitude) {
		String cmd = TerramapClientContext.getContext().getTpCommand().replace("{longitude}", ""+longitude).replace("{latitude}", ""+latitude);
		if(TerramapClientContext.getContext().getProjection() != null) {
			try {
				double[] xz = TerramapClientContext.getContext().getProjection().fromGeo(longitude, latitude);
				cmd = cmd.replace("{x}", "" + xz[0]).replace("{z}", "" + xz[1]);
				this.sendChatMessage(cmd, false);
			} catch (OutOfProjectionBoundsException e) {
				String s = System.currentTimeMillis() + ""; //Just a random string
				this.reportError(s, I18n.format("terramap.mapwidget.error.tp"));
				this.scheduleWithDelay(() -> this.discardPreviousErrors(s), 5000);
			}
		} else {
			TerramapMod.logger.error("Tried to teleport from the map but the projection was null!");
		}
	}

	public Map<String, FeatureVisibilityController> getVisibilityControllers() {
		Map<String, FeatureVisibilityController> m = new LinkedHashMap<>();
		m.putAll(this.markerControllers);
		if(this.directionVisibility != null ) m.put(this.directionVisibility.getSaveName(), this.directionVisibility);
		if(this.nameVisibility != null) m.put(this.nameVisibility.getSaveName(), this.nameVisibility);
		return m;
	}

	public double getZoom() {
		return this.controller.getZoom();
	}

	public double getMaxZoom() {
		return TerramapConfig.CLIENT.unlockZoom? 25: this.background.map.getMaxZoom();
	}

	public double getMinZoom() {
		return this.background.map.getMinZoom();
	}

	public MapWidget setZoom(double zoom) {
		this.controller.setZoom(Math.max(this.getMinZoom(), Math.min(this.getMaxZoom(), zoom)));
		return this;
	}

	public MapWidget zoom(int zoom) {
		this.controller.zoom(zoom);
		return this;
	}

	public double getCenterLongitude() {
		return this.controller.getCenterLongitude();
	}

	public MapWidget setCenterLongitude(double longitude) {
		this.controller.setCenterLongitude(longitude);
		return this;
	}

	public double getCenterLatitude() {
		return this.controller.getCenterLatitude();
	}

	public MapWidget setCenterLatitude(double latitude) {
		this.controller.setCenterLatitude(latitude);
		return this;
	}

	public double[] getCenterPosition() {
		return new double[] {this.getCenterLongitude(), this.getCenterLatitude()};
	}

	public MapWidget setCenterPosition(double longitude, double latitude) {
		return this.setCenterLongitude(longitude).setCenterLatitude(latitude);
	}

	public MapWidget setCenterPosition(double[] position) {
		this.setCenterPosition(position[0], position[1]);
		return this;
	}

	public double getMouseLongitude() {
		return this.mouseLongitude;
	}

	public double getMouseLatitude() {
		return this.mouseLatitude;
	}

	public double[] getMousePosition() {
		return new double[] {this.mouseLongitude, this.mouseLatitude};
	}

	public MapWidget setX(int x) {
		this.x = x;
		return this;
	}

	public MapWidget setY(int y) {
		this.y = y;
		return this;
	}

	public MapWidget setWidth(int width) {
		this.width = width;
		return this;
	}

	public MapWidget setHeight(int height) {
		this.height = height;
		this.scale.setY(this.height - 20);
		return this;
	}

	public boolean isInteractive() {
		return this.interactive;
	}

	public MapWidget setInteractive(boolean yesNo) {
		this.interactive = yesNo;
		return this;
	}

	public boolean isRightClickMenuEnabled() {
		return this.enableRightClickMenu;
	}

	public MapWidget setRightClickMenuEnabled(boolean yesNo) {
		this.enableRightClickMenu = yesNo;
		return this;
	}

	public MapWidget enableRightClickMenu() {
		return this.setRightClickMenuEnabled(true);
	}

	public MapWidget disableRightClickMenu() {
		return this.setRightClickMenuEnabled(false);
	}

	public boolean getCopyrightVisibility() {
		return this.showCopyright;
	}

	public MapWidget setCopyrightVisibility(boolean yesNo) {
		this.showCopyright = yesNo;
		return this;
	}

	public void moveMap(int dX, int dY) {
		controller.moveMap(dX, dY);
	}

	public double getScreenX(double longitude) {
		return this.background.getScreenX(longitude);
	}

	public double getScreenY(double latitude) {
		return this.background.getScreenY(latitude);
	}

	public double getScreenLongitude(double xOnScreen) {
		return this.background.getScreenLongitude(xOnScreen);
	}

	public double getScreenLatitude(double yOnScreen) {
		return this.background.getScreenLatitude(yOnScreen);
	}

	public int getScaleX() {
		return this.scale.getX();
	}

	public MapWidget setScaleX(int x) {
		this.scale.setX(x);
		return this;
	}

	public int getScaleY() {
		return this.scale.getY();
	}

	public MapWidget setScaleY(int y) {
		this.scale.setY(y);
		return this;
	}

	public int getScaleWidth() {
		return this.scale.getWidth();
	}

	public MapWidget setScaleWidth(int width) {
		this.scale.setWidth(width);
		return this;
	}

	public boolean getScaleVisibility() {
		return this.scale.isVisible(this);
	}

	public MapWidget setScaleVisibility(boolean yesNo) {
		this.scale.setVisibility(yesNo);
		return this;
	}

	public MapContext getContext() {
		return this.context;
	}

	public boolean isTracking() {
		return this.trackingMarker != null;
	}

	public Marker getTracking() {
		return this.trackingMarker;
	}

	public void track(Marker marker) {
		this.trackingMarker = marker;
	}

	public MainPlayerMarker getMainPlayerMarker() {
		return this.mainPlayerMarker;
	}

	public IRasterTiledMap getBackgroundStyle() {
		return this.background.getMap();
	}
	
	public MapWidget trySetFeatureVisibility(String markerId, boolean value) {
		FeatureVisibilityController c = this.getVisibilityControllers().get(markerId);
		if(c != null) c.setVisibility(value);
		return this;
	}

	public void restoreTracking(String markerId) {
		this.restoreTrackingId = markerId;
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	public double getTileScaling() {
		return this.tileScaling;
	}

	public void setTileScaling(double tileScaling) {
		this.tileScaling = tileScaling;
	}

	private boolean isShortcutEnabled() {
		return TerramapClientContext.getContext().getProjection() != null && this.isInteractive() && Keyboard.isKeyDown(KeyBindings.MAP_SHORTCUT.getKeyCode());
	}

	@Override
	public boolean isVisible(Screen parent) {
		return this.visible;
	}

	public MapWidget setVisibility(boolean yesNo) {
		this.visible = yesNo;
		return this;
	}
	
	public void reportError(Object source, String errorMessage) {
		ReportedError error = new ReportedError(source, errorMessage);
		if(this.reportedErrors.contains(error)) return;
		this.reportedErrors.add(error);
		if(this.reportedErrors.size() > MAX_ERRORS_KEPT) {
			this.reportedErrors.remove(0);
		}
	}
	
	public void discardPreviousErrors(Object source) {
		List<ReportedError> errsToRm = new ArrayList<>();
		for(ReportedError e: this.reportedErrors) {
			if(e.source.equals(source)) errsToRm.add(e);
		}
		this.reportedErrors.removeAll(errsToRm);
	}
	
	private class ReportedError {
		
		private Object source;
		private String message;
		
		private ReportedError(Object source, String message) {
			this.source = source;
			this.message = message;
		}
	}

}
