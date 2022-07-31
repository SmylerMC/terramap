package fr.thesmyler.terramap.gui.widgets.map.layer;

import java.util.HashSet;
import java.util.Set;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.Font;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.smylibgui.util.Util;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.maps.raster.IRasterTile;
import fr.thesmyler.terramap.maps.raster.IRasterTiledMap;
import fr.thesmyler.terramap.maps.raster.MapStylesLibrary;
import fr.thesmyler.terramap.maps.raster.imp.UrlRasterTile;
import fr.thesmyler.terramap.util.ICopyrightHolder;
import fr.thesmyler.terramap.util.geo.*;
import fr.thesmyler.terramap.util.geo.TilePos.InvalidTilePositionException;
import fr.thesmyler.terramap.util.math.Mat2d;
import fr.thesmyler.terramap.util.math.Vec2dImmutable;
import fr.thesmyler.terramap.util.math.Vec2dMutable;
import fr.thesmyler.terramap.util.math.Vec2dReadOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class RasterMapLayer extends MapLayer implements ICopyrightHolder {

    protected final IRasterTiledMap tiledMap;
    protected Set<IRasterTile> lastNeededTiles = new HashSet<>();

    // Used for calculations
    private final Vec2dMutable top = new Vec2dMutable();
    private final Vec2dMutable left = new Vec2dMutable();
    private final Vec2dMutable bottom = new Vec2dMutable();
    private final Vec2dMutable right = new Vec2dMutable();
    private final Vec2dMutable minusCenterPos = new Vec2dMutable();
    private final Vec2dMutable distanceToCenterCalculator = new Vec2dMutable();
    private GeoPointReadOnly focusedPoint;
    private Vec2dReadOnly renderingSpaceDimensions;
    private Vec2dReadOnly halfRenderingSpaceDimensions;

    public RasterMapLayer() {
        //TODO remove work-around constructor
        this.tiledMap = TerramapClientContext.getContext().getMapStyles().get("osm");
    }

    public RasterMapLayer(MapWidget map, IRasterTiledMap tiledMap) {
        //TODO Remove RasterMapLayer constructor
        this.tiledMap = tiledMap;
    }

    public IRasterTiledMap getTiledMap() {
        return this.tiledMap;
    }

    @Override
    protected void initialize() {
        this.focusedPoint = this.getMap().getController().getCenterLocation();
        this.renderingSpaceDimensions = this.getRenderSpaceDimensions();
        this.halfRenderingSpaceDimensions = this.getRenderSpaceHalfDimensions();
    }

    @Override
    public String getId() {
        return "tiled-raster-" + this.tiledMap.getId();
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {

        Font smallFont = Util.getSmallestFont();
        Minecraft mc = Minecraft.getMinecraft();
        TextureManager textureManager = mc.getTextureManager();
        float rotation = this.getRotation();

        boolean perfectDraw = true;
        Set<IRasterTile> neededTiles = new HashSet<>();

        MapWidget parentMap = (MapWidget) parent;
        boolean debug = parentMap.isDebugMode();
        Profiler profiler = parentMap.getProfiler();

        profiler.startSection("render-raster-layer_" + this.tiledMap.getId());

        GlStateManager.pushMatrix();
        float widthViewPort = this.getWidth();
        float heightViewPort = this.getHeight();
        double zoom = this.getMap().getController().getZoom();

        // These are the x and y original elementary vectors expressed in the rotated coordinate system
        Mat2d rotationMatrix = this.getRotationMatrix();
        Vec2dImmutable xvec = rotationMatrix.line1();
        Vec2dImmutable yvec = rotationMatrix.line2();

        this.applyRotationGl(x, y);
        
        Vec2dReadOnly upperLeft = this.getUpperLeftRenderCornerPositionInMercatorSpace();

        int zoomLevel = (int) Math.round(zoom);
        double zoomSizeFactor = zoom - zoomLevel;

        double renderSize = WebMercatorUtil.TILE_DIMENSIONS / this.getMap().getTileScaling() * Math.pow(2, zoomSizeFactor);

        int maxTileXY = WebMercatorUtil.getDimensionsInTile(zoomLevel);
        double maxX = upperLeft.x() + this.renderingSpaceDimensions.x();
        double maxY = upperLeft.y() + this.renderingSpaceDimensions.y();

        int lowerTileX = (int) Math.floor(upperLeft.x() / renderSize);
        int lowerTileY = (int) Math.floor(upperLeft.y() / renderSize);
        
        Color whiteWithAlpha = Color.WHITE.withAlpha(this.getAlpha());

        for(int tileX = lowerTileX; tileX * renderSize < maxX; tileX++) {

            for(int tileY = lowerTileY; tileY * renderSize < maxY; tileY++) {

                IRasterTile tile;

                try {
                    tile = tiledMap.getTile(zoomLevel, Math.floorMod(tileX, maxTileXY), tileY);
                } catch(InvalidTilePositionException silenced) { continue ;}

                // This is the tile we would like to render, but it is not possible if it hasn't been cached yet
                IRasterTile bestTile = tile;
                double dispX = tileX * renderSize - upperLeft.x();
                double dispY = tileY * renderSize - upperLeft.y();
                double displayWidth = Math.min(renderSize, maxX - tileX * renderSize);
                double displayHeight = Math.min(renderSize, maxY - tileY * renderSize);

                /* 
                 * Let's do some checks to ensure the tile is indeed visible when rotation is taken into account.
                 * To do that, we project each corner of the tile onto the corresponding unit vector of the non-rotated coordinate system,
                 * and if the result of the projection is further than the limit, we skip the tile.
                 */
                //FIXME often crops out the corners, probably a floating point precision problem
                if(rotation < 90) {
                    this.top.set(dispX, dispY);
                    this.right.set(dispX + displayWidth, dispY);
                    this.bottom.set(dispX + displayWidth, dispY + displayHeight);
                    this.left.set(dispX, dispY + displayHeight);
                } else if(rotation < 180){
                    this.right.set(dispX, dispY);
                    this.bottom.set(dispX + displayWidth, dispY);
                    this.left.set(dispX + displayWidth, dispY + displayHeight);
                    this.top.set(dispX, dispY + displayHeight);
                } else if(rotation < 270){
                    this.bottom.set(dispX, dispY);
                    this.left.set(dispX + displayWidth, dispY);
                    this.top.set(dispX + displayWidth, dispY + displayHeight);
                    this.right.set(dispX, dispY + displayHeight);
                } else {
                    this.left.set(dispX, dispY);
                    this.top.set(dispX + displayWidth, dispY);
                    this.right.set(dispX + displayWidth, dispY + displayHeight);
                    this.bottom.set(dispX, dispY + displayHeight);
                }
                this.top.subtract(this.halfRenderingSpaceDimensions);
                this.right.subtract(this.halfRenderingSpaceDimensions);
                this.bottom.subtract(this.halfRenderingSpaceDimensions);
                this.left.subtract(this.halfRenderingSpaceDimensions);

                if(this.bottom.dotProd(yvec) < -heightViewPort / 2) continue;
                if(this.top.dotProd(yvec) > heightViewPort / 2) continue;
                if(this.right.dotProd(xvec) < -widthViewPort / 2) continue;
                if(this.left.dotProd(xvec) > widthViewPort / 2) continue;

                neededTiles.add(bestTile);
                boolean lowerResRender = false;
                boolean unlockedZoomRender = false;
                if(!bestTile.isTextureAvailable()) {
                    lowerResRender = true;
                    perfectDraw = false;
                    if(zoomLevel > this.tiledMap.getMaxZoom()) {
                        unlockedZoomRender = true;
                    }

                    while(tile.getPosition().getZoom() > 0 && !tile.isTextureAvailable()) {
                        try {
                            tile = this.tiledMap.getTile(tile.getPosition().getZoom()-1, tile.getPosition().getX() /2, tile.getPosition().getY() /2);
                        } catch(InvalidTilePositionException silenced) {
                            break;
                        }
                        if(tile.getPosition().getZoom() == this.tiledMap.getMaxZoom()) {
                            try {
                                tile.getTexture();
                                neededTiles.add(tile);
                            } catch (Throwable e) {
                                parentMap.reportError(this, e.toString());
                            }
                        }
                    }
                }

                neededTiles.add(tile);

                double renderSizedSize = renderSize;

                double dX = 0;
                double dY = 0;

                if(tileX == lowerTileX) {
                    dX -= dispX;
                    dispX = 0;
                    displayWidth -= dX;
                }
                if(tileY == lowerTileY) {
                    dY -= dispY;
                    dispY = 0;
                    displayHeight -= dY;
                }

                if(lowerResRender) {
                    int sizeFactor = (1 <<(bestTile.getPosition().getZoom() - tile.getPosition().getZoom()));

                    int xInBiggerTile = bestTile.getPosition().getX() - sizeFactor * tile.getPosition().getX();
                    int yInBiggerTile = bestTile.getPosition().getY() - sizeFactor * tile.getPosition().getY();

                    double factorX = (double)xInBiggerTile / (double)sizeFactor;
                    double factorY = (double)yInBiggerTile / (double)sizeFactor;
                    renderSizedSize *= sizeFactor;
                    dX += factorX * renderSizedSize;
                    dY += factorY * renderSizedSize;
                }

                whiteWithAlpha.applyGL();
                ResourceLocation texture = UrlRasterTile.errorTileTexture;
                try {
                    if(tile.isTextureAvailable()) texture = tile.getTexture();
                    else perfectDraw = false;
                } catch (Throwable e) {
                    perfectDraw = false;
                    parentMap.reportError(this, e.toString());
                }
                textureManager.bindTexture(texture);
                RenderUtil.drawModalRectWithCustomSizedTexture(
                        dispX,
                        dispY,
                        dX, dY,
                        displayWidth,
                        displayHeight,
                        renderSizedSize,
                        renderSizedSize
                        );
                if(debug) {
                    Color lineColor = lowerResRender? unlockedZoomRender? Color.BLUE: Color.RED : Color.WHITE;
                    RenderUtil.drawClosedStrokeLine(lineColor, 1f, 
                            dispX, dispY,
                            dispX, dispY + displayHeight - 1,
                            dispX + displayWidth - 1, dispY + displayHeight - 1,
                            dispX + displayWidth - 1, dispY
                            );
                    smallFont.drawCenteredString((float)(dispX + displayWidth/2), (float)(dispY + displayHeight/2), String.valueOf(tile.getPosition().getZoom()), lineColor, false);
                    smallFont.drawString((float)dispX + 2, (float)(dispY + displayHeight/2), GeoServices.formatGeoCoordForDisplay(dispX), lineColor, false);
                    smallFont.drawCenteredString((float)(dispX + displayWidth/2), (float)dispY + 2, GeoServices.formatGeoCoordForDisplay(dispY), lineColor, false);
                }
                Color.WHITE.applyGL();
            }
        }


        WebMercatorUtil.fromGeo(this.minusCenterPos, this.focusedPoint, 0d).scale(- 1 / 256d);
        // Filter out tiles that are not needed and order the needed ones for loading.
        if(zoomLevel <= this.tiledMap.getMaxZoom()) {
            neededTiles.stream().filter(t -> !t.isTextureAvailable()).sorted((t1, t2) -> {
                TilePos pos1 = t1.getPosition();
                TilePos pos2 = t2.getPosition();
                int dz = Integer.compare(pos1.getZoom(), pos2.getZoom());
                if(dz != 0) return dz;
                double factor = 1d / (1 << pos1.getZoom());
                double dis1 = this.distanceToCenterCalculator.set(pos1.getX() + 0.5d, pos1.getY() + 0.5d)
                        .scale(factor)
                        .add(this.minusCenterPos)
                        .normSquared();
                double dis2 = this.distanceToCenterCalculator.set(pos2.getX() + 0.5d, pos2.getY() + 0.5d)
                        .scale(factor)
                        .add(this.minusCenterPos)
                        .normSquared();
                return Double.compare(dis1, dis2);
            }).forEachOrdered(tile -> {
                try {
                    tile.getTexture(); // Will start loading the tile
                } catch (Throwable e) {
                    parentMap.reportError(this, e.toString());
                }
            });
        }
        if(perfectDraw) parentMap.discardPreviousErrors(this);
        this.lastNeededTiles.removeAll(neededTiles);
        this.lastNeededTiles.forEach(IRasterTile::cancelTextureLoading);
        this.lastNeededTiles = neededTiles;

        GlStateManager.popMatrix();
        profiler.endSection();

    }

    @Override
    public ITextComponent getCopyright(String localeKey) {
        if(this.tiledMap instanceof ICopyrightHolder) {
            return ((ICopyrightHolder)this.tiledMap).getCopyright(localeKey);
        }
        return new TextComponentString("");
    }

    @Override
    public MapLayer copy(MapWidget forMap) {
        RasterMapLayer other = new RasterMapLayer(forMap, this.tiledMap);
        this.copyPropertiesToOther(other);
        return other;
    }

    @Override
    public String name() {
        return this.tiledMap.getLocalizedName(SmyLibGui.getGameContext().getLanguage());
    }

    @Override
    public String description() {
        return SmyLibGui.getTranslator().format("terramap.mapwidget.layers.raster.desc");
    }

}
