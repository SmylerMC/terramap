package fr.thesmyler.terramap.gui.widgets.map.layer;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.profiler.Profiler;

public class RenderingDeltaPreviewLayer extends MapLayer {

    private double realLongitude, realLatitude;

    public RenderingDeltaPreviewLayer(double tileScaling, double realCenterLongitude, double realCenterLatitude) {
        super(tileScaling);
        this.z = -1;
        this.realLongitude = realCenterLongitude;
        this.realLatitude = realCenterLatitude;
    }
    
    @Override
    public String getId() {
        return "delta-preview";
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {

        MapWidget parentMap = (MapWidget) parent;
        Profiler profiler = parentMap.getProfiler();
        profiler.startSection("render-delta-preview-layer");

        float width = this.getWidth();
        float height = this.getHeight();
        double extendedWidth = this.getExtendedWidth();
        double extendedHeight = this.getExtendedHeight();
        double realCenterX = this.getRenderX(this.realLongitude);
        double realCenterY = this.getRenderY(this.realLatitude);

        GlStateManager.pushMatrix();
        this.applyRotationGl(x, y);
        RenderUtil.drawStrokeLine(Color.RED, 2f,
                extendedWidth / 2, extendedHeight / 2,
                realCenterX, extendedHeight / 2,
                realCenterX, realCenterY);
        GlStateManager.popMatrix();

        float centerHole = 10;
        float linesWidth = 1f;
        RenderUtil.drawStrokeLine(Color.DARK_GRAY, linesWidth, 
                x + width / 2, y,
                x + width / 2, y + height / 2 - centerHole);
        RenderUtil.drawStrokeLine(Color.DARK_GRAY, linesWidth, 
                x + width / 2, y + height / 2 + centerHole,
                x + width / 2, y + height);
        RenderUtil.drawStrokeLine(Color.DARK_GRAY, linesWidth, 
                x, y + height / 2,
                x + width / 2 -  centerHole, y + height / 2);
        RenderUtil.drawStrokeLine(Color.DARK_GRAY, linesWidth, 
                x + width / 2 + centerHole, y + height / 2,
                x + width, y + height / 2);

        profiler.endSection();
    }

    public double getRealCenterLongitude() {
        return realLongitude;
    }

    public void setRealCenterLongitude(double realLongitude) {
        this.realLongitude = realLongitude;
    }

    public double getRealCenterLatitude() {
        return realLatitude;
    }

    public void setRealCenterLatitude(double realLatitude) {
        this.realLatitude = realLatitude;
    }

    @Override
    public MapLayer copy() {
        RenderingDeltaPreviewLayer layer = new RenderingDeltaPreviewLayer(this.getTileScaling(), this.realLongitude, this.realLatitude);
        this.copyPropertiesToOther(layer);
        return layer;
    }

    @Override
    public String name() {
        return "Rendering delta"; //TODO localized
    }

    @Override
    public String description() {
        return "Offset preview"; //TODO localized
    }

}
