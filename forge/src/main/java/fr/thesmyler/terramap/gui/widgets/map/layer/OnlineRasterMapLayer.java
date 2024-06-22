package fr.thesmyler.terramap.gui.widgets.map.layer;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.smyler.smylib.gui.containers.FlexibleWidgetContainer;
import net.smyler.smylib.gui.containers.WidgetContainer;
import net.smyler.smylib.Animation;
import net.smyler.smylib.Color;
import net.smyler.smylib.gui.widgets.text.TextWidget;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.TerramapConfig;
import fr.thesmyler.terramap.gui.widgets.map.MapController;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.maps.raster.RasterTiledMap;
import fr.thesmyler.terramap.maps.raster.imp.ColorTiledMap;
import net.smyler.terramap.util.CopyrightHolder;
import net.smyler.smylib.game.GameClient;
import net.smyler.smylib.gui.DrawContext;
import net.smyler.smylib.gui.Font;

import net.smyler.smylib.text.ImmutableText;
import net.smyler.smylib.text.Text;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.function.Consumer;

import static net.smyler.smylib.Animation.AnimationState.LEAVE;
import static net.smyler.smylib.Color.*;
import static fr.thesmyler.terramap.MapContext.PREVIEW;
import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static net.smyler.smylib.SmyLib.getGameClient;
import static net.smyler.smylib.text.ImmutableText.ofPlainText;

public class OnlineRasterMapLayer extends RasterMapLayer implements CopyrightHolder {

    protected RasterTiledMap tiledMap = new ColorTiledMap(Color.WHITE, "Empty map");

    public RasterTiledMap getTiledMap() {
        return this.tiledMap;
    }

    public void setTiledMap(RasterTiledMap map) {
        this.tiledMap = map;
        this.getMap().updateCopyright();
    }

    @Override
    public Text getCopyright(String localeKey) {
        if(this.tiledMap instanceof CopyrightHolder) {
            return ((CopyrightHolder)this.tiledMap).getCopyright(localeKey);
        }
        return ImmutableText.EMPTY;
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
            RasterTiledMap tiledMap = TerramapClientContext.getContext().getMapStyles().get(styleId);
            if (tiledMap != null) {
                this.setTiledMap(tiledMap);
            }
        } catch (IllegalStateException | NullPointerException ignored) {
            // Too bad, we can't load it
        }
    }

    @Override
    public String name() {
        return this.tiledMap.getLocalizedName(getGameClient().translator().language());
    }

    @Override
    public String description() {
        return getGameClient().translator().format("terramap.mapwidget.layers.raster.desc");
    }

    public boolean isConfigurable() {
        return true;
    }

    @Override
    public FlexibleWidgetContainer createConfigurationContainer() {

        GameClient game = getGameClient();
        final Font font = game.defaultFont();
        final Font smallFont = game.defaultFont().withScale(0.5f);
        final String language = game.translator().language();
        final float width = 250f;
        final float margin = 5f;
        final float entryHeight = 60f;
        final float mapWidth = 100f;
        final Color hoverColor = LIGHT_GRAY.withAlpha(0.35f);
        final Color selectedColor = SELECTION.withAlpha(0.5f);

        class StyleEntry extends WidgetContainer {

            final RasterTiledMap style;

            final TextWidget nameText;
            final TextWidget infoText;
            final TextWidget copyrightText;
            final MapWidget previewMap;

            boolean selected;
            float y = 0;
            Consumer<StyleEntry> onClick;

            final Animation backgroundColorAnimation = new Animation(200);

            public StyleEntry(RasterTiledMap style) {
                super(0);
                this.style = style;
                float y = margin;
                this.nameText = new TextWidget(margin, y, 0, ofPlainText(style.getLocalizedName(language)), font);
                this.addWidget(this.nameText);
                y += this.nameText.getHeight() + margin;
                Text line2 = ofPlainText(format("%s - %sv%s", style.getId(), style.getProvider(), style.getProviderVersion()));
                this.infoText = new TextWidget(this.nameText.getX(), y, 0, line2, smallFont);
                this.addWidget(this.infoText);
                if (style instanceof CopyrightHolder) {
                    CopyrightHolder copyrightHolder = (CopyrightHolder) style;
                    Text copyright = copyrightHolder.getCopyright(language);
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
                MapController previewController = this.previewMap.getController();
                MapController controller = OnlineRasterMapLayer.this.getMap().getController();
                OnlineRasterMapLayer layer = (OnlineRasterMapLayer) this.previewMap.copyLayer(OnlineRasterMapLayer.this);
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
                this.addWidget(this.previewMap);
                this.setSelected(style == OnlineRasterMapLayer.this.getTiledMap());
            }

            @Override
            public void draw(DrawContext context, float x, float y, float mouseX, float mouseY, boolean screenHovered, boolean screenFocused, @Nullable WidgetContainer parent) {
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
                context.drawRectangleWithContours(x, y, x + width, y + height, background , 1f, Color.DARK_GRAY);
                super.draw(context, x, y, mouseX, mouseY, screenHovered, screenFocused, parent);
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
                .sorted(comparing(RasterTiledMap::getDisplayPriority).reversed())
                .map(s -> new StyleEntry(s))
                .collect(toList());

        float y = margin;
        for (StyleEntry style: styles) {
            style.y = y;
            style.onClick = s -> {
                styles.forEach(StyleEntry::unselect);
                OnlineRasterMapLayer.this.setTiledMap(style.style);
            };
            y += style.getHeight() + margin;
            container.addWidget(style);
        }
        y += margin;
        container.setHeight(y);
        return container;
    }

}
