package fr.thesmyler.terramap.gui.widgets.map.layer.vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.maps.vector.features.VectorFeature;
import fr.thesmyler.terramap.maps.vector.terracompat.TerraJsonVectorFeature;
import fr.thesmyler.terramap.util.geo.GeoBounds;
import net.buildtheearth.terraplusplus.TerraConfig;
import net.buildtheearth.terraplusplus.dataset.geojson.GeoJsonObject;
import net.buildtheearth.terraplusplus.dataset.geojson.dataset.ParsingGeoJsonDataset;
import net.buildtheearth.terraplusplus.dataset.geojson.dataset.ReferenceResolvingGeoJsonDataset;
import net.buildtheearth.terraplusplus.dataset.geojson.dataset.TiledGeoJsonDataset;
import net.buildtheearth.terraplusplus.dataset.geojson.object.Feature;
import net.buildtheearth.terraplusplus.dataset.geojson.object.FeatureCollection;
import net.buildtheearth.terraplusplus.projection.EquirectangularProjection;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.buildtheearth.terraplusplus.util.CornerBoundingBox2d;

public class TerraOSMLayer extends VectorLayer {

    private static final ParsingGeoJsonDataset RAW_OSM = new ParsingGeoJsonDataset(TerraConfig.openstreetmap.servers);
    private static final TiledGeoJsonDataset TILED_OSM = new TiledGeoJsonDataset(new ReferenceResolvingGeoJsonDataset(RAW_OSM));
    private static final GeographicProjection PROJ = new EquirectangularProjection();

    public TerraOSMLayer(double tileScaling) {
        super(tileScaling);
    }

    @Override
    public Iterable<CompletableFuture<Iterable<VectorFeature>>> getVisibleFeatures(GeoBounds bounds) {
        if(this.getZoom() < 16) return new ArrayList<>();
        GeoBounds[] splitted = bounds.splitAtAntimeridian();
        List<CompletableFuture<Iterable<VectorFeature>>> features = new ArrayList<>();
        for(GeoBounds b: splitted) {
            double[] point00 = b.lowerCorner.asArray();
            double[] point10 = b.lowerCorner.withLongitude(b.upperCorner.longitude).asArray();
            double[] point11 = b.upperCorner.asArray();
            double[] point01 = b.upperCorner.withLongitude(b.lowerCorner.longitude).asArray();
            CornerBoundingBox2d bbox;
            try {
                bbox = new CornerBoundingBox2d(point00, point10, point11, point01, PROJ, true);
            } catch(OutOfProjectionBoundsException e) {
                // TODO Handle exception in TerraOSMLayer::getVisibleFeatures
                TerramapMod.logger.catching(e);
                return new ArrayList<>();
            }
            try {
                CompletableFuture<Iterable<VectorFeature>> future = TILED_OSM.getAsync(bbox).thenApplyAsync(tiles -> {
                    List<VectorFeature> tileFeatures = new ArrayList<>();
                    for(GeoJsonObject[] objects: tiles) {
                        for(GeoJsonObject object: objects) {
                            if(object instanceof Feature) {
                                VectorFeature feature = TerraJsonVectorFeature.convert((Feature)object);
                                if(feature != null) tileFeatures.add(feature);
                            } else if(object instanceof FeatureCollection) {
                                for(Feature feat: (FeatureCollection) object) {
                                    VectorFeature feature = TerraJsonVectorFeature.convert(feat);
                                    if(feature != null) tileFeatures.add(feature);
                                }
                            }
                        }
                    }
                    return (Iterable<VectorFeature>) tileFeatures;
                });
                features.add(future);
            } catch(OutOfProjectionBoundsException silenced) {} // Cannot happen with this projection
        }
        return features;
    }

    @Override
    public String getId() {
        return "terraosm";
    }

    @Override
    public MapLayer copy() {
        TerraOSMLayer other = new TerraOSMLayer(this.getTileScaling());
        this.copyPropertiesToOther(other);
        return other;
    }

    @Override
    public String name() {
        return "Terra++ OSM"; //TODO Localize
    }

    @Override
    public String description() {
        return ""; //TODO Terra OSM layer description
    }

}
