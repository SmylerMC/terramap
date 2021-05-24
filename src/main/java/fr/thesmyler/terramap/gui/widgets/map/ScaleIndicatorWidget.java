package fr.thesmyler.terramap.gui.widgets.map;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.terramap.util.GeoUtil;
import fr.thesmyler.terramap.util.WebMercatorUtil;

//TODO Make this even more accurate
public class ScaleIndicatorWidget implements IWidget {

    private float x, y;
    private int z;
    private float width;
    private boolean visible = true;

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
    public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        float barY = y + 5;
        String lengthstr = "-";
        float barwidth = this.getWidth();
        if(parent instanceof MapWidget) {

            MapWidget map = (MapWidget) parent;

            double[] point1 = map.getScreenGeoPos(this.getX(), this.getY() + 5);
            double[] point2 = map.getScreenGeoPos(this.getX() + this.getWidth(), this.getY() + 5);

            if(Math.abs(point1[1]) < WebMercatorUtil.LIMIT_LATITUDE && Math.abs(point2[1]) < WebMercatorUtil.LIMIT_LATITUDE) {

                double scale = GeoUtil.distanceHaversine(point1[0], point1[1], point2[0], point2[1]);
                String[] units = {"m", "km"};
                int j=0;
                for(; scale >= 1000 && j<units.length-1; j++) scale /= 1000;
                lengthstr = "" + Math.round(scale) + " " + units[j];
            }
        }
        float strwidth = parent.getFont().getStringWidth(lengthstr);
        parent.getFont().drawString(x + barwidth/2 - strwidth/2, barY - parent.getFont().height() - 5, lengthstr, Color.DARKER_GRAY, false);
        RenderUtil.drawRect(x, barY, x + barwidth, barY+2, Color.DARKER_GRAY);
        RenderUtil.drawRect(x, barY-4, x+2, barY+6, Color.DARKER_GRAY);
        RenderUtil.drawRect(x-2 + barwidth, barY-4, x + barwidth, barY+6, Color.DARKER_GRAY);
    }

}
