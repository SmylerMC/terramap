package net.smyler.smylib.gui.widgets;

import net.smyler.smylib.gui.sprites.Sprite;
import net.smyler.smylib.gui.containers.WidgetContainer;
import net.smyler.smylib.gui.DrawContext;

public class SpriteWidget extends AbstractSolidWidget {


    private final Sprite sprite;

    public SpriteWidget(float x, float y, int z, Sprite sprite) {
        super(x, y, z, (float)sprite.width(), (float)sprite.height());
        this.sprite = sprite;
    }

    @Override
    public void draw(DrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        context.drawSprite(x, y, this.sprite);
    }

    public SpriteWidget setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

}
