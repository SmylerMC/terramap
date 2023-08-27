package fr.thesmyler.terramap.gui.widgets.map.layer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.FlexibleWidgetContainer;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.*;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.TerramapConfig;
import fr.thesmyler.terramap.gui.widgets.map.MapController;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.maps.raster.IRasterTile;
import fr.thesmyler.terramap.maps.raster.IRasterTiledMap;
import fr.thesmyler.terramap.maps.raster.imp.ColorTiledMap;
import fr.thesmyler.terramap.maps.raster.imp.UrlRasterTile;
import fr.thesmyler.terramap.util.CopyrightHolder;
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

import javax.annotation.Nullable;

import static fr.thesmyler.smylibgui.SmyLibGui.*;
import static fr.thesmyler.smylibgui.util.Animation.AnimationState.LEAVE;
import static fr.thesmyler.smylibgui.util.Color.*;
import static fr.thesmyler.terramap.MapContext.PREVIEW;
import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class RasterMapLayer extends MapLayer implements CopyrightHolder {

    protected IRasterTiledMap tiledMap = new ColorTiledMap(Color.WHITE, "Empty map");
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

    public IRasterTiledMap getTiledMap() {
        return this.tiledMap;
    }

    public void setTiledMap(IRasterTiledMap map) {
        this.tiledMap = map;
        this.getMap().updateCopyright();
    }

    @Override
    protected void initialize() {
        this.focusedPoint = this.getMap().getController().getCenterLocation();
        this.renderingSpaceDimensions = this.getRenderSpaceDimensions();
        this.halfRenderingSpaceDimensions = this.getRenderSpaceHalfDimensions();
    }

    @Override
    public JsonObject saveSettings() {
        JsonObject object = new JsonObject();
        object.add("style", new JsonPrimitive(this.tiledMap.getId()));
        return object;
    }

    @Override
    public void loadSettings(JsonObject json) {
        try {
            JsonPrimitive primitiveValue = json.getAsJsonPrimitive("style");
            String styleId = primitiveValue.getAsString();
            IRasterTiledMap tiledMap = TerramapClientContext.getContext().getMapStyles().get(styleId);
            if (tiledMap != null) {
                this.setTiledMap(tiledMap);
            }
        } catch (IllegalStateException | NullPointerException ignored) {
            // Too bad, we can't load it
        }
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
        if(this.tiledMap instanceof CopyrightHolder) {
            return ((CopyrightHolder)this.tiledMap).getCopyright(localeKey);
        }
        return new TextComponentString("");
    }

    @Override
    public String name() {
        return this.tiledMap.getLocalizedName(SmyLibGui.getGameContext().getLanguage());
    }

    @Override
    public String description() {
        return SmyLibGui.getTranslator().format("terramap.mapwidget.layers.raster.desc");
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public FlexibleWidgetContainer createConfigurationContainer() {

        final Font font = getDefaultFont();
        final Font smallFont = new Font(0.5f);
        final String language = getGameContext().getLanguage();
        final float width = 250f;
        final float margin = 5f;
        final float entryHeight = 60f;
        final float mapWidth = 100f;
        final Color hoverColor = LIGHT_GRAY.withAlpha(0.35f);
        final Color selectedColor = SELECTION.withAlpha(0.5f);

        class StyleEntry extends WidgetContainer {

            final IRasterTiledMap style;

            final TextWidget nameText;
            final TextWidget infoText;
            final TextWidget copyrightText;
            final MapWidget previewMap;

            boolean selected;
            float y = 0;
            Consumer<StyleEntry> onClick;

            final Animation backgroundColorAnimation = new Animation(200);

            public StyleEntry(IRasterTiledMap style) {
                super(0);
                this.style = style;
                float y = margin;
                this.nameText = new TextWidget(margin, y, 0, new TextComponentString(style.getLocalizedName(language)), font);
                this.addWidget(this.nameText);
                y += this.nameText.getHeight() + margin;
                TextComponentString line2 = new TextComponentString(format("%s - %sv%s", style.getId(), style.getProvider(), style.getProviderVersion()));
                this.infoText = new TextWidget(this.nameText.getX(), y, 0, line2, smallFont);
                this.addWidget(this.infoText);
                if (style instanceof CopyrightHolder) {
                    CopyrightHolder copyrightHolder = (CopyrightHolder) style;
                    ITextComponent copyright = copyrightHolder.getCopyright(language);
                    y += this.infoText.getHeight() + margin;
                    this.copyrightText = new TextWidget(
                            this.infoText.getX(), y, 0,
                            copyright, smallFont
                    );
                    this.copyrightText.setMaxWidth(this.getWidth() - margin * 3f - mapWidth);
                    this.addWidget(this.copyrightText);
                } else {
                    this.copyrightText = null;
                }
                this.previewMap = new MapWidget(
                        this.getWidth() - mapWidth - margin, margin, 0,
                        mapWidth, this.getHeight() - margin * 2f,
                        PREVIEW, TerramapConfig.CLIENT.getEffectiveTileScaling());
                MapController previewController = previewMap.getController();
                MapController controller = RasterMapLayer.this.getMap().getController();
                RasterMapLayer layer = (RasterMapLayer) previewMap.copyLayer(RasterMapLayer.this);
                layer.setAlpha(1f);
                layer.setTiledMap(style);
                this.previewMap.setInteractive(false);
                this.previewMap.setAllowsQuickTp(false);
                this.previewMap.setCopyrightVisibility(false);
                this.previewMap.setRightClickMenuEnabled(false);
                this.previewMap.setScaleVisibility(false);
                previewController.setMinZoom(style.getMinZoom());
                previewController.setMaxZoom(style.getMaxZoom());
                previewController.setZoom(controller.getZoom(), false);
                previewController.setRotation(controller.getRotation(), false);
                previewController.moveLocationToCenter(controller.getCenterLocation(), false);
                this.addWidget(previewMap);
                this.setSelected(style == RasterMapLayer.this.getTiledMap());
            }

            @Override
            public void draw(float x, float y, float mouseX, float mouseY, boolean screenHovered, boolean screenFocused, @Nullable WidgetContainer parent) {
                float width = this.getWidth();
                float height = this.getHeight();
                if (screenHovered) {
                    this.backgroundColorAnimation.start(LEAVE);
                }
                this.backgroundColorAnimation.update();
                Color background = backgroundColorAnimation.blend(hoverColor, LIGHT_OVERLAY);
                if (this.selected) {
                    background = selectedColor;
                }
                RenderUtil.drawRectWithContour(x, y, x + width, y + height, background , 1f, Color.DARK_GRAY);
                super.draw(x, y, mouseX, mouseY, screenHovered, screenFocused, parent);
            }

            public void setSelected(boolean selected) {
                this.selected = selected;
                if (selected) {
                    this.infoText.setBaseColor(LIGHT_GRAY);
                } else {
                    this.infoText.setBaseColor(MEDIUM_GRAY);
                }
            }

            @Override
            public boolean onClick(float mouseX, float mouseY, int mouseButton, @Nullable WidgetContainer parent) {
                this.onClick.accept(this);
                this.select();
                return false;
            }

            public void select() {
                this.setSelected(true);
            }

            public void unselect() {
                this.setSelected(false);
            }

            @Override
            public void init() {
                super.init();
                this.previewMap.setTileScaling(TerramapConfig.CLIENT.getEffectiveTileScaling());
            }

            @Override
            public float getX() {
                return margin;
            }

            @Override
            public float getY() {
                return this.y;
            }

            @Override
            public float getWidth() {
                return width - margin * 2f;
            }

            @Override
            public float getHeight() {
                return entryHeight;
            }
        }

        FlexibleWidgetContainer container = new FlexibleWidgetContainer(0f, 0f, 0, width, 200f);
        List<StyleEntry> styles = TerramapClientContext.getContext().getMapStyles().values()
                .stream()
                .sorted(comparing(IRasterTiledMap::getDisplayPriority).reversed())
                .map(s -> new StyleEntry(s))
                .collect(toList());

        float y = margin;
        for (StyleEntry style: styles) {
            style.y = y;
            style.onClick = s -> {
                styles.forEach(StyleEntry::unselect);
                RasterMapLayer.this.setTiledMap(style.style);
            };
            y += style.getHeight() + margin;
            container.addWidget(style);
        }
        y += margin;
        container.setHeight(y);
        return container;
    }

}
