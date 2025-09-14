package fr.thesmyler.terramap.gui.widgets.map.layer;

import net.smyler.smylib.gui.UiDrawContext;
import net.smyler.smylib.gui.containers.FlexibleWidgetContainer;
import net.smyler.smylib.gui.containers.WidgetContainer;
import net.smyler.smylib.Color;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import net.smyler.terramap.util.geo.*;
import net.smyler.smylib.math.Vec2dMutable;
import net.smyler.smylib.math.Vec2dReadOnly;

import static net.smyler.smylib.SmyLib.getGameClient;

/**
 * Shows the area and angular distortion from the world's {@link GeoProjection}.
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
    private final TissotsIndicatrix tissot = new TissotsIndicatrix();

    protected void initialize() {
        this.renderSpaceDimensions = this.getRenderSpaceDimensions();
    }

    @Override
    public void draw(UiDrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        MapWidget map = (MapWidget) parent;
        GeoProjection projection = TerramapClientContext.getContext().getProjection();
        if(projection == null) return;
        map.getProfiler().startSection("layer-distortion");
        context.gl().pushViewMatrix();
        this.applyRotationGl(context, x, y);

        double maxX = this.renderSpaceDimensions.x();
        double maxY = this.renderSpaceDimensions.y();
        double res = 20d;
        for(double dx = -1; dx < maxX; dx += res) {
            for(double dy = -1; dy < maxY; dy += res) {
                this.getLocationAtPositionInRenderSpace(this.renderedLocations, this.screenPositions.set(dx + res / 2, dy + res / 2));
                if(!WebMercatorUtil.PROJECTION_BOUNDS.contains(this.renderedLocations)) continue;
                Color color = Color.TRANSPARENT;
                try {
                    projection.tissot(this.tissot, this.renderedLocations);
                    float red = (float) Math.min(this.tissot.areaInflation() / 4f, 1f);
                    float green = (float) Math.min(this.tissot.maxAngularDistortion() / 2/Math.PI, 1f);
                    float alpha = Math.min(red + green, 1f);
                    color = new Color(red, green, 0f, alpha);
                } catch(OutOfGeoBoundsException e) {
                    color = color.withRed(0f).withGreen(0).withAlpha(.1f);
                }
                context.drawRectangle(x + dx, y + dy, x + dx + res, y + dy + res, color);
            }
        }

        context.gl().popViewMatrix();
        map.getProfiler().endSection();
    }

    @Override
    public String name() {
        return getGameClient().translator().format("terramap.mapwidget.layers.distortion.name");
    }

    @Override
    public String description() {
        return getGameClient().translator().format("terramap.mapwidget.layers.distortion.desc");
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public FlexibleWidgetContainer createConfigurationContainer() {
        return null;
    }

}
