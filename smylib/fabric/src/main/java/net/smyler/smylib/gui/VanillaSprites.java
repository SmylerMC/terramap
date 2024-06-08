package net.smyler.smylib.gui;

import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.sprites.Sprite;
import net.smyler.smylib.gui.sprites.SpriteLibrary;

public class VanillaSprites extends SpriteLibrary {

    private static final Identifier TEXTURE_WIDGETS_VANILLA = new Identifier("minecraft", "textures/gui/widgets.png");
    private static final Identifier TEXTURE_SLIDERS_VANILLA = new Identifier("minecraft", "textures/gui/slider.png");

    public VanillaSprites() {
        this.registerVanillaWidget("minecraft:button", 0d, 66d, 200d, 86d);
        this.registerVanillaWidget("minecraft:button_disabled", 0d, 46d, 200d, 66d);
        this.registerVanillaWidget("minecraft:button_highlighted", 0d, 86d, 200d, 106d);
        this.registerVanillaWidget("minecraft:locked_button", 0d, 146d, 20d, 166d);
        this.registerVanillaWidget("minecraft:locked_button_disabled", 0d, 186d, 20d, 206d);
        this.registerVanillaWidget("minecraft:locked_button_highlighted", 0d, 166d, 20d, 186d);
        this.registerVanillaWidget("minecraft:unlocked_button", 20d, 146d, 40d, 166d);
        this.registerVanillaWidget("minecraft:unlocked_button_disabled", 20d, 186d, 40d, 206d);
        this.registerVanillaWidget("minecraft:unlocked_button_highlighted", 20d, 166d, 40d, 186d);
        this.registerVanillaSlider("minecraft:slider", 0d, 20d);
        this.registerVanillaSlider("minecraft:slider_highlighted", 20d, 40d);
        this.registerVanillaSlider("minecraft:slider_handle", 40d, 60d);
        this.registerVanillaSlider("minecraft:slider_handle_highlighted", 60d, 80d);
    }

    private void registerVanillaWidget(String identifier, double xLeft, double yTop, double xRight, double yBottom) {
        this.registerSprite(
                identifier,
                new Sprite(
                        TEXTURE_WIDGETS_VANILLA,
                        256d, 256d,
                        xLeft, yTop, xRight, yBottom

                )
        );
    }

    private void registerVanillaSlider(String identifier, double yTop, double yBottom) {
        this.registerSprite(
                identifier,
                new Sprite(
                        TEXTURE_SLIDERS_VANILLA,
                        256d, 256d,
                        0d, yTop, 200d, yBottom
                )
        );
    }

}
