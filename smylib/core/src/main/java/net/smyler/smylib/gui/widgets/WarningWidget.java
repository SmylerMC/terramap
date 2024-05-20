package net.smyler.smylib.gui.widgets;

import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.sprites.Sprite;
import net.smyler.smylib.gui.containers.WidgetContainer;
import net.smyler.smylib.gui.DrawContext;

public class WarningWidget extends AbstractSolidWidget {

    private static final Identifier TEXTURE = new Identifier("terramap", "textures/gui/widgets.png");
    private static final Sprite SPRITE = new Sprite(TEXTURE, 256d, 256d, 15d, 54d, 30d, 69d);

    public WarningWidget(float x, float y, int z) {
        super(x, y, z, 15, 15);
    }

    @Override
    public void draw(DrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        context.drawSprite(x, y, SPRITE);
    }

    public WarningWidget setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

}
