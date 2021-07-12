package fr.thesmyler.terramap.gui.widgets.markers.markers;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import fr.thesmyler.terramap.util.geo.GeoPoint;
import fr.thesmyler.terramap.util.geo.WebMercatorUtil;
import fr.thesmyler.terramap.util.math.Vec2d;
import net.minecraft.util.text.ITextComponent;

public abstract class Marker implements IWidget {

    protected float width, height;
    protected int minZoom;
    protected int maxZoom;
    private float x, y;
    private MarkerController<?> controller;

    public Marker(MarkerController<?> controller, float width, float height, int minZoom, int maxZoom) {
        this.controller = controller;
        this.width = width;
        this.height = height;
        this.minZoom = minZoom;
        this.maxZoom = maxZoom;
    }

    public Marker(MarkerController<?> controller, float width, float height) {
        this(controller, width, height, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public float getX() {
        return this.x;
    }

    @Override
    public float getY() {
        return this.y;
    }

    @Override
    public int getZ() {
        return this.controller.getZLayer();
    }

    @Override
    public float getWidth() {
        return this.width;
    }

    @Override
    public float getHeight() {
        return this.height;
    }

    public abstract GeoPoint getLocation();

    public abstract float getDeltaX();

    public abstract float getDeltaY();

    @Override
    public void onUpdate(float mouseX, float mouseY, WidgetContainer parent) {
        if(parent instanceof MapWidget) {
            MapWidget map = (MapWidget) parent;
            this.update(map);
            GeoPoint location = this.getLocation();
            if(location != null) {
                Vec2d xy = map.getScreenPosition(this.getLocation());
                this.x = (float) (xy.x + this.getDeltaX());
                this.y = (float) (xy.y + this.getDeltaY());
            }
        }
    }

    public void update(MapWidget map) {}

    @Override
    public boolean isVisible(WidgetContainer parent) {
        if(!this.controller.getVisibility()) return false;
        GeoPoint location = this.getLocation();
        if(location == null || !WebMercatorUtil.PROJECTION_BOUNDS.contains(this.getLocation())) return false;
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
    public boolean onClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
        return true;
    }

    @Override
    public boolean onDoubleClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
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
