package net.smyler.smylib.gui.sprites;

import net.smyler.smylib.Identifier;
import net.smyler.smylib.SmyLib;

import java.util.HashMap;
import java.util.Map;

import static net.smyler.smylib.gui.sprites.Sprite.builder;

public final class SmyLibSprites {
    private static final Identifier SPRITE_RESOURCES = new Identifier(
            SmyLib.MODDING_NAMESPACE,
            "textures/gui/sprites"
    );


    private static final Map<Identifier, Sprite> sprites = new HashMap<>();

    public static final Sprite

    // === 14x14 ===

    WARNING_14 = register("warning_14", builder()
            .texture(SPRITE_RESOURCES.resolve("warning.png"))
            .textureDimensions(14d, 14d)
            .xLeft(0d).yTop(0d)
            .xRight(14d).yBottom(14d)
            .build()
    ),

    // === 15x15 ===

    MAGNIFYING_GLASS_15 = register("magnifying_glass_15", builder()
            .texture(SPRITE_RESOURCES.resolve("magnifying_glass.png"))
            .textureDimensions(15d, 15d)
            .xLeft(0d).yTop(0d)
            .xRight(15d).yBottom(15d)
            .build()
    ),

    BUTTON_BLANK_15 = register("button_blank_15", builder()
            .texture(SPRITE_RESOURCES.resolve("button_blank_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(0d)
            .xRight(15d).yBottom(15d)
            .build()
    ),
    BUTTON_BLANK_15_HIGHLIGHTED = register("button_blank_15_highlighted", builder()
            .texture(SPRITE_RESOURCES.resolve("button_blank_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(15d)
            .xRight(15d).yBottom(30d)
            .build()
    ),
    BUTTON_BLANK_15_DISABLED = register("button_blank_15_disabled", builder()
            .texture(SPRITE_RESOURCES.resolve("button_blank_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(30d)
            .xRight(15d).yBottom(45d)
            .build()
    ),

    BUTTON_PLUS_15 = register("button_plus_15", builder()
            .texture(SPRITE_RESOURCES.resolve("button_plus_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(0d)
            .xRight(15d).yBottom(15d)
            .build()
    ),
    BUTTON_PLUS_15_HIGHLIGHTED = register("button_plus_15_highlighted", builder()
            .texture(SPRITE_RESOURCES.resolve("button_plus_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(15d)
            .xRight(15d).yBottom(30d)
            .build()
    ),
    BUTTON_PLUS_15_DISABLED = register("button_plus_15_disabled", builder()
            .texture(SPRITE_RESOURCES.resolve("button_plus_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(30d)
            .xRight(15d).yBottom(45d)
            .build()
    ),

    BUTTON_MINUS_15 = register("button_minus_15", builder()
            .texture(SPRITE_RESOURCES.resolve("button_minus_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(0d)
            .xRight(15d).yBottom(15d)
            .build()
    ),
    BUTTON_MINUS_15_HIGHLIGHTED = register("button_minus_15_highlighted", builder()
            .texture(SPRITE_RESOURCES.resolve("button_minus_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(15d)
            .xRight(15d).yBottom(30d)
            .build()
    ),
    BUTTON_MINUS_15_DISABLED = register("button_minus_15_disabled", builder()
            .texture(SPRITE_RESOURCES.resolve("button_minus_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(30d)
            .xRight(15d).yBottom(45d)
            .build()
    ),

    BUTTON_LEFT_15 = register("button_left_15", builder()
            .texture(SPRITE_RESOURCES.resolve("button_left_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(0d)
            .xRight(15d).yBottom(15d)
            .build()
    ),
    BUTTON_LEFT_15_HIGHLIGHTED = register("button_left_15_highlighted", builder()
            .texture(SPRITE_RESOURCES.resolve("button_left_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(15d)
            .xRight(15d).yBottom(30d)
            .build()
    ),
    BUTTON_LEFT_15_DISABLED = register("button_left_15_disabled", builder()
            .texture(SPRITE_RESOURCES.resolve("button_left_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(30d)
            .xRight(15d).yBottom(45d)
            .build()
    ),

    BUTTON_RIGHT_15 = register("button_right_15", builder()
            .texture(SPRITE_RESOURCES.resolve("button_right_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(0d)
            .xRight(15d).yBottom(15d)
            .build()
    ),
    BUTTON_RIGHT_15_HIGHLIGHTED = register("button_right_15_highlighted", builder()
            .texture(SPRITE_RESOURCES.resolve("button_right_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(15d)
            .xRight(15d).yBottom(30d)
            .build()
    ),
    BUTTON_RIGHT_15_DISABLED = register("button_right_15_disabled", builder()
            .texture(SPRITE_RESOURCES.resolve("button_right_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(30d)
            .xRight(15d).yBottom(45d)
            .build()
    ),

    BUTTON_UP_15 = register("button_up_15", builder()
            .texture(SPRITE_RESOURCES.resolve("button_up_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(0d)
            .xRight(15d).yBottom(15d)
            .build()
    ),
    BUTTON_UP_15_HIGHLIGHTED = register("button_up_15_highlighted", builder()
            .texture(SPRITE_RESOURCES.resolve("button_up_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(15d)
            .xRight(15d).yBottom(30d)
            .build()
    ),
    BUTTON_UP_15_DISABLED = register("button_up_15_disabled", builder()
            .texture(SPRITE_RESOURCES.resolve("button_up_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(30d)
            .xRight(15d).yBottom(45d)
            .build()
    ),

    BUTTON_DOWN_15 = register("button_down_15", builder()
            .texture(SPRITE_RESOURCES.resolve("button_down_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(0d)
            .xRight(15d).yBottom(15d)
            .build()
    ),
    BUTTON_DOWN_15_HIGHLIGHTED = register("button_down_15_highlighted", builder()
            .texture(SPRITE_RESOURCES.resolve("button_down_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(15d)
            .xRight(15d).yBottom(30d)
            .build()
    ),
    BUTTON_DOWN_15_DISABLED = register("button_down_15_disabled", builder()
            .texture(SPRITE_RESOURCES.resolve("button_down_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(30d)
            .xRight(15d).yBottom(45d)
            .build()
    ),

    BUTTON_CROSS_15 = register("button_cross_15", builder()
            .texture(SPRITE_RESOURCES.resolve("button_cross_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(0d)
            .xRight(15d).yBottom(15d)
            .build()
    ),
    BUTTON_CROSS_15_HIGHLIGHTED = register("button_cross_15_highlighted", builder()
            .texture(SPRITE_RESOURCES.resolve("button_cross_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(15d)
            .xRight(15d).yBottom(30d)
            .build()
    ),
    BUTTON_CROSS_15_DISABLED = register("button_cross_15_disabled", builder()
            .texture(SPRITE_RESOURCES.resolve("button_cross_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(30d)
            .xRight(15d).yBottom(45d)
            .build()
    ),

    BUTTON_CENTER_15 = register("button_center_15", builder()
            .texture(SPRITE_RESOURCES.resolve("button_center_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(0d)
            .xRight(15d).yBottom(15d)
            .build()
    ),
    BUTTON_CENTER_15_HIGHLIGHTED = register("button_center_15_highlighted", builder()
            .texture(SPRITE_RESOURCES.resolve("button_center_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(15d)
            .xRight(15d).yBottom(30d)
            .build()
    ),
    BUTTON_CENTER_15_DISABLED = register("button_center_15_disabled", builder()
            .texture(SPRITE_RESOURCES.resolve("button_center_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(30d)
            .xRight(15d).yBottom(45d)
            .build()
    ),

    BUTTON_PAPER_15 = register("button_paper_15", builder()
            .texture(SPRITE_RESOURCES.resolve("button_paper_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(0d)
            .xRight(15d).yBottom(15d)
            .build()
    ),
    BUTTON_PAPER_15_HIGHLIGHTED = register("button_paper_15_highlighted", builder()
            .texture(SPRITE_RESOURCES.resolve("button_paper_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(15d)
            .xRight(15d).yBottom(30d)
            .build()
    ),
    BUTTON_PAPER_15_DISABLED = register("button_paper_15_disabled", builder()
            .texture(SPRITE_RESOURCES.resolve("button_paper_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(30d)
            .xRight(15d).yBottom(45d)
            .build()
    ),

    BUTTON_WRENCH_15 = register("button_wrench_15", builder()
            .texture(SPRITE_RESOURCES.resolve("button_wrench_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(0d)
            .xRight(15d).yBottom(15d)
            .build()
    ),
    BUTTON_WRENCH_15_HIGHLIGHTED = register("button_wrench_15_highlighted", builder()
            .texture(SPRITE_RESOURCES.resolve("button_wrench_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(15d)
            .xRight(15d).yBottom(30d)
            .build()
    ),
    BUTTON_WRENCH_15_DISABLED = register("button_wrench_15_disabled", builder()
            .texture(SPRITE_RESOURCES.resolve("button_wrench_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(30d)
            .xRight(15d).yBottom(45d)
            .build()
    ),

    BUTTON_BURGER_15 = register("button_burger_15", builder()
            .texture(SPRITE_RESOURCES.resolve("button_burger_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(0d)
            .xRight(15d).yBottom(15d)
            .build()
    ),
    BUTTON_BURGER_15_HIGHLIGHTED = register("button_burger_15_highlighted", builder()
            .texture(SPRITE_RESOURCES.resolve("button_burger_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(15d)
            .xRight(15d).yBottom(30d)
            .build()
    ),
    BUTTON_BURGER_15_DISABLED = register("button_burger_15_disabled", builder()
            .texture(SPRITE_RESOURCES.resolve("button_burger_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(30d)
            .xRight(15d).yBottom(45d)
            .build()
    ),

    BUTTON_TRASH_15 = register("button_trash_15", builder()
            .texture(SPRITE_RESOURCES.resolve("button_trash_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(0d)
            .xRight(15d).yBottom(15d)
            .build()
    ),
    BUTTON_TRASH_15_HIGHLIGHTED = register("button_trash_15_highlighted", builder()
            .texture(SPRITE_RESOURCES.resolve("button_trash_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(15d)
            .xRight(15d).yBottom(30d)
            .build()
    ),
    BUTTON_TRASH_15_DISABLED = register("button_trash_15_disabled", builder()
            .texture(SPRITE_RESOURCES.resolve("button_trash_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(30d)
            .xRight(15d).yBottom(45d)
            .build()
    ),

    BUTTON_OFFSET_15 = register("button_offset_15", builder()
            .texture(SPRITE_RESOURCES.resolve("button_offset_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(0d)
            .xRight(15d).yBottom(15d)
            .build()
    ),
    BUTTON_OFFSET_15_HIGHLIGHTED = register("button_offset_15_highlighted", builder()
            .texture(SPRITE_RESOURCES.resolve("button_offset_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(15d)
            .xRight(15d).yBottom(30d)
            .build()
    ),
    BUTTON_OFFSET_15_DISABLED = register("button_offset_15_disabled", builder()
            .texture(SPRITE_RESOURCES.resolve("button_offset_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(30d)
            .xRight(15d).yBottom(45d)
            .build()
    ),

    BUTTON_OFFSET_WARNING_15 = register("button_offset_warning_15", builder()
            .texture(SPRITE_RESOURCES.resolve("button_offset_warning_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(0d)
            .xRight(15d).yBottom(15d)
            .build()
    ),
    BUTTON_OFFSET_WARNING_15_HIGHLIGHTED = register("button_offset_warning_15_highlighted", builder()
            .texture(SPRITE_RESOURCES.resolve("button_offset_warning_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(15d)
            .xRight(15d).yBottom(30d)
            .build()
    ),
    BUTTON_OFFSET_WARNING_15_DISABLED = register("button_offset_warning_15_disabled", builder()
            .texture(SPRITE_RESOURCES.resolve("button_offset_warning_15.png"))
            .textureDimensions(15d, 45d)
            .xLeft(0d).yTop(30d)
            .xRight(15d).yBottom(45d)
            .build()
    ),

    BUTTON_VISIBILITY_ON_15 = register("button_visibility_on_15", builder()
            .texture(SPRITE_RESOURCES.resolve("button_visibility_15.png"))
            .textureDimensions(15d, 90d)
            .xLeft(0d).yTop(0d)
            .xRight(15d).yBottom(15d)
            .build()
    ),
    BUTTON_VISIBILITY_OFF_15 = register("button_visibility_off_15", builder()
            .texture(SPRITE_RESOURCES.resolve("button_visibility_15.png"))
            .textureDimensions(15d, 90d)
            .xLeft(0d).yTop(15d)
            .xRight(15d).yBottom(30d)
            .build()
    ),
    BUTTON_VISIBILITY_ON_15_HIGHLIGHTED = register("button_visibility_on_15_highlighted", builder()
            .texture(SPRITE_RESOURCES.resolve("button_visibility_15.png"))
            .textureDimensions(15d, 90d)
            .xLeft(0d).yTop(30d)
            .xRight(15d).yBottom(45d)
            .build()
    ),
    BUTTON_VISIBILITY_OFF_15_HIGHLIGHTED = register("button_visibility_off_15_highlighted", builder()
            .texture(SPRITE_RESOURCES.resolve("button_visibility_15.png"))
            .textureDimensions(15d, 90d)
            .xLeft(0d).yTop(45d)
            .xRight(15d).yBottom(60d)
            .build()
    ),
    BUTTON_VISIBILITY_ON_15_DISABLED = register("button_visibility_on_15_disabled", builder()
            .texture(SPRITE_RESOURCES.resolve("button_visibility_15.png"))
            .textureDimensions(15d, 90d)
            .xLeft(0d).yTop(60d)
            .xRight(15d).yBottom(75d)
            .build()
    ),
    BUTTON_VISIBILITY_OFF_15_DISABLED = register("button_visibility_off_15_disabled", builder()
            .texture(SPRITE_RESOURCES.resolve("button_visibility_15.png"))
            .textureDimensions(15d, 90d)
            .xLeft(0d).yTop(75d)
            .xRight(15d).yBottom(90d)
            .build()
    ),

    // === 20x20 ===

    BUTTON_BLANK_20 = register("button_blank_20", builder()
            .texture(SPRITE_RESOURCES.resolve("button_blank_20.png"))
            .textureDimensions(20d, 60d)
            .xLeft(0d).yTop(0d)
            .xRight(20d).yBottom(20d)
            .build()
    ),
    BUTTON_BLANK_20_HIGHLIGHTED = register("button_blank_20_highlighted", builder()
            .texture(SPRITE_RESOURCES.resolve("button_blank_20.png"))
            .textureDimensions(20d, 60d)
            .xLeft(0d).yTop(20d)
            .xRight(20d).yBottom(40d)
            .build()
    ),
    BUTTON_BLANK_20_DISABLED = register("button_blank_20_disabled", builder()
            .texture(SPRITE_RESOURCES.resolve("button_blank_20.png"))
            .textureDimensions(20d, 60d)
            .xLeft(0d).yTop(40d)
            .xRight(20d).yBottom(60d)
            .build()
    ),

    BUTTON_BURGER_20 = register("button_burger_20", builder()
            .texture(SPRITE_RESOURCES.resolve("button_burger_20.png"))
            .textureDimensions(20d, 60d)
            .xLeft(0d).yTop(0d)
            .xRight(20d).yBottom(20d)
            .build()
    ),
    BUTTON_BURGER_20_HIGHLIGHTED = register("button_burger_20_highlighted", builder()
            .texture(SPRITE_RESOURCES.resolve("button_burger_20.png"))
            .textureDimensions(20d, 60d)
            .xLeft(0d).yTop(20d)
            .xRight(20d).yBottom(40d)
            .build()
    ),
    BUTTON_BURGER_20_DISABLED = register("button_burger_20_disabled", builder()
            .texture(SPRITE_RESOURCES.resolve("button_burger_20.png"))
            .textureDimensions(20d, 60d)
            .xLeft(0d).yTop(40d)
            .xRight(20d).yBottom(60d)
            .build()
    ),

    // === 21x21 ===

    BUTTON_BLANK_21 = register("button_blank_21", builder()
            .texture(SPRITE_RESOURCES.resolve("button_blank_21.png"))
            .textureDimensions(21d, 63d)
            .xLeft(0d).yTop(0d)
            .xRight(21d).yBottom(21d)
            .build()
    ),
    BUTTON_BLANK_21_HIGHLIGHTED = register("button_blank_21_highlighted", builder()
            .texture(SPRITE_RESOURCES.resolve("button_blank_21.png"))
            .textureDimensions(21d, 63d)
            .xLeft(0d).yTop(21d)
            .xRight(21d).yBottom(42d)
            .build()
    ),
    BUTTON_BLANK_21_DISABLED = register("button_blank_21_disabled", builder()
            .texture(SPRITE_RESOURCES.resolve("button_blank_21.png"))
            .textureDimensions(21d, 63d)
            .xLeft(0d).yTop(42d)
            .xRight(21d).yBottom(63d)
            .build()
    ),

    BUTTON_SEARCH_21 = register("button_search_21", builder()
            .texture(SPRITE_RESOURCES.resolve("button_search_21.png"))
            .textureDimensions(21d, 63d)
            .xLeft(0d).yTop(0d)
            .xRight(21d).yBottom(21d)
            .build()
    ),
    BUTTON_SEARCH_21_HIGHLIGHTED = register("button_search_21_highlighted", builder()
            .texture(SPRITE_RESOURCES.resolve("button_search_21.png"))
            .textureDimensions(21d, 63d)
            .xLeft(0d).yTop(21d)
            .xRight(21d).yBottom(42d)
            .build()
    ),
    BUTTON_SEARCH_21_DISABLED = register("button_search_21_disabled", builder()
            .texture(SPRITE_RESOURCES.resolve("button_search_21.png"))
            .textureDimensions(21d, 63d)
            .xLeft(0d).yTop(42d)
            .xRight(21d).yBottom(63d)
            .build()
    ),

    // === 26x16 ===

    BUTTON_TOGGLE_OFF = register("button_toggle_off", builder()
            .texture(SPRITE_RESOURCES.resolve("button_toggle.png"))
            .textureDimensions(52d, 48d)
            .xLeft(0d).yTop(0d)
            .xRight(26d).yBottom(16d)
            .build()
    ),
    BUTTON_TOGGLE_ON = register("button_toggle_on", builder()
            .texture(SPRITE_RESOURCES.resolve("button_toggle.png"))
            .textureDimensions(52d, 48d)
            .xLeft(26d).yTop(0d)
            .xRight(52d).yBottom(16d)
            .build()
    ),
    BUTTON_TOGGLE_OFF_HIGHLIGHTED = register("button_toggle_off_highlighted", builder()
            .texture(SPRITE_RESOURCES.resolve("button_toggle.png"))
            .textureDimensions(52d, 48d)
            .xLeft(0d).yTop(16d)
            .xRight(26d).yBottom(32d)
            .build()
    ),
    BUTTON_TOGGLE_ON_HIGHLIGHTED = register("button_toggle_on_highlighted", builder()
            .texture(SPRITE_RESOURCES.resolve("button_toggle.png"))
            .textureDimensions(52d, 48d)
            .xLeft(26d).yTop(16d)
            .xRight(52d).yBottom(32d)
            .build()
    ),
    BUTTON_TOGGLE_OFF_DISABLED = register("button_toggle_off_disabled", builder()
            .texture(SPRITE_RESOURCES.resolve("button_toggle.png"))
            .textureDimensions(52d, 48d)
            .xLeft(0d).yTop(32d)
            .xRight(26d).yBottom(48d)
            .build()
    ),
    BUTTON_TOGGLE_ON_DISABLED = register("button_toggle_on_disabled", builder()
            .texture(SPRITE_RESOURCES.resolve("button_toggle.png"))
            .textureDimensions(52d, 48d)
            .xLeft(26d).yTop(32d)
            .xRight(52d).yBottom(48d)
            .build());

    private static Sprite register(String name, Sprite sprite) {
        Identifier identifier = new Identifier(
                SmyLib.MODDING_NAMESPACE,
                name
        );
        sprites.put(identifier, sprite);
        return sprite;
    }

    public static void registerAllSmyLibSprites() {
        SpriteLibrary library = SmyLib.getGameClient().sprites();
        for (Map.Entry<Identifier, Sprite> entry : sprites.entrySet()) {
            library.registerSprite(entry.getKey(), entry.getValue());
        }
    }

}
