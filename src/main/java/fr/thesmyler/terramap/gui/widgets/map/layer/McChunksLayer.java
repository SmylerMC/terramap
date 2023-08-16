package fr.thesmyler.terramap.gui.widgets.map.layer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.screen.PopupScreen;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.util.geo.GeoPointImmutable;
import fr.thesmyler.terramap.util.geo.GeoPointMutable;
import fr.thesmyler.terramap.util.geo.GeoPointReadOnly;
import fr.thesmyler.terramap.util.geo.WebMercatorUtil;
import fr.thesmyler.terramap.util.math.Vec2d;
import fr.thesmyler.terramap.util.math.Vec2dMutable;
import fr.thesmyler.terramap.util.math.Vec2dReadOnly;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.minecraft.client.renderer.GlStateManager;

import static java.lang.Math.floor;
import static java.lang.Math.floorDiv;

/**
 * Renders Minecraft region (both 2dr and 3dr), chunks, and blocks outlines onto a map widget.
 * 
 * @author Smyler
 *
 */
public class McChunksLayer extends MapLayer {
    
    public static final String ID = "mcchunks";
    
    private final ProjectionCache cache = new ProjectionCache(4);
    private final Vec2dMutable mcCenter = new Vec2dMutable();
    private Vec2dReadOnly extendedDimensions;
    private GeoPointReadOnly geoCenter;

    private Color color = Color.DARK_GRAY;

    // Used for calculations. Those aren't local fields so we don't create hundreds of objects every time we render
    private final Vec2dMutable[] corners = {
            new Vec2dMutable(),
            new Vec2dMutable(),
            new Vec2dMutable(),
            new Vec2dMutable()
    };
    private final Vec2dMutable[] projectedCorners = {
            new Vec2dMutable(),
            new Vec2dMutable(),
            new Vec2dMutable(),
            new Vec2dMutable()
    };
    private final Vec2dMutable centerTile = new Vec2dMutable();
    private final Vec2dMutable deltaCalculator = new Vec2dMutable();
    private final GeoPointMutable nearCenterLocation = new GeoPointMutable();

    @Override
    protected void initialize() {
        this.extendedDimensions = this.getRenderSpaceDimensions();
        this.geoCenter = this.getMap().getController().getCenterLocation();
        this.setAlpha(0.25f);
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        MapWidget map = (MapWidget)parent;
        GeographicProjection projection = TerramapClientContext.getContext().getProjection();
        if(projection == null) return;
        map.getProfiler().startSection("layer-" + ID);
        
        this.cache.projection = projection;

        boolean render2dr = false;
        boolean render3dr = false;
        boolean renderChunks = false;
        boolean renderBlocks = false;
        double renderThreshold = 128d;

        // First decide on what we are going to render depending on the scale at the center of the map
        try {
            this.deltaCalculator.set(this.extendedDimensions).downscale(2d).add(10d, 10d);
            this.getLocationAtPositionInRenderSpace(this.nearCenterLocation, this.deltaCalculator);
            this.mcCenter.set(projection.fromGeo(this.geoCenter.longitude(), this.geoCenter.latitude()));
            this.deltaCalculator.set(projection.fromGeo(this.nearCenterLocation.longitude(), this.nearCenterLocation.latitude()));
            double d = this.mcCenter.distanceTo(this.deltaCalculator);
            if(d < renderThreshold) render2dr = true;
            if(d < renderThreshold / 2) render3dr = true;
            if(d < renderThreshold / 16) renderChunks = true;
            if(d < renderThreshold / 128) renderBlocks = true;
        } catch(OutOfProjectionBoundsException silenced) {
            // The center is out of bounds, let's not render anything
            return;
        }

        GlStateManager.pushMatrix();
        this.applyRotationGl(x, y);
        
        Color c = this.color.withAlpha(this.getAlpha());

        if(renderBlocks) {
            this.renderGrid(x, y, 0, 1, c, 1f);
            this.renderGrid(x, y, 1, 16, c, 2f);
            this.renderGrid(x, y, 2, 256, c, 3f);
            this.renderGrid(x, y, 3, 512, c, 4f);
        } else if(renderChunks) {
            this.renderGrid(x, y, 1, 16, c, 1f);
            this.renderGrid(x, y, 2, 256, c, 2f);
            this.renderGrid(x, y, 3, 512, c, 3f);
        } else if(render3dr) {
            this.renderGrid(x, y, 2, 256, c, 1f);
            this.renderGrid(x, y, 3, 512, c, 2f);
        } else if(render2dr) {
            this.renderGrid(x, y, 3, 512, c, 1f);
        }

        this.cache.cycle();
        GlStateManager.popMatrix();
        map.getProfiler().endSection();
    }
    
