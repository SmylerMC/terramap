package fr.thesmyler.terramap.gui.widgets.map;

import net.smyler.smylib.gui.containers.WidgetContainer;
import net.smyler.smylib.Color;
import net.smyler.smylib.gui.widgets.Widget;
import net.smyler.smylib.gui.DrawContext;
import net.smyler.terramap.util.geo.GeoPointMutable;
import net.smyler.terramap.util.geo.WebMercatorUtil;

//TODO Make this even more accurate
public class ScaleIndicatorWidget implements Widget {

    private float x, y;
    private final int z;
    private float width;
    private boolean visible = true;
    private final GeoPointMutable point1 = new GeoPointMutable();
    private final GeoPointMutable point2 = new GeoPointMutable();

    public ScaleIndicatorWidget(float x, float y, int z, float width) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
    }

    public ScaleIndicatorWidget(int z) {
        this(0, 0, z, 50);
    }

    @Override
    public float getX() {
        return this.x;
    }

    public ScaleIndicatorWidget setX(float x) {
        this.x = x;
        return this;
    }

    @Override
    public float getY() {
        return this.y;
    }

    public ScaleIndicatorWidget setY(float y) {
        this.y = y;
        return this;
    }

    @Override
    public int getZ() {
        return this.z;
    }

    @Override
    public float getWidth() {
        return this.width;
    }

    public ScaleIndicatorWidget setWidth(float width) {
        this.width = width;
        return this;
    }

    @Override
    public float getHeight() {
        return 10;
    }

    @Override
    public boolean isVisible(WidgetContainer parent) {
        return this.visible;
    }

    public ScaleIndicatorWidget setVisibility(boolean yesNo) {
        this.visible = yesNo;
        return this;
    }

    @Override
    public void draw(DrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        float barY = y + 5;
        String lengthstr = "-";
        float barwidth = this.getWidth();
        if(parent instanceof MapWidget) {

            MapWidget map = (MapWidget) parent;

            map.getScreenLocation(this.point1, this.getX(), this.getY() + 5);
            map.getScreenLocation(this.point2, this.getX() + this.getWidth(), this.getY() + 5);

            if(WebMercatorUtil.PROJECTION_BOUNDS.contains(this.point1) && WebMercatorUtil.PROJECTION_BOUNDS.contains(this.point2)) {

                double scale = this.point1.distanceTo(point2);
                String[] units = {"cm", "m", "km"};
                int j=1;
                for(; scale >= 1000 && j<units.length-1; j++) scale /= 1000;
                if(scale < 1) {
                    scale *= 100;
                    j = 0;
                }
                lengthstr = Math.round(scale) + " " + units[j];
            }
        }
        float strwidth = parent.getFont().getStringWidth(lengthstr);
        parent.getFont().drawString(x + barwidth/2 - strwidth/2, barY - parent.getFont().height() - 5, lengthstr, Color.DARKER_GRAY, false);
        context.drawRectangle(x, barY, x + barwidth, barY+2, Color.DARKER_GRAY);
        context.drawRectangle(x, barY-4, x+2, barY+6, Color.DARKER_GRAY);
        context.drawRectangle(x-2 + barwidth, barY-4, x + barwidth, barY+6, Color.DARKER_GRAY);
    }

}
