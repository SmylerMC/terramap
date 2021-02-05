package fr.thesmyler.terramap.gui.widgets.markers.markers;

import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import fr.thesmyler.terramap.maps.utils.WebMercatorUtils;
import net.minecraft.util.text.ITextComponent;

public abstract class Marker implements IWidget {
	
	protected int width, height;
	protected int minZoom;
	protected int maxZoom;
	private int x, y;
	private MarkerController<?> controller;

	public Marker(MarkerController<?> controller, int width, int height, int minZoom, int maxZoom) {
		this.controller = controller;
		this.width = width;
		this.height = height;
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
	}
	
	public Marker(MarkerController<?> controller, int width, int height) {
		this(controller, width, height, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	@Override
	public int getX() {
		return this.x;
	}

	@Override
	public int getY() {
		return this.y;
	}

	@Override
	public int getZ() {
		return this.controller.getZLayer();
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return this.height;
	}

	public abstract double getLongitude();

	public abstract double getLatitude();
	
	public abstract int getDeltaX();
	
	public abstract int getDeltaY();

	@Override
	public void onUpdate(Screen parent) {
		if(parent instanceof MapWidget) {
			MapWidget map = (MapWidget) parent;
			this.update(map);
			this.x = (int) Math.round(map.getScreenX(this.getLongitude())) + this.getDeltaX();
			this.y = (int) Math.round(map.getScreenY(this.getLatitude())) + this.getDeltaY();
		}
	}
	
	public void update(MapWidget map) {}
	
	@Override
	public boolean isVisible(Screen parent) {
		if(!this.controller.getVisibility()) return false;
		if(!WebMercatorUtils.isPositionOnMap(this.getLongitude(), this.getLatitude())) return false;
		if(parent instanceof MapWidget) {
			MapWidget map = (MapWidget)parent;
			double zoom = map.getZoom();
			return this.minZoom <= zoom && zoom <= this.maxZoom;
		}
		return true;
	}
	
	public String getControllerId() {
		return this.controller.getId();
	}
	
	public abstract boolean canBeTracked();
	
	@Override
	public boolean onClick(int mouseX, int mouseY, int mouseButton, Screen parent) {
		return true; //TODO do not prevent the map from being dragged if clicked
	}
	
	@Override
	public boolean onDoubleClick(int mouseX, int mouseY, int mouseButton, Screen parent) {
		if(this.canBeTracked() && parent instanceof MapWidget) {
			MapWidget map = (MapWidget) parent;
			map.track(this);
		}
		return false;
	}
	
	public MarkerController<?> getController() {
		return this.controller;
	}
	
	public abstract ITextComponent getDisplayName();
	
	/**
	 * This identifier shall be used to resume tracking this marker is the map is saved and closed then opened again
	 * So it cannot depend on runtime and should be unique. The convention is markertype:uuid
	 * 
	 * @return a String uniquely identifying this marker
	 */
	public abstract String getIdentifier();
	
}
