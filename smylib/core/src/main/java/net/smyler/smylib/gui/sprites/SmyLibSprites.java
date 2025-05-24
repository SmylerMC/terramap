package net.smyler.smylib.gui.sprites;

import net.smyler.smylib.Identifier;
import net.smyler.smylib.SmyLib;

public enum SmyLibSprites {

    // === 14x14 ===

    WARNING_14("warning_14", "warning", 14d, 14d, 0d, 0d, 14d, 14d),

    // === 15x15 ===

    MAGNIFYING_GLASS_15("magnifying_glass_15", "magnifying_glass", 15d, 45d, 0d, 0d, 15d, 15d),

    BUTTON_BLANK_15("button_blank_15", "button_blank_15", 15d, 45d, 0d, 0d, 15d, 15d),
    BUTTON_BLANK_15_HIGHLIGHTED("button_blank_15_highlighted", "button_blank_15", 15d, 45d, 0d, 15d, 15d, 30d),
    BUTTON_BLANK_15_DISABLED("button_blank_15_disabled", "button_blank_15", 15d, 45d, 0d, 30d, 15d, 45d),

    BUTTON_PLUS_15("button_plus_15", "button_plus_15", 15d, 45d, 0d, 0d, 15d, 15d),
    BUTTON_PLUS_15_HIGHLIGHTED("button_plus_15_highlighted", "button_plus_15", 15d, 45d, 0d, 15d, 15d, 30d),
    BUTTON_PLUS_15_DISABLED("button_plus_15_disabled", "button_plus_15", 15d, 45d, 0d, 30d, 15d, 45d),

    BUTTON_MINUS_15("button_minus_15", "button_minus_15", 15d, 45d, 0d, 0d, 15d, 15d),
    BUTTON_MINUS_15_HIGHLIGHTED("button_minus_15_highlighted", "button_minus_15", 15d, 45d, 0d, 15d, 15d, 30d),
    BUTTON_MINUS_15_DISABLED("button_minus_15_disabled", "button_minus_15", 15d, 45d, 0d, 30d, 15d, 45d),

    BUTTON_LEFT_15("button_left_15", "button_left_15", 15d, 45d, 0d, 0d, 15d, 15d),
    BUTTON_LEFT_15_HIGHLIGHTED("button_left_15_highlighted", "button_left_15", 15d, 45d, 0d, 15d, 15d, 30d),
    BUTTON_LEFT_15_DISABLED("button_left_15_disabled", "button_left_15", 15d, 45d, 0d, 30d, 15d, 45d),

    BUTTON_RIGHT_15("button_right_15", "button_right_15", 15d, 45d, 0d, 0d, 15d, 15d),
    BUTTON_RIGHT_15_HIGHLIGHTED("button_right_15_highlighted", "button_right_15", 15d, 45d, 0d, 15d, 15d, 30d),
    BUTTON_RIGHT_15_DISABLED("button_right_15_disabled", "button_right_15", 15d, 45d, 0d, 30d, 15d, 45d),

    BUTTON_UP_15("button_up_15", "button_up_15", 15d, 45d, 0d, 0d, 15d, 15d),
    BUTTON_UP_15_HIGHLIGHTED("button_up_15_highlighted", "button_up_15", 15d, 45d, 0d, 15d, 15d, 30d),
    BUTTON_UP_15_DISABLED("button_up_15_disabled", "button_up_15", 15d, 45d, 0d, 30d, 15d, 45d),

    BUTTON_DOWN_15("button_down_15", "button_down_15", 15d, 45d, 0d, 0d, 15d, 15d),
    BUTTON_DOWN_15_HIGHLIGHTED("button_down_15_highlighted", "button_down_15", 15d, 45d, 0d, 15d, 15d, 30d),
    BUTTON_DOWN_15_DISABLED("button_down_15_disabled", "button_down_15", 15d, 45d, 0d, 30d, 15d, 45d),

