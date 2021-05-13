package fr.thesmyler.terramap.gui.widgets.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.FlexibleWidgetContainer;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.Font;
import fr.thesmyler.smylibgui.util.Util;
import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.smylibgui.widgets.MenuWidget;
import fr.thesmyler.smylibgui.widgets.MenuWidget.MenuEntry;
import fr.thesmyler.smylibgui.widgets.text.TextAlignment;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
import fr.thesmyler.terramap.GeoServices;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.config.TerramapConfig;
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
import net.buildtheearth.terraplusplus.control.PresetEarthGui;
import net.buildtheearth.terraplusplus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.text.TextComponentString;

//TODO Disable "Center map here" if not interactive
public class MapWidget extends FlexibleWidgetContainer {

	private boolean interactive = true;
	private boolean focusedZoom = true; // Zoom where the cursor is (true) or at the center of the map (false) when using the wheel
	private boolean enableRightClickMenu = true;
	private boolean showCopyright = true;
	private boolean debugMode = false;
	private boolean visible = true;

	private final ControllerMapLayer controller;
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
	private long lastUpdateTime = Long.MIN_VALUE;
	
	private float drag = 0.3f;
	private float zoomSnapping = 1f;
	private float zoomResponsiveness = 0.01f;
	protected double tileScaling;
	private float orientation = 0;

	private final MenuWidget rightClickMenu;
	private final MenuEntry teleportMenuEntry;
	private final MenuEntry copyBlockMenuEntry;
	private final MenuEntry copyChunkMenuEntry;
	private final MenuEntry copyRegionMenuEntry;
	private final MenuEntry copy3drMenuEntry;
	private final MenuEntry copy2drMenuEntry;
	private final MenuEntry setProjectionMenuEntry;

	private TextWidget copyright;
	private ScaleIndicatorWidget scale = new ScaleIndicatorWidget(-1);
	
	private final Profiler profiler = new Profiler();
	private static final GuiScreen CHAT_SENDER_GUI = new GuiScreen() {}; // The only reason this exists is so we can use it to send chat messages
	static { CHAT_SENDER_GUI.mc = Minecraft.getMinecraft(); }

	private TextWidget errorText;

	private List<ReportedError> reportedErrors = new ArrayList<>();
	private static final int MAX_ERRORS_KEPT = 10;

	private final MapContext context;

	public static final int BACKGROUND_Z = Integer.MIN_VALUE;
	public static final int CONTROLLER_Z = 0;

