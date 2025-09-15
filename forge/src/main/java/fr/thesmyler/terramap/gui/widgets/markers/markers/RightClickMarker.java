package fr.thesmyler.terramap.gui.widgets.markers.markers;

import net.smyler.smylib.gui.UiDrawContext;
import net.smyler.smylib.gui.containers.WidgetContainer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import net.smyler.smylib.gui.sprites.Sprite;
import net.smyler.smylib.text.ImmutableText;
import net.smyler.smylib.text.Text;
import net.smyler.terramap.TerramapResources;
import net.smyler.terramap.util.geo.GeoPointImmutable;

public class RightClickMarker extends AbstractFixedMarker {

    private static final Sprite SPRITE = Sprite.builder()
            .texture(TerramapResources.SPRITES.resolve("map_marker.png"))
            .textureDimensions(15d, 55d)
            .xLeft(0d).yTop(0d)
            .width(15d).height(26d)
            .build();
    private static final Sprite SPRITE_HOVERED = Sprite.builder()
            .texture(SPRITE.texture)
            .textureDimensions(15d, 55d)
            .xLeft(0d).yTop(28d)
            .width(15d).height(27d)
            .build();
    private static final Text DISPLAY_NAME = ImmutableText.ofPlainText("Right click marker");

    public RightClickMarker(MarkerController<?> controller) {
        super(controller, 15, 23, GeoPointImmutable.ORIGIN);
    }

    @Override
    public void draw(UiDrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        context.drawSprite(x, y, hovered ? SPRITE_HOVERED : SPRITE);
    }

    @Override
    public float getDeltaX() {
        return -8;
    }

    @Override
    public float getDeltaY() {
        return -23;
    }

    @Override
    public void update(MapWidget map) {
        this.setLocation(map.getMouseLocation());
    }

    @Override
    public boolean canBeTracked() {
        return false;
    }

    @Override
    public Text getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public String getIdentifier() {
        return this.getControllerId() + ":0";
    }

}