    BUTTON_CROSS_15("button_cross_15", "button_cross_15", 15d, 45d, 0d, 0d, 15d, 15d),
    BUTTON_CROSS_15_HIGHLIGHTED("button_cross_15_highlighted", "button_cross_15", 15d, 45d, 0d, 15d, 15d, 30d),
    BUTTON_CROSS_15_DISABLED("button_cross_15_disabled", "button_cross_15", 15d, 45d, 0d, 30d, 15d, 45d),

    BUTTON_CENTER_15("button_center_15", "button_center_15", 15d, 45d, 0d, 0d, 15d, 15d),
    BUTTON_CENTER_15_HIGHLIGHTED("button_center_15_highlighted", "button_center_15", 15d, 45d, 0d, 15d, 15d, 30d),
    BUTTON_CENTER_15_DISABLED("button_center_15_disabled", "button_center_15", 15d, 45d, 0d, 30d, 15d, 45d),

    BUTTON_PAPER_15("button_paper_15", "button_paper_15", 15d, 45d, 0d, 0d, 15d, 15d),
    BUTTON_PAPER_15_HIGHLIGHTED("button_paper_15_highlighted", "button_paper_15", 15d, 45d, 0d, 15d, 15d, 30d),
    BUTTON_PAPER_15_DISABLED("button_paper_15_disabled", "button_paper_15", 15d, 45d, 0d, 30d, 15d, 45d),

    BUTTON_WRENCH_15("button_wrench_15", "button_wrench_15", 15d, 45d, 0d, 0d, 15d, 15d),
    BUTTON_WRENCH_15_HIGHLIGHTED("button_wrench_15_highlighted", "button_wrench_15", 15d, 45d, 0d, 15d, 15d, 30d),
    BUTTON_WRENCH_15_DISABLED("button_wrench_15_disabled", "button_wrench_15", 15d, 45d, 0d, 30d, 15d, 45d),

    BUTTON_BURGER_15("button_burger_15", "button_burger_15", 15d, 45d, 0d, 0d, 15d, 15d),
    BUTTON_BURGER_15_HIGHLIGHTED("button_burger_15_highlighted", "button_burger_15", 15d, 45d, 0d, 15d, 15d, 30d),
    BUTTON_BURGER_15_DISABLED("button_burger_15_disabled", "button_burger_15", 15d, 45d, 0d, 30d, 15d, 45d),

    BUTTON_TRASH_15("button_trash_15", "button_trash_15", 15d, 45d, 0d, 0d, 15d, 15d),
    BUTTON_TRASH_15_HIGHLIGHTED("button_trash_15_highlighted", "button_trash_15", 15d, 45d, 0d, 15d, 15d, 30d),
    BUTTON_TRASH_15_DISABLED("button_trash_15_disabled", "button_trash_15", 15d, 45d, 0d, 30d, 15d, 45d),

    BUTTON_OFFSET_15("button_offset_15", "button_offset_15", 15d, 45d, 0d, 0d, 15d, 15d),
    BUTTON_OFFSET_15_HIGHLIGHTED("button_offset_15_highlighted", "button_offset_15", 15d, 45d, 0d, 15d, 15d, 30d),
    BUTTON_OFFSET_15_DISABLED("button_offset_15_disabled", "button_offset_15", 15d, 45d, 0d, 30d, 15d, 45d),

    BUTTON_OFFSET_WARNING_15("button_offset_warning_15", "button_offset_warning_15", 15d, 45d, 0d, 0d, 15d, 15d),
    BUTTON_OFFSET_WARNING_15_HIGHLIGHTED("button_offset_warning_15_highlighted", "button_offset_warning_15", 15d, 45d, 0d, 15d, 15d, 30d),
    BUTTON_OFFSET_WARNING_15_DISABLED("button_offset_warning_15_disabled", "button_offset_warning_15", 15d, 45d, 0d, 30d, 15d, 45d),

