package net.smyler.smylib.gui.sprites;

import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.widgets.SpriteWidget;

public class WarningWidget extends SpriteWidget {

    private static final Identifier TEXTURE = new Identifier("terramap", "textures/gui/widgets.png");
    private static final Sprite SPRITE = new Sprite(TEXTURE, 256d, 256d, 15d, 54d, 30d, 69d);

    public WarningWidget(float x, float y, int z) {
        super(x, y, z, SPRITE);
    }
}
