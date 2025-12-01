package fr.thesmyler.terramap.gui.widgets.map.layer;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.FlexibleWidgetContainer;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.util.geo.GeoPointMutable;
import fr.thesmyler.terramap.util.geo.WebMercatorUtil;
import fr.thesmyler.terramap.util.math.Vec2dMutable;
import net.buildtheearth.terraplusplus.dataset.scalar.MultiScalarDataset;
import net.buildtheearth.terraplusplus.util.bvh.Bounds2d;

import java.util.List;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static net.buildtheearth.terraplusplus.generator.EarthGeneratorPipelines.KEY_DATASET_HEIGHTS;

public class TerraPlusPlusDtsmsLayer extends MapLayer {

    private final HackedMultiScalarDataset heightDataset = new HackedMultiScalarDataset(KEY_DATASET_HEIGHTS);

    private final Vec2dMutable screenPositionCorner1 = new Vec2dMutable();
    private final Vec2dMutable screenPositionCorner2 = new Vec2dMutable();
    private final Vec2dMutable screenPositionCorner3 = new Vec2dMutable();
    private final Vec2dMutable screenPositionCorner4 = new Vec2dMutable();

    private final GeoPointMutable renderedLocations = new GeoPointMutable();

    @Override
    public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        MapWidget map = (MapWidget) parent;
        map.getProfiler().startSection("layer-terrapp-dtsms");

        float renderAlpha = this.getAlpha();

        for (Dataset dataset: this.heightDataset.getDataset(Bounds2d.of(-180, 180, -WebMercatorUtil.LIMIT_LATITUDE, WebMercatorUtil.LIMIT_LATITUDE))) {
            Bounds2d bounds = dataset.bounds;
            this.getPositionOnWidget(this.screenPositionCorner1, this.renderedLocations.set(bounds.minX(), bounds.minZ()));
            this.getPositionOnWidget(this.screenPositionCorner2, this.renderedLocations.set(bounds.maxX(), bounds.minZ()));
            this.getPositionOnWidget(this.screenPositionCorner3, this.renderedLocations.set(bounds.maxX(), bounds.maxZ()));
            this.getPositionOnWidget(this.screenPositionCorner4, this.renderedLocations.set(bounds.minX(), bounds.maxZ()));
            RenderUtil.drawClosedStrokeLine(
                    dataset.color.withAlpha(renderAlpha), 1f,
                    x + this.screenPositionCorner1.x, y + this.screenPositionCorner1.y,
                    x + this.screenPositionCorner2.x, y + this.screenPositionCorner2.y,
                    x + this.screenPositionCorner3.x, y + this.screenPositionCorner3.y,
                    x + this.screenPositionCorner4.x, y + this.screenPositionCorner4.y
            );
        }

        map.getProfiler().endSection();
    }

    @Override
    protected void initialize() {

    }

    @Override
    public String name() {
        return SmyLibGui.getTranslator().format("terramap.mapwidget.layers.tpp_dtsms.name");
    }

    @Override
    public String description() {
        return SmyLibGui.getTranslator().format("terramap.mapwidget.layers.tpp_dtsms.desc");
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public FlexibleWidgetContainer createConfigurationContainer() {
        return null;
    }

    /**
     * This has the sole purpose of getting access to protected fields.
     */
    private static class HackedMultiScalarDataset extends MultiScalarDataset {

        public HackedMultiScalarDataset(String name) {
            super(name, true);
        }

        public List<Dataset> getDataset(Bounds2d bounds) {
            // Do some gymnastics because MultiScalarDataset#WrappedDataset is private
            Bounds2d[] datasetAsBounds = this.bvh.getAllIntersecting(bounds).toArray(new Bounds2d[0]);
            return stream(datasetAsBounds)
                    .map(Dataset::new)
                    .collect(toList());
        }

    }

    static class Dataset {
        private final Bounds2d bounds;
        private final Color color;

        Dataset(Bounds2d bounds) {
            this.bounds = bounds;
            this.color = new Color(0xFF000000 | bounds.hashCode());
        }
    }

}
