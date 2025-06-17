package net.smyler.terramap.gui.sprites;

import net.smyler.smylib.Identifier;
import net.smyler.smylib.SmyLib;
import net.smyler.smylib.gui.sprites.Sprite;
import net.smyler.smylib.gui.sprites.SpriteLibrary;

import java.util.HashMap;
import java.util.Map;

public final class TerramapSprites {

    private static final Map<Identifier, Sprite> sprites = new HashMap<>();

    public static final Sprite
            MARKER_CAT = sprite("marker_cat", "markers/cat", 5d, 5d),
            MARKER_CHICKEN = sprite("marker_chicken", "markers/chicken", 4d, 6d),
            MARKER_DONKEY = sprite("marker_donkey", "markers/donkey", 6d, 21d),
            MARKER_ENDER_DRAGON = sprite("marker_ender_dragon", "markers/ender_dragon", 16d, 20d),
            MARKER_ENDERMAN = sprite("marker_enderman", "markers/enderman", 8d, 8d),
            MARKER_IRON_GOLEM = sprite("marker_iron_golem", "markers/golem_iron", 8d, 11d),
            MARKER_GUARDIAN = sprite("marker_guardian", "markers/guardian", 12d, 12d),
            MARKER_ELDER_GUARDIAN = sprite("marker_elder_guardian", "markers/elder_guardian", 12d, 12d),
            MARKER_HORSE = sprite("marker_horse", "markers/horse", 6d, 15d),
            MARKER_SKELETON_HORSE = sprite("marker_skeleton_horse", "markers/horse_skeleton", 6d, 15d),
            MARKER_ZOMBIE_HORSE = sprite("marker_zombie_horse", "markers/horse_zombie", 6d, 15d),
            MARKER_LLAMA = sprite("marker_llama", "markers/llama", 8d, 11d),
            MARKER_MAGMA_CUBE = sprite("maker_magma_cube", "markers/magma_cube", 8d, 8d),
            MARKER_MULE = sprite("maker_mule", "markers/mule", 16d, 21d),
            MARKER_OCELOT = sprite("maker_ocelot", "markers/ocelot", 5d, 5d),
            MARKER_PARROT = sprite("maker_parrot", "markers/parrot", 4d, 8d),
            MARKER_PIG = sprite("maker_pig", "markers/pig", 8d, 8d),
            MARKER_PIGLIN_ZOMBIFIED = sprite("maker_piglin_zombified", "markers/piglin_zombified", 8d, 8d),
            MARKER_RABBIT = sprite("maker_rabbit", "markers/rabbit", 5d, 9d),
            MARKER_SLIME = sprite("maker_slime", "markers/slime", 8d, 8d),
            MARKER_VILLAGER = sprite("maker_villager", "markers/villager", 8d, 11d),
            MARKER_ZOMBIE_VILLAGER = sprite("maker_zombie_villager", "markers/villager_zombie", 8d, 11d),
            MARKER_WITCH = sprite("maker_witch", "markers/witch", 10d, 21d),
            MARKER_WITHER = sprite("maker_wither", "markers/wither", 26d, 18d),
            MARKER_WOLF = sprite("maker_wolf", "markers/wolf", 12d, 12d),
            MARKER_TOKEN_GREEN = sprite("maker_token_green", "markers/token_green", 10d, 24d, 0d, 0d, 10d, 12d),
            MARKER_TOKEN_GREY = sprite("maker_token_grey", "markers/token_grey", 10d, 24d, 0d, 0d, 10d, 12d),
            MARKER_TOKEN_RED = sprite("maker_token_red", "markers/token_red", 10d, 24d, 0d, 0d, 10d, 12d),
            MARKER_TOKEN_YELLOW = sprite("maker_token_yellow", "markers/token_yellow", 10d, 24d, 0d, 0d, 10d, 12d),
            MARKER_TOKEN_BLUE = sprite("maker_token_blue", "markers/token_blue", 10d, 24d, 0d, 0d, 10d, 12d);


    private static Sprite sprite(String name, String textureName, double textureWidth, double textureHeight, double leftOffset, double topOffset, double rightOffset, double bottomOffset) {
        Identifier texture = new Identifier("terramap", "textures/sprites/" + textureName + ".png");
        Sprite sprite = new Sprite(texture, textureWidth, textureHeight, leftOffset, topOffset, rightOffset, bottomOffset);
        Identifier identifier = new Identifier("terramap", name);
        sprites.put(identifier, sprite);
        return sprite;
    }

    private static Sprite sprite(String name, String textureName, double textureWidth, double textureHeight) {
        return sprite(name, textureName, textureWidth, textureHeight, 0d, 0d, textureWidth, textureHeight);
    }

    public static void registerAllTerramapSprites() {
        SpriteLibrary library = SmyLib.getGameClient().sprites();
        for (Map.Entry<Identifier, Sprite> entry : sprites.entrySet()) {
            library.registerSprite(entry.getKey(), entry.getValue());
        }
    }

    private TerramapSprites() {
        throw new IllegalStateException("Utility class is being instantiated");
    }

}
