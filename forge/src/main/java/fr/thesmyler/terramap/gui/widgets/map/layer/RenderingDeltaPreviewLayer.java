package fr.thesmyler.terramap.gui.widgets.map.layer;

import com.google.gson.JsonObject;
import net.smyler.smylib.gui.containers.FlexibleWidgetContainer;
import net.smyler.smylib.gui.containers.WidgetContainer;
import net.smyler.smylib.Color;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import net.smyler.smylib.gui.UiDrawContext;
import net.smyler.terramap.util.geo.GeoPoint;
import net.smyler.terramap.util.geo.GeoPointMutable;
import net.smyler.smylib.math.Vec2dMutable;
import net.smyler.smylib.math.Vec2dReadOnly;
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
    public void draw(UiDrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {

        MapWidget parentMap = (MapWidget) parent;
        Profiler profiler = parentMap.getProfiler();
        profiler.startSection("render-delta-preview-layer");

        float width = this.getWidth();
        float height = this.getHeight();
        this.getLocationPositionInRenderSpace(this.realCenterPosition, this.realCenter);

        context.gl().pushViewMatrix();
        this.applyRotationGl(context, x, y);
        context.drawStrokeLine(Color.RED, 2f,
                this.renderSpaceHalfDimensions.x(), this.renderSpaceHalfDimensions.y(),
                this.realCenterPosition.x, this.renderSpaceHalfDimensions.y(),
                this.realCenterPosition.x, this.realCenterPosition.y);
        context.gl().popViewMatrix();

        float centerHole = 10;
        float linesWidth = 1f;
        context.drawStrokeLine(Color.DARK_GRAY, linesWidth,
                x + width / 2, y,
                x + width / 2, y + height / 2 - centerHole);
        context.drawStrokeLine(Color.DARK_GRAY, linesWidth,
                x + width / 2, y + height / 2 + centerHole,
                x + width / 2, y + height);
        context.drawStrokeLine(Color.DARK_GRAY, linesWidth,
                x, y + height / 2,
                x + width / 2 -  centerHole, y + height / 2);
        context.drawStrokeLine(Color.DARK_GRAY, linesWidth,
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