	public MapWidget(float x, float y, int z, float width, float height, IRasterTiledMap map, MapContext context, double tileScaling) {
		super(x, y, z, width, height);
		this.setDoScissor(true);
		this.context = context;
		this.tileScaling = tileScaling;
		Font font = SmyLibGui.DEFAULT_FONT;
		Font smallFont = Util.getSmallestFont();
		this.copyright = new TextWidget(Integer.MAX_VALUE, new TextComponentString(""), smallFont) {
			@Override
			public boolean isVisible(WidgetContainer parent) {
				return MapWidget.this.showCopyright;
			}
		};
		this.copyright.setBackgroundColor(Color.DARK_OVERLAY).setPadding(3).setAlignment(TextAlignment.LEFT).setShadow(false);
		super.addWidget(this.copyright);

		this.errorText = new TextWidget(Integer.MAX_VALUE, font) {
			@Override
			public boolean isVisible(WidgetContainer parent) {
				return MapWidget.this.reportedErrors.size() > 0 && MapWidget.this.context == MapContext.FULLSCREEN;
			}
		};
		this.errorText.setBackgroundColor(Color.ERROR_OVERLAY).setPadding(5).setAlignment(TextAlignment.CENTER).setShadow(false).setBaseColor(Color.WHITE);
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
			EarthGeneratorSettings stg = TerramapClientContext.getContext().getGeneratorSettings();
			Minecraft.getMinecraft().displayGuiScreen(new PresetEarthGui(null, stg != null ? stg.toString(): PresetEarthGui.DEFAULT_PRESETS.get("default"), s ->  {
				TerramapClientContext.getContext().setGeneratorSettings(EarthGeneratorSettings.parse(s));
				TerramapClientContext.getContext().saveSettings();
			}));
		});

		this.controller = new ControllerMapLayer(this.tileScaling);
		super.addWidget(this.controller);

		this.setMapBackgroud(new RasterMapLayerWidget(map, this.tileScaling));

		this.scale.setX(15).setY(this.getHeight() - 30);
		this.addWidget(scale);
		this.updateRightClickMenuEntries();
		this.updateMouseGeoPos(this.getWidth()/2, this.getHeight()/2);

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
	
	@Override
	public void init() {
		this.copyright.setFont(Util.getSmallestFont());
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
		this.copyright.setText(background.map.getCopyright(SmyLibGui.getLanguage()));
		this.zoom(0);
		return this;
	}

	public void setBackground(IRasterTiledMap map) {
		this.discardPreviousErrors(this.background); // We don't care about errors for this background anymore
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
	public WidgetContainer addWidget(IWidget widget) {
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
	public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
		
		//TODO Remove debug
		this.orientation += 1f;
		if(orientation > 360) this.orientation -= 360;
		
		this.profiler.startSection("misc-gui-updates");
		this.copyright.setAnchorX(this.getWidth() - 3).setAnchorY(this.getHeight() - this.copyright.getHeight()).setMaxWidth(this.getWidth());
		this.scale.setX(15).setY(this.copyright.getAnchorY() - 15);
		this.errorText.setAnchorX(this.getWidth() / 2).setAnchorY(0).setMaxWidth(this.getWidth() - 40);
		
		if(!this.rightClickMenu.isVisible(this)) {
			float relativeMouseX = mouseX - x;
			float relativeMouseY = mouseY - y;
			this.updateMouseGeoPos(relativeMouseX, relativeMouseY);
		}
		
		// Sync the various layers with the map and gather markers at the same time
		this.profiler.endStartSection("update-layers");
		Map<Class<?>, List<Marker>> markers = new HashMap<Class<?>, List<Marker>>();
		for(MarkerController<?> controller: this.markerControllers.values()) {
			markers.put(controller.getMarkerType(), new ArrayList<Marker>());
		}
		for(IWidget widget: this.widgets) {
			if(widget instanceof MapLayerWidget) {
				MapLayerWidget layer = (MapLayerWidget) widget;
				layer.width = this.getWidth();
				layer.height = this.getHeight();
				layer.tileScaling = this.tileScaling;
				if(!layer.equals(this.controller)) {
					layer.centerLongitude = this.controller.centerLongitude;
					layer.centerLatitude = this.controller.centerLatitude;
					layer.zoom = this.controller.zoom;
					layer.orientation = this.orientation;
				}
			} else if(widget instanceof Marker) {
				for(Class<?> clazz: markers.keySet()) {
					if(clazz.isInstance(widget)) {
						markers.get(clazz).add((Marker)widget);
					}
				}
			}
		}
		
		// Update the markers
		this.profiler.endStartSection("query-marker-controllers");
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

		/* 
		 * The map markers have a higher priority than the background since they are on top,
		 * which means that they are updated before it moves,
		 * so they lag behind when the map moves fast if they are not updated again
		 * 
		 * This is not really ideal, but negligible compared to other stuff
		 */
		this.profiler.endStartSection("update-markers");
		for(IWidget w: this.widgets) {
			if(w instanceof Marker) {
				w.onUpdate(this); 
			}
		}
		if(this.rcmMarkerController != null) this.rcmMarkerController.setVisibility(this.rightClickMenu.isVisible(this));
		
		// Actually draw the map
		this.profiler.endStartSection("draw");
		super.draw(x, y, mouseX, mouseY, hovered, focused, parent);
		this.getFont().drawCenteredString(x + 200, y+200, "" + this.orientation, Color.RED, false);
		this.profiler.endSection();
	}

	@Override
	public void onUpdate(WidgetContainer parent) {
		super.onUpdate(parent);
		long ctime = System.currentTimeMillis();
		long dt = ctime - this.lastUpdateTime;
		
		// Both these things do time dependent integration operations, so if the integration step is irrelevant, skip
		if(dt < 1000) {
			this.controller.processInertia(dt);
			this.controller.processZoom(dt);
		}
		
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
			this.errorText.setText(new TextComponentString(errorText));
		}
		
		this.lastUpdateTime = ctime;
	}

	private class ControllerMapLayer extends MapLayerWidget {
		
		double zoomLongitude, zoomLatitude;
		double zoomTarget = 0;
		float speedX, speedY;

		public ControllerMapLayer(double tileScaling) {
			super(tileScaling);
			this.z = CONTROLLER_Z;
		}

		@Override
		public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
			// Literally nothing to do here, this is strictly used to handle user input
		}

		@Override
		public boolean onClick(float mouseX, float mouseY, int mouseButton, @Nullable WidgetContainer parent) {
			this.cancelMovement();
			if(MapWidget.this.isShortcutEnabled()) {
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
		public boolean onDoubleClick(float mouseX, float mouseY, int mouseButton, @Nullable WidgetContainer parent) {
			this.cancelMovement();
			// We don't care about double right clicks
			if(mouseButton != 0) this.onClick(mouseX, mouseY, mouseButton, parent);

			if(MapWidget.this.isInteractive() && mouseButton == 0) {
				this.zoom(this.getScreenLongitude(mouseX), this.getScreenLatitude(mouseY), 1);
			}
			return false;
		}

		@Override
		public void onMouseDragged(float mouseX, float mouseY, float dX, float dY, int mouseButton, @Nullable WidgetContainer parent, long dt) {
			if(MapWidget.this.isInteractive() && mouseButton == 0) {
				this.moveMap(dX, dY);
				this.speedX = dX / dt;
				this.speedY = dY / dt;
			}
		}

		@Override
		public void onKeyTyped(char typedChar, int keyCode, @Nullable WidgetContainer parent) {
		}

		@Override
		public boolean onMouseWheeled(float mouseX, float mouseY, int amount, @Nullable WidgetContainer parent) {
			if(MapWidget.this.isInteractive()) {
				double z = amount > 0? 1: -1;
				z *= MapWidget.this.zoomSnapping;
				if(MapWidget.this.focusedZoom) {
					double longitude = this.getScreenLongitude(mouseX);
					double latitude = this.getScreenLatitude(mouseY);
					this.zoom(longitude, latitude, z);
				} else {
					this.zoom(z);
				}
			}
			return false;
		}

		public void zoom(double val) {
			this.zoom(this.getCenterLongitude(), this.getCenterLatitude(), val);
		}
		
		public void zoom(double longitude, double latitude, double zoom) {
			this.zoomLongitude = longitude;
			this.zoomLatitude = latitude;
			this.zoomTarget += zoom;
		}
		
		private void processInertia(long dt) {
			if((this.speedX != 0 || this.speedY != 0) && dt < 1000 && !Mouse.isButtonDown(0)) {
				float dX = (float) (this.speedX * dt);
				float dY = (float) (this.speedY * dt);
				this.speedX -= MapWidget.this.drag*this.speedX;
				this.speedY -= MapWidget.this.drag*this.speedY;
				if(Math.abs(this.speedX) < 0.01f && Math.abs(this.speedY) < 0.01f) {
					this.speedX = 0f;
					this.speedY = 0f;
				}
				if(Math.abs(dX) < 100 && Math.abs(dY) < 100) {
					this.moveMap(dX, dY);
				}
				
			}
		}
		
		private void processZoom(long dt) {
			
			// Round up the targeted zoom level to the nearest multiple of the snapping value and ensure it is within bounds
			double zoomTarget = Math.round(this.zoomTarget / MapWidget.this.zoomSnapping) * MapWidget.this.zoomSnapping;
			double maxZoom = TerramapConfig.CLIENT.unlockZoom? 25: getMaxZoom();
			zoomTarget = Math.min(zoomTarget, maxZoom);
			zoomTarget = Math.max(MapWidget.this.getMinZoom(), zoomTarget);
			
			// If we are close enough of the desired zoom level, just finish reaching it
			if(Math.abs(this.zoom - zoomTarget) < 0.01d) {
				this.zoomTarget = zoomTarget;
				this.zoom = zoomTarget;
				return;
			}
			
			MapWidget.this.rightClickMenu.hide(null);

			// Compute a delta to the new zoom value, exponential decay, and ensure it is within bounds
			double maxDzoom = zoomTarget - this.zoom;
			double dzoom = MapWidget.this.zoomResponsiveness*(maxDzoom)*dt;
			dzoom = maxDzoom > 0 ? Math.min(dzoom, maxDzoom) : Math.max(dzoom, maxDzoom);

			// The position that needs to stay static and how far it is from the center of the screen
			double x = this.getScreenX(this.zoomLongitude);
			double y = this.getScreenY(this.zoomLatitude);
			double dX = x - this.width/2;
			double dY = y - this.height/2;
			
			/*
			 *  Get the scale factor from the previous zoom to the new one
			 *  Then do some basic arithmetic to know much the center of the screen should move
			 */
			double factor = Math.pow(2, dzoom);
			double ndX = dX * (1 - factor);
			double ndY = dY * (1 - factor);
			this.speedX *= factor;
			this.speedY *= factor;
			
			this.zoom += dzoom; // That's what we are here for

			// And move so the static point is static
			this.setCenterLongitude(this.getScreenLongitude((double)this.width/2 - ndX));
			this.setCenterLatitude(this.getScreenLatitude((double)this.height/2 - ndY));
		}

		public void moveMap(float dX, float dY) {
			MapWidget.this.trackingMarker = null;
			double nlon = this.getScreenLongitude(this.width/2 - dX);
			double nlat = this.getScreenLatitude(this.height/2 - dY);
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
		
		public void cancelMovement() {
			this.speedX = this.speedY = 0f;
		}
		
		@Override
		public void setZoom(double zoom) {
			super.setZoom(zoom);
			this.zoomTarget = zoom;
		}

	}

	private void updateMouseGeoPos(float mouseX, float mouseY) {
		this.mouseLongitude = controller.getScreenLongitude(mouseX);
		this.mouseLatitude = controller.getScreenLatitude(mouseY);
	}

	private void updateRightClickMenuEntries() {
		boolean hasProjection = TerramapClientContext.getContext().getProjection() != null;
		this.teleportMenuEntry.enabled = true;
		this.copyBlockMenuEntry.enabled = hasProjection;
		this.copyChunkMenuEntry.enabled = hasProjection;
		this.copyRegionMenuEntry.enabled = hasProjection;
		this.copy3drMenuEntry.enabled = hasProjection;
		this.copy2drMenuEntry.enabled = hasProjection;
		this.setProjectionMenuEntry.enabled = (!TerramapClientContext.getContext().isInstalledOnServer() && TerramapClientContext.getContext().isOnEarthWorld());
	}

	private void teleportPlayerTo(double longitude, double latitude) {
		String cmd = TerramapClientContext.getContext().getTpCommand().replace("{longitude}", ""+longitude).replace("{latitude}", ""+latitude);
		GeographicProjection projection = TerramapClientContext.getContext().getProjection();
		if(projection == null && (cmd.contains("{x}") || cmd.contains("{z}"))) {
			String s = System.currentTimeMillis() + ""; // Just a random string
			this.reportError(s, I18n.format("terramap.mapwidget.error.tp"));
			this.scheduleWithDelay(() -> this.discardPreviousErrors(s), 5000);
			return;
		}
		if(projection != null) {
			try {
				double[] xz = TerramapClientContext.getContext().getProjection().fromGeo(longitude, latitude);
				cmd = cmd.replace("{x}", "" + xz[0]).replace("{z}", "" + xz[1]);
			} catch (OutOfProjectionBoundsException e) {
				String s = System.currentTimeMillis() + ""; // Just a random string
				this.reportError(s, I18n.format("terramap.mapwidget.error.tp"));
				this.scheduleWithDelay(() -> this.discardPreviousErrors(s), 5000);
			}
		}
		CHAT_SENDER_GUI.sendChatMessage(cmd, false);
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
		this.controller.zoomLongitude = this.controller.getCenterLongitude();
		this.controller.zoomLatitude = this.controller.getCenterLatitude();
		return this;
	}

	public MapWidget zoom(double zoom) {
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
	
	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);
		this.scale.setY(this.getHeight() - 20);
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

	public void moveMap(float dX, float dY) {
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

	public float getScaleX() {
		return this.scale.getX();
	}

	public MapWidget setScaleX(float x) {
		this.scale.setX(x);
		return this;
	}

	public float getScaleY() {
		return this.scale.getY();
	}

	public MapWidget setScaleY(float y) {
		this.scale.setY(y);
		return this;
	}

	public float getScaleWidth() {
		return this.scale.getWidth();
	}

	public MapWidget setScaleWidth(float width) {
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
		this.profiler.profilingEnabled = debugMode;
		if(!debugMode) this.profiler.clearProfiling();
	}

	public double getTileScaling() {
		return this.tileScaling;
	}

	public void setTileScaling(double tileScaling) {
		this.tileScaling = tileScaling;
	}

	private boolean isShortcutEnabled() {
		return this.isInteractive() && Keyboard.isKeyDown(KeyBindings.MAP_SHORTCUT.getKeyCode());
	}

	@Override
	public boolean isVisible(WidgetContainer parent) {
		return this.visible;
	}

	public MapWidget setVisibility(boolean yesNo) {
		this.visible = yesNo;
		return this;
	}
	
	public float getInertia() {
		return this.drag;
	}
	
	public void setInertia(float inertia) {
		this.drag = inertia;
	}
	
	public float getZoomSnapping() {
		return this.zoomSnapping;
	}
	
	public void setZoomSnapping(float value) {
		this.zoomSnapping = value;
	}
	
	public float getZoomResponsiveness() {
		return this.zoomResponsiveness;
	}

	public void setZoomResponsiveness(float value) {
		this.zoomResponsiveness = value;
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
	
	public Profiler getProfiler() {
		return this.profiler;
	}

}
