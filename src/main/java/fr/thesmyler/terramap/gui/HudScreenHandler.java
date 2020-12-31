package fr.thesmyler.terramap.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.screen.HudScreen;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapRemote;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.gui.config.HudConfigScreen;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.AnimalMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MobMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.OtherPlayerMarkerController;
import fr.thesmyler.terramap.maps.TiledMap;
import net.minecraft.client.Minecraft;

public abstract class HudScreenHandler {

	private static MapWidget map;

	public static void init(HudScreen screen) {

		screen.removeAllWidgets();
		screen.cancellAllScheduled();

		if(TerramapRemote.getRemote().allowsMap(MapContext.MINIMAP) && !(Minecraft.getMinecraft().currentScreen instanceof HudConfigScreen)) {
			if(map == null) {
				map = new MapWidget(10, TerramapRemote.getRemote().getMapStyles().values().toArray(new TiledMap[0])[0], MapContext.MINIMAP, TerramapConfig.getEffectiveMinimapTileScaling());
				map.setInteractive(false);
				map.setCopyrightVisibility(false);
				map.setScaleVisibility(false);
				map.scheduleAtUpdate(() -> {
					if(TerramapRemote.getRemote().getProjection() != null) {
						map.track(map.getMainPlayerMarker());
					}
				});
			}

			updateMinimap();

			screen.addWidget(map);
		}
	}

	public static void updateMinimap() {
		HudScreen screen = SmyLibGui.getHudScreen();
		if(map == null) {
			init(screen);
			return;
		}
		map.setX(Math.round((float)TerramapConfig.minimapPosX / 100 * screen.getWidth()));
		map.setY(Math.round((float)TerramapConfig.minimapPosY / 100 * screen.getHeight()));
		map.setWidth(Math.round((float)TerramapConfig.minimapWidth / 100 * screen.getWidth()));
		map.setHeight(Math.round((float)TerramapConfig.minimapHeight / 100 * screen.getHeight()));
		Map<String, Boolean> markerVisibility = new HashMap<String, Boolean>();
		markerVisibility.put(AnimalMarkerController.ID, TerramapConfig.minimapShowEntities);
		markerVisibility.put(MobMarkerController.ID, TerramapConfig.minimapShowEntities);
		markerVisibility.put(OtherPlayerMarkerController.ID, TerramapConfig.minimapShowOtherPlayers);
		map.setMarkersVisibilities(markerVisibility);
		Map<String, TiledMap> styles = TerramapRemote.getRemote().getMapStyles();
		TiledMap bg = styles.get(TerramapConfig.minimapStyle);
		if(bg == null || ! bg.isAllowedOnMinimap()) {
			ArrayList<TiledMap> maps = new ArrayList<TiledMap>(styles.values());
			Collections.sort(maps, Collections.reverseOrder());
			bg = maps.get(0);
		}
		map.setBackground(bg);
		int zoomLevel = Math.max(bg.getMinZoom(), TerramapConfig.minimapZoomLevel);
		zoomLevel = Math.min(bg.getMaxZoom(), TerramapConfig.minimapZoomLevel);
		map.setZoom(zoomLevel);
		map.setZoom(TerramapConfig.minimapZoomLevel);

		map.setTileScaling(TerramapConfig.getEffectiveMinimapTileScaling());
		map.setVisibility(TerramapConfig.minimapEnable && TerramapRemote.getRemote().allowsMap(MapContext.MINIMAP));
	}

	public static void zoomInMinimap() {
		map.zoom(1);
		TerramapConfig.minimapZoomLevel = (int) map.getZoom();
		TerramapConfig.sync();
	}

	public static void zoomOutMinimap() {
		map.zoom(-1);
		TerramapConfig.minimapZoomLevel = (int) map.getZoom();
		TerramapConfig.sync();
	}

	public static void toggleMinimap() {
		if(TerramapRemote.getRemote().allowsMap(MapContext.MINIMAP)) {
			map.setVisibility(!map.isVisible(null));
			TerramapConfig.minimapEnable = map.isVisible(null);
			TerramapConfig.sync();
		}
	}
}
