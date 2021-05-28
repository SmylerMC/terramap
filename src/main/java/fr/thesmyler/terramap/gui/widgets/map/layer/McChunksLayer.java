package fr.thesmyler.terramap.gui.widgets.map.layer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.util.Vec2d;
import fr.thesmyler.terramap.util.WebMercatorUtil;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.minecraft.client.renderer.GlStateManager;

public class McChunksLayer extends MapLayer {
    
    private ProjectionCache cache2dr = new ProjectionCache();
    private ProjectionCache cache3dr = new ProjectionCache();
    private ProjectionCache cacheChunks = new ProjectionCache();

    public McChunksLayer(double tileScaling) {
        super(tileScaling);
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        MapWidget map = (MapWidget)parent;
        GeographicProjection projection = TerramapClientContext.getContext().getProjection();
        if(projection == null) return;
        map.getProfiler().startSection("layer-" + this.getId());
        
        this.cache2dr.projection = projection;
        this.cache3dr.projection = projection;
        this.cacheChunks.projection = projection;

        double extendedWidth = this.getExtendedWidth();
        double extendedHeight = this.getExtendedHeight();        

        //TODO Fade away instead
        boolean render2dr = false;
        boolean render3dr = false;
        boolean renderChunks = false;
        double renderThreshold = 128d;

        Vec2d centerMc;
        try {
            centerMc = new Vec2d(projection.fromGeo(this.getCenterLongitude(), this.getCenterLatitude()));
            Vec2d pos2 = new Vec2d(projection.fromGeo(this.getRenderLongitude(extendedWidth / 2 + 10), this.getRenderLatitude(extendedHeight / 2 + 10)));
            double d = centerMc.distanceTo(pos2);
            if(d < renderThreshold) render2dr = true;
            if(d < renderThreshold / 2) render3dr = true;
            if(d < renderThreshold / 16) renderChunks = true;
        } catch(OutOfProjectionBoundsException silenced) {
            // The center is out of bounds, let's not render anything
            return;
        }

        GlStateManager.pushMatrix();
        this.applyRotationGl(x, y);

        if(renderChunks) {
            this.renderGrid(x, y, this.cache2dr, centerMc, 512, extendedWidth, extendedHeight, Color.DARK_GRAY, 3f);
            this.renderGrid(x, y, this.cache3dr, centerMc, 256, extendedWidth, extendedHeight, Color.DARK_GRAY, 2f);
            this.renderGrid(x, y, this.cacheChunks, centerMc, 16, extendedWidth, extendedHeight, Color.DARK_GRAY, 1f);
        } else if(render3dr) {
            this.renderGrid(x, y, this.cache2dr, centerMc, 512, extendedWidth, extendedHeight, Color.DARK_GRAY, 2f);
            this.renderGrid(x, y, this.cache3dr, centerMc, 256, extendedWidth, extendedHeight, Color.DARK_GRAY, 1f);
        } else if(render2dr) {
            this.renderGrid(x, y, this.cache2dr, centerMc, 512, extendedWidth, extendedHeight, Color.DARK_GRAY, 1f);
        }

        this.cache2dr.cycle();
        this.cache3dr.cycle();
        this.cacheChunks.cycle();
        GlStateManager.popMatrix();
        map.getProfiler().endSection();
    }
    
