package fr.thesmyler.terramap.gui.widgets.map.layer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.FeatureVisibilityController;
import fr.thesmyler.terramap.util.Vec2d;
import fr.thesmyler.terramap.util.geo.GeoPoint;
import fr.thesmyler.terramap.util.geo.WebMercatorUtil;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

/**
 * Renders Minecraft region (both 2dr and 3dr), chunks, and blocks outlines onto a map widget.
 * 
 * @author SmylerMC
 *
 */
public class McChunksLayer extends MapLayer implements FeatureVisibilityController {
    
    public static final String ID = "mcchunks";
    
    private ProjectionCache cache = new ProjectionCache(4);
    
    private ToggleButtonWidget button;
    
    private boolean visible = false;
    
    private Color color = Color.DARK_GRAY;

    public McChunksLayer(double tileScaling) {
        super(tileScaling);
        this.setAlpha(0.25f);
        this.button = new ToggleButtonWidget(10, 14, 14,
                186, 108, 186, 122,
                186, 108, 186, 122,
                186, 136, 186, 150,
                this.visible,
                b -> this.visible = b
                );
        this.button.setTooltip(I18n.format("terramap.mapwidget.mcchunks.tooltip"));
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        MapWidget map = (MapWidget)parent;
        GeographicProjection projection = TerramapClientContext.getContext().getProjection();
        if(projection == null) return;
        map.getProfiler().startSection("layer-" + this.getId());
        
        this.cache.projection = projection;

        double extendedWidth = this.getExtendedWidth();
        double extendedHeight = this.getExtendedHeight();        

        boolean render2dr = false;
        boolean render3dr = false;
        boolean renderChunks = false;
        boolean renderBlocks = false;
        double renderThreshold = 128d;

        // First decide on what we are going to render depending on the scale at the center of the map
        Vec2d centerMc;
        try {
            GeoPoint centerLocation = this.getCenterLocation();
            centerMc = new Vec2d(projection.fromGeo(centerLocation.longitude, centerLocation.latitude));
            GeoPoint nearCenterLocation = this.getRenderLocation(new Vec2d(extendedWidth / 2 + 10, extendedHeight / 2 + 10));
            Vec2d pos2 = new Vec2d(projection.fromGeo(nearCenterLocation.longitude, nearCenterLocation.latitude));
            double d = centerMc.distanceTo(pos2);
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
            this.renderGrid(x, y, 0, centerMc, 1, extendedWidth, extendedHeight, c, 1f);
            this.renderGrid(x, y, 1, centerMc, 16, extendedWidth, extendedHeight, c, 2f);
            this.renderGrid(x, y, 2, centerMc, 256, extendedWidth, extendedHeight, c, 3f);
            this.renderGrid(x, y, 3, centerMc, 512, extendedWidth, extendedHeight, c, 4f);
        } else if(renderChunks) {
            this.renderGrid(x, y, 1, centerMc, 16, extendedWidth, extendedHeight, c, 1f);
            this.renderGrid(x, y, 2, centerMc, 256, extendedWidth, extendedHeight, c, 2f);
            this.renderGrid(x, y, 3, centerMc, 512, extendedWidth, extendedHeight, c, 3f);
        } else if(render3dr) {
            this.renderGrid(x, y, 2, centerMc, 256, extendedWidth, extendedHeight, c, 1f);
            this.renderGrid(x, y, 3, centerMc, 512, extendedWidth, extendedHeight, c, 2f);
        } else if(render2dr) {
            this.renderGrid(x, y, 3, centerMc, 512, extendedWidth, extendedHeight, c, 1f);
        }

        this.cache.cycle();
        GlStateManager.popMatrix();
        map.getProfiler().endSection();
    }
    
