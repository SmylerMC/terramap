package fr.thesmyler.terramap.gui.widgets.markers.markers;

import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;

public abstract class MapMarker implements IWidget {
	
	private int width, height;
	private int x, y;
	private MarkerController<?> controller;

	public MapMarker(MarkerController<?> controller, int width, int height) {
		this.controller = controller;
		this.width = width;
		this.height = height;
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
	public boolean isVisible() {
		return this.controller.areMakersVisible();
	}
	
	public String getControllerId() {
		return this.controller.getId();
	}
	
	@Override
	public boolean onClick(int mouseX, int mouseY, int mouseButton, Screen parent) {
		return true; //TODO do not prevent the map from being dragged if clicked
	}
	
	@Override
	public boolean onDoubleClick(int mouseX, int mouseY, int mouseButton, Screen parent) {
		//TODO Track the marker if it enables it
		return false;
	}
	
}