    private void renderGrid(float x, float y, ProjectionCache cache, Vec2d mcCenter, long tileSize, double extendedWidth, double extendedHeight, Color color, float lineWidth) {
        
        int maxTiles = 50; // Maximum draw iterations, for safety
        
        Vec2d centerTile = new Vec2d(Math.floorDiv((long)Math.floor(mcCenter.x), tileSize), Math.floorDiv((long)Math.floor(mcCenter.y), tileSize));
        int dX = 0;
        int dY = 0;
        Vec2d[] corners = {
                new Vec2d(centerTile.x * tileSize, centerTile.y * tileSize),
                new Vec2d(centerTile.x * tileSize, (centerTile.y + 1) * tileSize),
                new Vec2d((centerTile.x + 1) * tileSize, (centerTile.y + 1) * tileSize),
                new Vec2d((centerTile.x + 1) * tileSize, centerTile.y * tileSize)
        };
        int direction = 1;
        int size = 1;
        int safety = 0;
        boolean inTop, inBottom, inLeft, inRight;
        inTop = inBottom = inLeft = inRight = true;
        while((inTop || inBottom || inRight || inLeft) && safety++ < maxTiles) {
            
            boolean[] linesInlineIn = new boolean[4];
            while(2*dX*direction < size) {
                this.renderTile(x, y, cache, corners, color, lineWidth, linesInlineIn, extendedWidth, extendedHeight);
                dX += direction;
                long step = tileSize*direction;
                for(int i=0; i<corners.length; i++) corners[i] = corners[i].add(step, 0);
            }
            
            if(!linesInlineIn[0]) inLeft = false;
            if(!linesInlineIn[1]) inRight = false;
            if(!linesInlineIn[2]) inTop = false;
            if(!linesInlineIn[3]) inBottom = false;
            linesInlineIn = new boolean[4];

            while(2*dY*direction < size) {
                this.renderTile(x, y, cache, corners, color, lineWidth, linesInlineIn, extendedWidth, extendedHeight);
                dY += direction;
                long step = tileSize*direction;
                for(int i=0; i<corners.length; i++) corners[i] = corners[i].add(0, step);
            }
            
            if(!linesInlineIn[0]) inLeft = false;
            if(!linesInlineIn[1]) inRight = false;
            if(!linesInlineIn[2]) inTop = false;
            if(!linesInlineIn[3]) inBottom = false;

            direction *= -1;
            size++;
        }
    }

    private void renderTile(float x, float y, ProjectionCache cache, Vec2d[] corners, Color color, float lineWidth, boolean[] loopingConditions, double extendedWidth, double extendedHeight) {
        Vec2d[] renderCorners = new Vec2d[4];
        try {
            for(int i=0; i<renderCorners.length; i++) {
                renderCorners[i] = cache.getRenderPos(corners[i]);
            }
        } catch(OutOfProjectionBoundsException silenced) {
            return; // Skip the tile
        }
        for(Vec2d corner: renderCorners) {
            loopingConditions[0] = loopingConditions[0] || corner.x >= 0;
            loopingConditions[1] = loopingConditions[1] || corner.x <= extendedWidth;
            loopingConditions[2] = loopingConditions[2] || corner.y >= 0;
            loopingConditions[3] = loopingConditions[3] || corner.y <= extendedHeight;
        }
        RenderUtil.drawClosedStrokeLine(color, lineWidth,
                x + renderCorners[0].x, y + renderCorners[0].y,
                x + renderCorners[1].x, y + renderCorners[1].y,
                x + renderCorners[2].x, y + renderCorners[2].y,
                x + renderCorners[3].x, y + renderCorners[3].y
        );
    }
    
    @Override
    public String getId() {
        return "mcchunks";
    }
    
    private class ProjectionCache {
        
        GeographicProjection projection;
        
        Map<Vec2d, double[]> mcToGeo = new HashMap<>();
        Set<Vec2d> accessedInCycle = new HashSet<>();
        
        int maxProjectionsPerCycle = 20;
        int projectionsThisCycle = 0;
        
        Vec2d getRenderPos(Vec2d mcPos) throws OutOfProjectionBoundsException {
            this.accessedInCycle.add(mcPos);
            
            // Not really out of bounds but we don't need to differentiate the two
            if(this.projectionsThisCycle >= this.maxProjectionsPerCycle) throw OutOfProjectionBoundsException.get();
            
            double[] lola;
            
            // Try getting a cached value
            if(this.mcToGeo.containsKey(mcPos)) {
                lola = this.mcToGeo.get(mcPos);
                if(lola == null) throw OutOfProjectionBoundsException.get();
            } else {
                // Fallback to computing it
                try {
                    this.projectionsThisCycle++;
                    lola = this.projection.toGeo(mcPos.x, mcPos.y);
                    this.mcToGeo.put(mcPos, lola);
                } catch(OutOfProjectionBoundsException e) {
                    this.mcToGeo.put(mcPos, null);
                    throw e;
                }
            }
            
            if(Math.abs(lola[1]) > WebMercatorUtil.LIMIT_LATITUDE) throw OutOfProjectionBoundsException.get();
            return new Vec2d(McChunksLayer.this.getRenderX(lola[0]), McChunksLayer.this.getRenderY(lola[1]));
        }
        
        void cycle() {
            this.mcToGeo.keySet().retainAll(this.accessedInCycle);
            this.accessedInCycle.clear();
            this.projectionsThisCycle = 0;
        }
        
        
    }

}