    BUTTON_VISIBILITY_ON_15("button_visibility_on_15", "button_visibility_15", 15d, 90d, 0d, 0d, 15d, 15d),
    BUTTON_VISIBILITY_OFF_15("button_visibility_off_15", "button_visibility_15", 15d, 90d, 0d, 15d, 15d, 30d),
    BUTTON_VISIBILITY_ON_15_HIGHLIGHTED("button_visibility_on_15_highlighted", "button_visibility_15", 15d, 90d, 0d, 30d, 15d, 45d),
    BUTTON_VISIBILITY_OFF_15_HIGHLIGHTED("button_visibility_off_15_highlighted", "button_visibility_15", 15d, 90d, 0d, 45d, 15d, 60d),
    BUTTON_VISIBILITY_ON_15_DISABLED("button_visibility_on_15_disabled", "button_visibility_15", 15d, 90d, 0d, 60d, 15d, 75d),
    BUTTON_VISIBILITY_OFF_15_DISABLED("button_visibility_off_15_disabled", "button_visibility_15", 15d, 90d, 0d, 75d, 15d, 90d),

    // === 20x20 ===

    BUTTON_BLANK_20("button_blank_20", "button_blank_20", 20d, 60d, 0d, 0d, 20d, 20d),
    BUTTON_BLANK_20_HIGHLIGHTED("button_blank_20_highlighted", "button_blank_20", 20d, 60d, 0d, 20d, 20d, 40d),
    BUTTON_BLANK_20_DISABLED("button_blank_20_disabled", "button_blank_20", 20d, 60d, 0d, 40d, 20d, 60d),

    BUTTON_BURGER_20("button_burger_20", "button_burger_20", 20d, 60d, 0d, 0d, 20d, 20d),
    BUTTON_BURGER_20_HIGHLIGHTED("button_burger_20_highlighted", "button_burger_20", 20d, 60d, 0d, 20d, 20d, 40d),
    BUTTON_BURGER_20_DISABLED("button_burger_20_disabled", "button_burger_20", 20d, 60d, 0d, 40d, 20d, 60d),

    // === 21x21 ===

    BUTTON_BLANK_21("button_blank_21", "button_blank_21", 21d, 63d, 0d, 0d, 21d, 21d),
    BUTTON_BLANK_21_HIGHLIGHTED("button_blank_21_highlighted", "button_blank_21", 21d, 63d, 0d, 21d, 21d, 42d),
    BUTTON_BLANK_21_DISABLED("button_blank_21_disabled", "button_blank_21", 21d, 63d, 0d, 42d, 21d, 63d),

    BUTTON_SEARCH_21("button_search_21", "button_search_21", 21d, 63d, 0d, 0d, 21d, 21d),
    BUTTON_SEARCH_21_HIGHLIGHTED("button_search_21_highlighted", "button_search_21", 21d, 63d, 0d, 21d, 21d, 42d),
    BUTTON_SEARCH_21_DISABLED("button_search_21_disabled", "button_search_21", 21d, 63d, 0d, 42d, 21d, 63d),

    // === 26x16 ===

    BUTTON_TOGGLE_OFF("button_toggle_off", "button_toggle", 52d, 48d, 0d, 0d, 26d, 16d),
    BUTTON_TOGGLE_ON("button_toggle_on", "button_toggle", 52d, 48d, 26d, 0d, 52d, 16d),
    BUTTON_TOGGLE_OFF_HIGHLIGHTED("button_toggle_off_highlighted", "button_toggle", 52d, 48d, 0d, 16d, 26d, 32d),
    BUTTON_TOGGLE_ON_HIGHLIGHTED("button_toggle_on_highlighted", "button_toggle", 52d, 48d, 26d, 16d, 52d, 32d),
    BUTTON_TOGGLE_OFF_DISABLED("button_toggle_off_disabled", "button_toggle", 52d, 48d, 0d, 32d, 26d, 48d),
    BUTTON_TOGGLE_ON_DISABLED("button_toggle_on_disabled", "button_toggle", 52d, 48d, 26d, 32d, 52d, 48d);

    public final Identifier identifier;
    public final Sprite sprite;

    SmyLibSprites(String name, String textureName, double textureWidth, double textureHeight, double xLeft, double yTop, double xRight, double yBottom) {
        this.identifier = new Identifier("smylib", name);
        this.sprite = new Sprite(
                new Identifier(SmyLib.MODDING_NAMESPACE, "textures/gui/sprites/" + textureName + ".png"),
                textureWidth, textureHeight,
                xLeft, yTop, xRight, yBottom
        );
    }

}
