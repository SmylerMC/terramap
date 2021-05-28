package fr.thesmyler.terramap.gui.widgets.map.layer;

import java.util.HashMap;
import java.util.Map;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.util.Vec2d;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.minecraft.client.renderer.GlStateManager;

public class McChunksLayer extends MapLayer {

    public McChunksLayer(double tileScaling) {
        super(tileScaling);
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        MapWidget map = (MapWidget)parent;
        GeographicProjection projection = TerramapClientContext.getContext().getProjection();
        if(projection == null) return;
        map.getProfiler().startSection("layer-" + this.getId());

        double extendedWidth = this.getExtendedWidth();
        double extendedHeight = this.getExtendedHeight();        

        //TODO Fade away instead
        boolean render2dr = false;
        boolean render3dr = false;
        boolean renderChunks = false;
        double renderThreshold = 128d;
        int maxTiles = 50;

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

        Map<Vec2d, Vec2d> positions = new HashMap<>();
        GlStateManager.pushMatrix();
        this.applyRotationGl(x, y);

        if(render2dr) {
            Vec2d center2dr = new Vec2d(Math.floorDiv((long)Math.floor(centerMc.x), 512), Math.floorDiv((long)Math.floor(centerMc.y), 512));
            int dX = 0;
            int dY = 0;
            Vec2d[] corners = {
                    new Vec2d(center2dr.x * 512, center2dr.y * 512),
                    new Vec2d(center2dr.x * 512, (center2dr.y + 1) * 512),
                    new Vec2d((center2dr.x + 1) * 512, (center2dr.y + 1) * 512),
                    new Vec2d((center2dr.x + 1) * 512, center2dr.y * 512)
            };
            int direction = 1;
            int size = 1;
            int safety = 0;
            boolean inTop, inBottom, inLeft, inRight;
            inTop = inBottom = inLeft = inRight = true;
            while((inTop || inBottom || inRight || inLeft) && safety++ < maxTiles) {
                
                boolean lineInLeft, lineInRight, lineInTop, lineInBottom;
                lineInLeft = lineInRight = lineInTop = lineInBottom = false;
                while(2*dX*direction < size) {
                    Vec2d[] renderCorners = new Vec2d[4];
                    for(int i=0; i<renderCorners.length; i++) {
                        renderCorners[i] = positions.get(corners[i]);
                        if(renderCorners[i] == null) {
                            try {
                                double[] lola = projection.toGeo(corners[i].x, corners[i].y);
                                renderCorners[i] = new Vec2d(this.getRenderX(lola[0]), this.getRenderY(lola[1]));
                                positions.put(corners[i], renderCorners[i]);
                            } catch(OutOfProjectionBoundsException silenced) {
                                // Skip it
                                break;
                            }
                        }
                    }
                    
                    if(renderCorners[0] == null || renderCorners[1] == null || renderCorners[2] == null || renderCorners[3] == null) continue;
                    
                    for(Vec2d corner: renderCorners) {
                        lineInLeft = lineInLeft || corner.x >= 0;
                        lineInRight = lineInRight || corner.x <= extendedWidth;
                        lineInTop = lineInTop || corner.y >= 0;
                        lineInBottom = lineInBottom || corner.y <= extendedHeight;
                    }

                    RenderUtil.drawClosedStrokeLine(Color.DARK_GRAY, 2f,
                            x + renderCorners[0].x, y + renderCorners[0].y,
                            x + renderCorners[1].x, y + renderCorners[1].y,
                            x + renderCorners[2].x, y + renderCorners[2].y,
                            x + renderCorners[3].x, y + renderCorners[3].y
                    );
                    
                    dX += direction;
                    int step = 512*direction;
                    for(int i=0; i<corners.length; i++) corners[i] = corners[i].add(step, 0);
                }
                
                if(!lineInLeft) inLeft = false;
                if(!lineInRight) inRight = false;
                if(!lineInTop) inTop = false;
                if(!lineInBottom) inBottom = false;
                lineInLeft = lineInRight = lineInTop = lineInBottom = false;

                while(2*dY*direction < size) {
                    Vec2d[] renderCorners = new Vec2d[4];
                    for(int i=0; i<renderCorners.length; i++) {
                        renderCorners[i] = positions.get(corners[i]);
                        if(renderCorners[i] == null) {
                            try {
                                double[] lola = projection.toGeo(corners[i].x, corners[i].y);
                                renderCorners[i] = new Vec2d(this.getRenderX(lola[0]), this.getRenderY(lola[1]));
                                positions.put(corners[i], renderCorners[i]);
                            } catch(OutOfProjectionBoundsException silenced) {
                                // Skip it
                                break;
                            }
                        }
                    }
                    
                    if(renderCorners[0] == null || renderCorners[1] == null || renderCorners[2] == null || renderCorners[3] == null) continue;
                    
                    for(Vec2d corner: renderCorners) {
                        lineInLeft = lineInLeft || corner.x >= 0;
                        lineInRight = lineInRight || corner.x <= extendedWidth;
                        lineInTop = lineInTop || corner.y >= 0;
                        lineInBottom = lineInBottom || corner.y <= extendedHeight;
                    }

                    RenderUtil.drawClosedStrokeLine(Color.DARK_GRAY, 2f,
                            x + renderCorners[0].x, y + renderCorners[0].y,
                            x + renderCorners[1].x, y + renderCorners[1].y,
                            x + renderCorners[2].x, y + renderCorners[2].y,
                            x + renderCorners[3].x, y + renderCorners[3].y
                    );
                    
                    dY += direction;
                    int step = 512*direction;
                    for(int i=0; i<corners.length; i++) corners[i] = corners[i].add(0, step);
                }
                
                if(!lineInLeft) inLeft = false;
                if(!lineInRight) inRight = false;
                if(!lineInTop) inTop = false;
                if(!lineInBottom) inBottom = false;

                direction *= -1;
                size++;
            }
            
            //TODO remove
            if(safety >= maxTiles) RenderUtil.drawRect(x + extendedWidth / 2, y + extendedHeight / 2, x + extendedWidth / 2 + 10, y + extendedHeight / 2 + 10, Color.RED);
        }

        GlStateManager.popMatrix();
        map.getProfiler().endSection();
    }

    @Override
    public String getId() {
        return "mcchunks";
    }

}
