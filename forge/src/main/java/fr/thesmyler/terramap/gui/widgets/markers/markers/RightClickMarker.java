package fr.thesmyler.terramap.gui.widgets.markers.markers;

import net.smyler.smylib.gui.containers.WidgetContainer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import net.smyler.smylib.gui.DrawContext;
import net.smyler.smylib.gui.sprites.Sprite;
import net.smyler.terramap.TerramapResources;
import net.smyler.terramap.util.geo.GeoPointImmutable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class RightClickMarker extends AbstractFixedMarker {

    private static final Sprite SPRITE = new Sprite(
            TerramapResources.SPRITES.resolve("map_marker.png"),
            15d, 55d,
            0d, 0d, 15d, 26d
    );
    private static final Sprite SPRITE_HOVERED = new Sprite(
            SPRITE.texture,
            15d, 55d,
            0d, 28d, 15d, 55d
    );

    public RightClickMarker(MarkerController<?> controller) {
        super(controller, 15, 23, GeoPointImmutable.ORIGIN);
    }

    @Override
    public void draw(DrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
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
    public ITextComponent getDisplayName() {
        return new TextComponentString("Right click marker");
    }

    @Override
    public String getIdentifier() {
        return this.getControllerId() + ":0";
    }

}
