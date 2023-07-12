package fr.thesmyler.terramap.gui.widgets.map.layer;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.util.geo.GeoPointMutable;
import fr.thesmyler.terramap.util.geo.WebMercatorUtil;
import fr.thesmyler.terramap.util.math.Vec2dMutable;
import fr.thesmyler.terramap.util.math.Vec2dReadOnly;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.minecraft.client.renderer.GlStateManager;

/**
 * Shows the area and angular distortion from the world's {@link GeographicProjection}.
 * <p>
 * A very interesting way to make a computer suffer.
 * 
 * @author SmylerMC
 *
 */
public class DistortionLayer extends MapLayer {

    private final Vec2dMutable screenPositions = new Vec2dMutable();
    private final GeoPointMutable renderedLocations = new GeoPointMutable();
    private Vec2dReadOnly renderSpaceDimensions;

    protected void initialize() {
        this.renderSpaceDimensions = this.getRenderSpaceDimensions();
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        MapWidget map = (MapWidget) parent;
        GeographicProjection projection = TerramapClientContext.getContext().getProjection();
        if(projection == null) return;
        map.getProfiler().startSection("layer-distortion");
        GlStateManager.pushMatrix();
        this.applyRotationGl(x, y);

        double maxX = this.renderSpaceDimensions.x();
        double maxY = this.renderSpaceDimensions.y();
        double res = 20d;
        for(double dx = -1; dx < maxX; dx += res) {
            for(double dy = -1; dy < maxY; dy += res) {
                this.getLocationAtPositionInRenderSpace(this.renderedLocations, this.screenPositions.set(dx + res / 2, dy + res / 2));
                if(!WebMercatorUtil.PROJECTION_BOUNDS.contains(this.renderedLocations)) continue;
                Color color = Color.TRANSPARENT;
                try {
                    double[] distortion = projection.tissot(this.renderedLocations.longitude(), this.renderedLocations.latitude());
                    float red = (float) Math.min(distortion[0] / 4f, 1f);
                    float green = (float) Math.min(distortion[1] / 2/Math.PI, 1f);
                    float alpha = Math.min(red + green, 1f);
                    color = new Color(red, green, 0f, alpha);
                } catch(OutOfProjectionBoundsException e) {
                    color = color.withRed(0f).withGreen(0).withAlpha(.1f);
                }
                RenderUtil.drawRect(x + dx, y + dy, x + dx + res, y + dy + res, color);
            }
        }
        
        GlStateManager.popMatrix();
        map.getProfiler().endSection();
    }

    @Override
    public String name() {
        return SmyLibGui.getTranslator().format("terramap.mapwidget.layers.distortion.name");
    }

    @Override
    public String description() {
        return SmyLibGui.getTranslator().format("terramap.mapwidget.layers.distortion.desc");
    }

}
