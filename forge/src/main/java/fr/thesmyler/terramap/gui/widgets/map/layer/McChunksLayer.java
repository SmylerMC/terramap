package fr.thesmyler.terramap.gui.widgets.map.layer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.smyler.smylib.gui.UiDrawContext;
import net.smyler.smylib.gui.containers.FlexibleWidgetContainer;
import net.smyler.smylib.gui.containers.WidgetContainer;
import net.smyler.smylib.Color;
import net.smyler.smylib.gui.widgets.ColorPickerWidget;
import net.smyler.smylib.gui.widgets.buttons.ToggleButtonWidget;
import net.smyler.smylib.gui.widgets.text.TextWidget;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import net.smyler.smylib.gui.Font;
import net.smyler.terramap.Terramap;
import net.smyler.terramap.util.geo.GeoPointImmutable;
import net.smyler.terramap.util.geo.GeoPointMutable;
import net.smyler.terramap.util.geo.GeoPointReadOnly;
import net.smyler.terramap.util.geo.WebMercatorUtil;
import net.smyler.smylib.math.Vec2d;
import net.smyler.smylib.math.Vec2dMutable;
import net.smyler.smylib.math.Vec2dReadOnly;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;

import static net.smyler.smylib.SmyLib.getGameClient;
import static net.smyler.smylib.math.Math.clamp;
import static java.lang.Math.floor;
import static java.lang.Math.floorDiv;
import static net.smyler.smylib.text.ImmutableText.ofTranslation;

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

    private boolean render2dr = true;
    private boolean render3dr = true;
    private boolean renderChunks = true;
    private boolean renderBlocks = true;

    private Color color = Color.DARK_GRAY;
    private Color color2dr = Color.DARK_GRAY;
    private Color color3dr = Color.DARK_GRAY;
    private Color colorChunks = Color.DARK_GRAY;
    private Color colorBlocks = Color.DARK_GRAY;

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
    public JsonObject saveSettings() {
        JsonObject json = new JsonObject();
        json.add("render2dr", new JsonPrimitive(this.isRender2dr()));
        json.add("color2dr", this.saveColor(this.getColor2dr()));
        json.add("render3dr", new JsonPrimitive(this.isRender3dr()));
        json.add("color3dr", this.saveColor(this.getColor3dr()));
        json.add("renderChunks", new JsonPrimitive(this.isRenderChunks()));
        json.add("colorChunks", this.saveColor(this.getColorChunks()));
        json.add("renderBlocks", new JsonPrimitive(this.isRenderBlocks()));
        json.add("colorBlocks", this.saveColor(this.getColorBlocks()));
        return json;
    }

    private JsonObject saveColor(Color color) {
        JsonObject json = new JsonObject();
        json.add("red", new JsonPrimitive(color.red()));
        json.add("green", new JsonPrimitive(color.green()));
        json.add("blue", new JsonPrimitive(color.blue()));
        return json;
    }

    @Override
    public void loadSettings(JsonObject json) {
        try {
            this.setRender2dr(json.get("render2dr").getAsBoolean());
            this.loadColor(json.getAsJsonObject("color2dr"), this::setColor2dr);
            this.setRender3dr(json.get("render3dr").getAsBoolean());
            this.loadColor(json.getAsJsonObject("color3dr"), this::setColor3dr);
            this.setRenderChunks(json.get("renderChunks").getAsBoolean());
            this.loadColor(json.getAsJsonObject("colorChunks"), this::setColorChunks);
            this.setRenderBlocks(json.get("renderBlocks").getAsBoolean());
            this.loadColor(json.getAsJsonObject("colorBlocks"), this::setColorBlocks);
        } catch (NullPointerException | ClassCastException | IllegalStateException e) {
            Terramap.instance().logger().warn("Failed to load mc boundaries layer settings: {}", json);
        }
    }

    private void loadColor(JsonObject object, Consumer<Color> setter) {
            int red = clamp(object.get("red").getAsInt(), 0, 255);
            int green = clamp(object.get("green").getAsInt(), 0, 255);
            int blue = clamp(object.get("blue").getAsInt(), 0, 255);
            setter.accept(new Color(red, green, blue));
    }

    @Override
    public void draw(UiDrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
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
            if(d < renderThreshold) render2dr = this.render2dr;
            if(d < renderThreshold / 2) render3dr = this.render3dr;
            if(d < renderThreshold / 16) renderChunks = this.renderChunks;
            if(d < renderThreshold / 128) renderBlocks = this.renderBlocks;
        } catch(OutOfProjectionBoundsException silenced) {
            // The center is out of bounds, let's not render anything
            return;
        }

        context.gl().pushViewMatrix();
        this.applyRotationGl(context, x, y);
        

        float size = 1f;
        if(renderBlocks) {
            this.renderGrid(context, x, y, 0, 1, this.colorBlocks.withAlpha(this.getAlpha()), size);
            size += 1f;
        }
        if(renderChunks) {
            this.renderGrid(context, x, y, 1, 16, this.colorChunks.withAlpha(this.getAlpha()), size);
            size += 1f;
        }
        if(render3dr) {
            this.renderGrid(context, x, y, 2, 256, this.color3dr.withAlpha(this.getAlpha()), size);
            size += 1f;
        }
        if(render2dr) {
            this.renderGrid(context, x, y, 3, 512, this.color2dr.withAlpha(this.getAlpha()), size);
        }

        this.cache.cycle();
        context.gl().popViewMatrix();
        map.getProfiler().endSection();
    }
    
    private void renderGrid(UiDrawContext context, float x, float y, int discriminator, long tileSize, Color color, float lineWidth) {
        
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
                    this.renderTile(context, x, y, discriminator, color, lineWidth, linesInlineIn);
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
                    this.renderTile(context, x, y, discriminator, color, lineWidth, linesInlineIn);
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

    private void renderTile(UiDrawContext context, float x, float y, int discriminator, Color color, float lineWidth, boolean[] loopingConditions) {
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
        context.drawClosedStrokeLine(color, lineWidth,
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

    @Deprecated
    public Color getColor() {
        return color;
    }

    @Deprecated
    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isRender2dr() {
        return this.render2dr;
    }

    public void setRender2dr(boolean render2dr) {
        this.render2dr = render2dr;
    }

    public boolean isRender3dr() {
        return this.render3dr;
    }

    public void setRender3dr(boolean render3dr) {
        this.render3dr = render3dr;
    }

    public boolean isRenderChunks() {
        return this.renderChunks;
    }

    public void setRenderChunks(boolean renderChunks) {
        this.renderChunks = renderChunks;
    }

    public boolean isRenderBlocks() {
        return this.renderBlocks;
    }

    public void setRenderBlocks(boolean renderBlocks) {
        this.renderBlocks = renderBlocks;
    }

    public Color getColor2dr() {
        return this.color2dr;
    }

    public void setColor2dr(Color color2dr) {
        this.color2dr = color2dr;
    }

    public Color getColor3dr() {
        return this.color3dr;
    }

    public void setColor3dr(Color color3dr) {
        this.color3dr = color3dr;
    }

    public Color getColorChunks() {
        return this.colorChunks;
    }

    public void setColorChunks(Color colorChunks) {
        this.colorChunks = colorChunks;
    }

    public Color getColorBlocks() {
        return this.colorBlocks;
    }

    public void setColorBlocks(Color colorBlocks) {
        this.colorBlocks = colorBlocks;
    }

    @Override
    public String name() {
        return getGameClient().translator().format("terramap.mapwidget.layers.mcchunks.name");
    }

    @Override
    public String description() {
        return getGameClient().translator().format("terramap.mapwidget.layers.mcchunks.desc");
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public FlexibleWidgetContainer createConfigurationContainer() {
        float width = 200f;
        float height = 180f;
        FlexibleWidgetContainer container = new FlexibleWidgetContainer(0, 0, 0, width, height);
        Font font = getGameClient().defaultFont();
        float left = 1f;
        float right = 30f;
        float interline = 9f;
        float textYOffset = 3f;

        float y = 0;

        container.addWidget(new TextWidget(
                left, y + textYOffset, 10,
                ofTranslation("terramap.terramapscreen.outlines_config.mca"), font));
        container.addWidget(new ToggleButtonWidget(
                width - right, y, 0,
                McChunksLayer.this.isRender2dr(),
                McChunksLayer.this::setRender2dr)
        );
        y += font.height() + interline;
        ColorPickerWidget colorPicker2dr = new ColorPickerWidget(left, y, 0, McChunksLayer.this.getColor2dr(), font);
        colorPicker2dr.setOnColorChange(c -> c.ifPresent(McChunksLayer.this::setColor2dr));
        container.addWidget(colorPicker2dr);
        y += 20 + interline;

        container.addWidget(new TextWidget(
                left, y + textYOffset, 10,
                ofTranslation("terramap.terramapscreen.outlines_config.3dr"), font));
        container.addWidget(new ToggleButtonWidget(
                width - right, y, 0,
                McChunksLayer.this.isRender3dr(),
                McChunksLayer.this::setRender3dr)
        );
        y += font.height() + interline;
        ColorPickerWidget colorPicker3dr = new ColorPickerWidget(left, y, 0, McChunksLayer.this.getColor3dr(), font);
        colorPicker3dr.setOnColorChange(c -> c.ifPresent(McChunksLayer.this::setColor3dr));
        container.addWidget(colorPicker3dr);
        y += 20 + interline;

        container.addWidget(new TextWidget(
                left, y + textYOffset, 10,
                ofTranslation("terramap.terramapscreen.outlines_config.chunks"), font));
        container.addWidget(new ToggleButtonWidget(
                width - right, y, 0,
                McChunksLayer.this.isRenderChunks(),
                McChunksLayer.this::setRenderChunks)
        );
        y += font.height() + interline;
        ColorPickerWidget colorPickerChunks = new ColorPickerWidget(left, y, 0, McChunksLayer.this.getColorChunks(), font);
        colorPickerChunks.setOnColorChange(c -> c.ifPresent(McChunksLayer.this::setColorChunks));
        container.addWidget(colorPickerChunks);
        y += 20 + interline;

        container.addWidget(new TextWidget(
                left, y + textYOffset, 10,
                ofTranslation("terramap.terramapscreen.outlines_config.blocks"), font));
        container.addWidget(new ToggleButtonWidget(
                width - right, y, 0,
                McChunksLayer.this.isRenderBlocks(),
                McChunksLayer.this::setRenderBlocks)
        );
        y += font.height() + interline;
        ColorPickerWidget colorPickerBlocks = new ColorPickerWidget(left, y, 0, McChunksLayer.this.getColorBlocks(), font);
        colorPickerBlocks.setOnColorChange(c -> c.ifPresent(McChunksLayer.this::setColorBlocks));
        container.addWidget(colorPickerBlocks);

        return container;
    }

}
