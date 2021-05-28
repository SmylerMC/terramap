package fr.thesmyler.terramap.gui.widgets.map.layer;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.util.GeoUtil;
import fr.thesmyler.terramap.util.WebMercatorUtil;
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

    public DistortionLayer(double tileScaling) {
        super(tileScaling);
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        MapWidget map = (MapWidget) parent;
        GeographicProjection projection = TerramapClientContext.getContext().getProjection();
        if(projection == null) return;
        map.getProfiler().startSection("layer-" + this.getId());
        GlStateManager.pushMatrix();
        this.applyRotationGl(x, y);
        
        double res = 2d;
        for(double dx = -1; dx < this.getExtendedWidth(); dx += res) {
            double lon = GeoUtil.getLongitudeInRange(this.getRenderLongitude(dx + res / 2));
            for(double dy = -1; dy < this.getExtendedHeight(); dy += res) {
                double lat = this.getRenderLatitude(dy + res / 2);
                if(Math.abs(lat) > WebMercatorUtil.LIMIT_LATITUDE) continue;
                Color color = Color.TRANSPARENT;
                try {
                    double[] distortion = projection.tissot(lon, lat);
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
    public String getId() {
        return "distortion";
    }

}