    private void renderGrid(float x, float y, int discriminator, Vec2d mcCenter, long tileSize, double extendedWidth, double extendedHeight, Color color, float lineWidth) {
        
        int maxTiles = 100; // Maximum drawing iterations, for safety
        
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
        
        // Spirale out from the center tile until we aren't rendering anything onto the screen
        while((inTop || inBottom || inRight || inLeft) && safety++ < maxTiles) {
            
            boolean[] linesInlineIn = new boolean[4];
            while(2*dX*direction < size) {
                if((direction < 0 && inBottom) || (direction > 0 && inTop))
                    this.renderTile(x, y, discriminator, corners, color, lineWidth, linesInlineIn, extendedWidth, extendedHeight);
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
                if((direction < 0 && inLeft) || (direction > 0 && inRight))
                    this.renderTile(x, y, discriminator, corners, color, lineWidth, linesInlineIn, extendedWidth, extendedHeight);
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

    private void renderTile(float x, float y, int discriminator, Vec2d[] corners, Color color, float lineWidth, boolean[] loopingConditions, double extendedWidth, double extendedHeight) {
        Vec2d[] renderCorners = new Vec2d[4];
        try {
            for(int i=0; i<renderCorners.length; i++) {
                renderCorners[i] = this.cache.getRenderPos(corners[i], discriminator);
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
        return ID;
    }
    
    private class ProjectionCache {
        
        GeographicProjection projection;
        
        Map<Vec2d, GeoPoint> mcToGeo = new HashMap<>();
        Set<Vec2d> accessedInCycle = new HashSet<>();
        
        int maxProjectionsPerCycle = 50;
        int[] projectionsThisCycle;
        
        ProjectionCache(int diffCount) {
            this.projectionsThisCycle = new int[diffCount];
        }
        
        Vec2d getRenderPos(Vec2d mcPos, int discriminator) throws OutOfProjectionBoundsException {
            this.accessedInCycle.add(mcPos);
            
            // Not really out of bounds but we don't need to differentiate the two
            if(this.projectionsThisCycle[discriminator] >= this.maxProjectionsPerCycle) throw OutOfProjectionBoundsException.get();
            
            GeoPoint location;
            
            // Try getting a cached value
            if(this.mcToGeo.containsKey(mcPos)) {
                location = this.mcToGeo.get(mcPos);
                if(location == null) throw OutOfProjectionBoundsException.get();
            } else {
                // Fallback to computing it
                try {
                    this.projectionsThisCycle[discriminator]++;
                    location = new GeoPoint(this.projection.toGeo(mcPos.x, mcPos.y));
                    this.mcToGeo.put(mcPos, location);
                } catch(OutOfProjectionBoundsException e) {
                    this.mcToGeo.put(mcPos, null);
                    throw e;
                }
            }
            
            if(! WebMercatorUtil.PROJECTION_BOUNDS.contains(location)) throw OutOfProjectionBoundsException.get();
            return McChunksLayer.this.getRenderPos(location);
        }
        
        void cycle() {
            this.mcToGeo.keySet().retainAll(this.accessedInCycle);
            this.accessedInCycle.clear();
            this.projectionsThisCycle = new int[this.projectionsThisCycle.length];
        }
        
        
    }

    @Override
    public boolean showButton() {
        return true;
    }

    @Override
    public ToggleButtonWidget getButton() {
        return this.button;
    }

    @Override
    public String getSaveName() {
        return this.getId();
    }

    @Override
    public void setVisibility(boolean visibility) {
        this.visible = visibility;
        this.button.setState(visibility);
    }

    @Override
    public boolean getVisibility() {
        return this.visible;
    }

    @Override
    public boolean isVisible(WidgetContainer parent) {
        return this.visible;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public MapLayer copy() {
        McChunksLayer layer = new McChunksLayer(this.getTileScaling());
        this.copyPropertiesToOther(layer);
        return layer;
    }

    @Override
    public String name() {
        return "Minecraft outlines"; //TODO localized
    }

    @Override
    public String description() {
        return "Regions and chunks"; //TODO localized
    }

}
