package fr.thesmyler.terramap.gui.widgets.map.layer;

import com.google.gson.JsonObject;
import fr.thesmyler.smylibgui.container.FlexibleWidgetContainer;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import net.smyler.terramap.util.geo.GeoPoint;
import net.smyler.terramap.util.geo.GeoPointMutable;
import net.smyler.smylib.math.Vec2dMutable;
import net.smyler.smylib.math.Vec2dReadOnly;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.profiler.Profiler;

public class RenderingDeltaPreviewLayer extends MapLayer {

    private final GeoPointMutable realCenter = new GeoPointMutable();
    private final Vec2dMutable realCenterPosition = new Vec2dMutable();
    private Vec2dReadOnly renderSpaceHalfDimensions;

    public void setRealCenter(GeoPoint<?> realCenter) {
        this.realCenter.set(realCenter);
    }

    @Override
    protected void initialize() {
        this.renderSpaceHalfDimensions = this.getRenderSpaceHalfDimensions();
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {

        MapWidget parentMap = (MapWidget) parent;
        Profiler profiler = parentMap.getProfiler();
        profiler.startSection("render-delta-preview-layer");

        float width = this.getWidth();
        float height = this.getHeight();
        this.getLocationPositionInRenderSpace(this.realCenterPosition, this.realCenter);

        GlStateManager.pushMatrix();
        this.applyRotationGl(x, y);
        RenderUtil.drawStrokeLine(Color.RED, 2f,
                this.renderSpaceHalfDimensions.x(), this.renderSpaceHalfDimensions.y(),
                this.realCenterPosition.x, this.renderSpaceHalfDimensions.y(),
                this.realCenterPosition.x, this.realCenterPosition.y);
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

    @Override
    public JsonObject saveSettings() {
        JsonObject json = new JsonObject();
        JsonObject geoPoint = new JsonObject();
        geoPoint.addProperty("latitude", this.realCenter.latitude());
        geoPoint.addProperty("longitude", this.realCenter.longitude());
        json.add("realCenter", geoPoint);
        return json;
    }

    @Override
    public void loadSettings(JsonObject json) {
        try {
            JsonObject geoPoint = json.getAsJsonObject("realCenter");
            double latitude = geoPoint.get("latitude").getAsDouble();
            double longitude = geoPoint.get("longitude").getAsDouble();
            this.realCenter.set(longitude, latitude);
        } catch (IllegalStateException | NullPointerException ignored) {
            // Things were not in the expected format, let's abort there.
        }
    }

    @Override
    public String name() {
        return ""; // Never shown
    }

    @Override
    public String description() {
        return ""; // Never shown
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
