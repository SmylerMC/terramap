package net.smyler.smylib.gui;

import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.sprites.Sprite;
import net.smyler.smylib.gui.sprites.SpriteLibrary;

public class LegacyVanillaSprites extends SpriteLibrary {

    private static final Identifier TEXTURE_BUTTONS_VANILLA = new Identifier("minecraft", "textures/gui/widgets.png");

    public LegacyVanillaSprites() {
        this.registerSprite("minecraft:button", button().xLeft(0).yTop(66d).width(200d).height(20d).build());
        this.registerSprite("minecraft:button_disabled", button().xLeft(0d).yTop(46d).width(200d).height(20d).build());
        this.registerSprite("minecraft:button_highlighted", button().xLeft(0d).yTop(86d).width(200d).height(20d).build());
        this.registerSprite("minecraft:locked_button", button().xLeft(0d).yTop(146d).width(20d).height(20d).build());
        this.registerSprite("minecraft:locked_button_disabled", button().xLeft(0d).yTop(186d).width(20d).height(20d).build());
        this.registerSprite("minecraft:locked_button_highlighted", button().xLeft(0d).yTop(166d).width(20d).height(20d).build());
        this.registerSprite("minecraft:unlocked_button", button().xLeft(20d).yTop(146d).width(20d).height(20d).build());
        this.registerSprite("minecraft:unlocked_button_disabled", button().xLeft(20d).yTop(186d).width(20d).height(20d).build());
        this.registerSprite("minecraft:unlocked_button_highlighted", button().xLeft(20d).yTop(166d).width(20d).height(20d).build());
        this.registerSprite("minecraft:slider", button().xLeft(0d).yTop(46d).width(200d).height(20d).build());
        this.registerSprite("minecraft:slider_handle", button().xLeft(0d).yTop(66d).width(200d).height(20d).build());
        this.registerSprite("minecraft:player_skin_wide_steve", Sprite.builder().texture(new Identifier("minecraft", "textures/entity/steve.png")).fullTexture().build());
    }

    private Sprite.Builder button() {
        return Sprite.builder().texture(TEXTURE_BUTTONS_VANILLA, 256d, 256d);
    }

}
