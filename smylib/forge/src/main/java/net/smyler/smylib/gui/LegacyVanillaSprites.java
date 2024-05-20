package net.smyler.smylib.gui;

import net.smyler.smylib.Identifier;

public class LegacyVanillaSprites extends SpriteLibrary {

    private static final Identifier TEXTURE_BUTTONS_VANILLA = new Identifier("minecraft", "textures/gui/widgets.png");

    public LegacyVanillaSprites() {
        this.registerVanillaButton("minecraft:button", 0d, 66d, 200d, 86d);
        this.registerVanillaButton("minecraft:button_disabled", 0d, 46d, 200d, 66d);
        this.registerVanillaButton("minecraft:button_highlighted", 0d, 86d, 200d, 106d);
        this.registerVanillaButton("minecraft:locked_button", 0d, 146d, 20d, 166d);
        this.registerVanillaButton("minecraft:locked_button_disabled", 0d, 186d, 20d, 206d);
        this.registerVanillaButton("minecraft:locked_button_highlighted", 0d, 166d, 20d, 186d);
        this.registerVanillaButton("minecraft:unlocked_button", 20d, 146d, 40d, 166d);
        this.registerVanillaButton("minecraft:unlocked_button_disabled", 20d, 186d, 40d, 206d);
        this.registerVanillaButton("minecraft:unlocked_button_highlighted", 20d, 166d, 40d, 186d);
    }

    private void registerVanillaButton(String identifier, double xLeft, double yTop, double xRight, double yBottom) {
        this.registerSprite(
                identifier,
                new Sprite(
                        TEXTURE_BUTTONS_VANILLA,
                        256d, 256d,
                        xLeft, yTop, xRight, yBottom

                )
        );
    }

}