    private void renderGrid(float x, float y, int discriminator, long tileSize, Color color, float lineWidth) {
        
        final int maxTiles = 100; // Maximum drawing iterations, for safety

        this.centerTile.set(floorDiv((long) floor(this.mcCenter.x), tileSize), floorDiv((long)floor(this.mcCenter.y), tileSize));
        int dX = 0;
        int dY = 0;
        this.corners[0].set(this.centerTile).scale(tileSize);
        this.corners[1].set(this.centerTile).add(0, 1).scale(tileSize);
        this.corners[2].set(this.centerTile).add(1, 1).scale(tileSize);
        this.corners[3].set(this.centerTile).add(1, 0).scale(tileSize);
        int direction = 1;
        int size = 1;
        int safety = 0;
        boolean inTop, inBottom, inLeft, inRight;
        inTop = inBottom = inLeft = inRight = true;
        
        // Spiral out from the center tile until we aren't rendering anything onto the screen
        while((inTop || inBottom || inRight || inLeft) && safety++ < maxTiles) {
            
            boolean[] linesInlineIn = new boolean[4];
            while(2*dX*direction < size) {
                if((direction < 0 && inBottom) || (direction > 0 && inTop))
                    this.renderTile(x, y, discriminator, color, lineWidth, linesInlineIn);
                dX += direction;
                long step = tileSize*direction;
                for (Vec2dMutable corner : this.corners) corner.add(step, 0);
            }
            
            if(!linesInlineIn[0]) inLeft = false;
            if(!linesInlineIn[1]) inRight = false;
            if(!linesInlineIn[2]) inTop = false;
            if(!linesInlineIn[3]) inBottom = false;
            linesInlineIn = new boolean[4];

            while(2*dY*direction < size) {
                if((direction < 0 && inLeft) || (direction > 0 && inRight))
                    this.renderTile(x, y, discriminator, color, lineWidth, linesInlineIn);
                dY += direction;
                long step = tileSize*direction;
                for (Vec2dMutable corner : this.corners) corner.add(0, step);
            }
            
            if(!linesInlineIn[0]) inLeft = false;
            if(!linesInlineIn[1]) inRight = false;
            if(!linesInlineIn[2]) inTop = false;
            if(!linesInlineIn[3]) inBottom = false;

            direction *= -1;
            size++;
        }
    }

    private void renderTile(float x, float y, int discriminator, Color color, float lineWidth, boolean[] loopingConditions) {
        try {
            for(int i=0; i<this.projectedCorners.length; i++) {
                this.cache.getRenderPos(this.projectedCorners[i], this.corners[i], discriminator);
            }
        } catch(OutOfProjectionBoundsException silenced) {
            return; // Skip the tile
        }
        for(Vec2dMutable corner: this.projectedCorners) {
            loopingConditions[0] = loopingConditions[0] || corner.x >= 0;
            loopingConditions[1] = loopingConditions[1] || corner.x <= this.extendedDimensions.x();
            loopingConditions[2] = loopingConditions[2] || corner.y >= 0;
            loopingConditions[3] = loopingConditions[3] || corner.y <= this.extendedDimensions.y();
        }
        RenderUtil.drawClosedStrokeLine(color, lineWidth,
                x + this.projectedCorners[0].x, y + this.projectedCorners[0].y,
                x + this.projectedCorners[1].x, y + this.projectedCorners[1].y,
                x + this.projectedCorners[2].x, y + this.projectedCorners[2].y,
                x + this.projectedCorners[3].x, y + this.projectedCorners[3].y
        );
    }
    
    private class ProjectionCache {
        
        GeographicProjection projection;
        
        final Map<Vec2d<?>, GeoPointImmutable> mcToGeo = new HashMap<>();
        final Set<Vec2d<?>> accessedInCycle = new HashSet<>();
        
        final int maxProjectionsPerCycle = 50;
        int[] projectionsThisCycle;
        
        ProjectionCache(int diffCount) {
            this.projectionsThisCycle = new int[diffCount];
        }
        
        void getRenderPos(Vec2dMutable destination, Vec2d<?> mcPos, int discriminator) throws OutOfProjectionBoundsException {
            if (!this.accessedInCycle.contains(mcPos)) this.accessedInCycle.add(mcPos.getImmutable());
            
            // Not really out of bounds, but we don't need to differentiate the two
            if(this.projectionsThisCycle[discriminator] >= this.maxProjectionsPerCycle) throw OutOfProjectionBoundsException.get();
            
            GeoPointImmutable location;
            
            // Try getting a cached value
            if(this.mcToGeo.containsKey(mcPos)) {
                location = this.mcToGeo.get(mcPos);
                if(location == null) throw OutOfProjectionBoundsException.get();
            } else {
                // Fallback to computing it
                try {
                    this.projectionsThisCycle[discriminator]++;
                    location = new GeoPointImmutable(this.projection.toGeo(mcPos.x(), mcPos.y()));
                    this.mcToGeo.put(mcPos.getImmutable(), location);
                } catch(OutOfProjectionBoundsException e) {
                    this.mcToGeo.put(mcPos.getImmutable(), null);
                    throw e;
                }
            }
            
            if(! WebMercatorUtil.PROJECTION_BOUNDS.contains(location)) throw OutOfProjectionBoundsException.get();
            McChunksLayer.this.getLocationPositionInRenderSpace(destination, location);
        }
        
        void cycle() {
            this.mcToGeo.keySet().retainAll(this.accessedInCycle);
            this.accessedInCycle.clear();
            this.projectionsThisCycle = new int[this.projectionsThisCycle.length];
        }
        
        
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String name() {
        return SmyLibGui.getTranslator().format("terramap.mapwidget.layers.mcchunks.name");
    }

    @Override
    public String description() {
        return SmyLibGui.getTranslator().format("terramap.mapwidget.layers.mcchunks.desc");
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public PopupScreen createConfigurationScreen() {
        return null;
    }

}
