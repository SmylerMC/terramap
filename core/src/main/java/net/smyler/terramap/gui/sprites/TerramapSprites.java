package net.smyler.terramap.gui.sprites;

import net.smyler.smylib.Identifier;
import net.smyler.smylib.SmyLib;
import net.smyler.smylib.gui.sprites.Sprite;
import net.smyler.smylib.gui.sprites.SpriteLibrary;

import java.util.HashMap;
import java.util.Map;

import static net.smyler.smylib.gui.sprites.Sprite.builder;
import static net.smyler.terramap.TerramapResources.SPRITES_MAP_MARKERS;

public final class TerramapSprites {

    private static final Map<Identifier, Sprite> sprites = new HashMap<>();

    public static final Sprite
            MARKER_CAT = register( "marker_cat", builder()
                    .texture(SPRITES_MAP_MARKERS.resolve("cat.png"))
                    .textureDimensions(5d, 5d)
                    .fullTexture()
                    .build()
            ),
            MARKER_CHICKEN = register("marker_chicken", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("chicken.png"))
                .textureDimensions(4d, 6d)
                .fullTexture()
                .build()
            ),
            MARKER_DONKEY = register("marker_donkey", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("donkey.png"))
                .textureDimensions(6d, 21d)
                .fullTexture()
                .build()
            ),
            MARKER_ENDER_DRAGON = register("marker_ender_dragon", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("ender_dragon.png"))
                .textureDimensions(16d, 20d)
                .fullTexture()
                .build()
            ),
            MARKER_ENDERMAN = register("marker_enderman", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("enderman.png"))
                .textureDimensions(8d, 8d)
                .fullTexture()
                .build()
            ),
            MARKER_IRON_GOLEM = register("marker_iron_golem", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("golem_iron.png"))
                .textureDimensions(8d, 11d)
                .fullTexture()
                .build()
            ),
            MARKER_GUARDIAN = register("marker_guardian", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("guardian.png"))
                .textureDimensions(12d, 12d)
                .fullTexture()
                .build()
            ),
            MARKER_ELDER_GUARDIAN = register("marker_elder_guardian", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("guardian_elder.png"))
                .textureDimensions(12d, 12d)
                .fullTexture()
                .build()
            ),
            MARKER_HORSE = register("marker_horse", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("horse.png"))
                .textureDimensions(6d, 15d)
                .fullTexture()
                .build()
            ),
            MARKER_SKELETON_HORSE = register("marker_skeleton_horse", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("horse_skeleton.png"))
                .textureDimensions(6d, 15d)
                .fullTexture()
                .build()
            ),
            MARKER_ZOMBIE_HORSE = register("marker_zombie_horse", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("horse_zombie.png"))
                .textureDimensions(6d, 15d)
                .fullTexture()
                .build()
            ),
            MARKER_LLAMA = register("marker_llama", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("llama.png"))
                .textureDimensions(8d, 11d)
                .fullTexture()
                .build()
            ),
            MARKER_MAGMA_CUBE = register("maker_magma_cube", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("magma_cube.png"))
                .textureDimensions(8d, 8d)
                .fullTexture()
                .build()
            ),
            MARKER_MULE = register("maker_mule", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("mule.png"))
                .textureDimensions(16d, 21d)
                .fullTexture()
                .build()
            ),
            MARKER_OCELOT = register("maker_ocelot", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("ocelot.png"))
                .textureDimensions(5d, 5d)
                .fullTexture()
                .build()
            ),
            MARKER_PARROT = register("maker_parrot", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("parrot.png"))
                .textureDimensions(4d, 8d)
                .fullTexture()
                .build()
            ),
            MARKER_PIG = register("maker_pig", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("pig.png"))
                .textureDimensions(8d, 8d)
                .fullTexture()
                .build()
            ),
            MARKER_PIGLIN_ZOMBIFIED = register("maker_piglin_zombified", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("piglin_zombified.png"))
                .textureDimensions(8d, 8d)
                .fullTexture()
                .build()
            ),
            MARKER_RABBIT = register("maker_rabbit", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("rabbit.png"))
                .textureDimensions(5d, 9d)
                .fullTexture()
                .build()
            ),
            MARKER_SLIME = register("maker_slime", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("slime.png"))
                .textureDimensions(8d, 8d)
                .fullTexture()
                .build()
            ),
            MARKER_VILLAGER = register("maker_villager", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("villager.png"))
                .textureDimensions(8d, 11d)
                .fullTexture()
                .build()
            ),
            MARKER_ZOMBIE_VILLAGER = register("maker_zombie_villager", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("villager_zombie.png"))
                .textureDimensions(8d, 11d)
                .fullTexture()
                .build()
            ),
            MARKER_WITCH = register("maker_witch", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("witch.png"))
                .textureDimensions(10d, 21d)
                .fullTexture()
                .build()
            ),
            MARKER_WITHER = register("maker_wither", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("wither.png"))
                .textureDimensions(26d, 18d)
                .fullTexture()
                .build()
            ),
            MARKER_WOLF = register("maker_wolf", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("wolf.png"))
                .textureDimensions(12d, 12d)
                .fullTexture()
                .build()
            ),
            MARKER_TOKEN_GREEN = register("maker_token_green", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("token_green.png"))
                .textureDimensions(10d, 24d)
                .xLeft(0d).yTop(0d)
                .xRight(10d).yBottom(12d)
                .build()
            ),
            MARKER_TOKEN_GREY = register("maker_token_grey", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("token_grey.png"))
                .textureDimensions(10d, 24d)
                .xLeft(0d).yTop(0d)
                .xRight(10d).yBottom(12d)
                .build()
            ),
            MARKER_TOKEN_RED = register("maker_token_red", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("token_red.png"))
                .textureDimensions(10d, 24d)
                .xLeft(0d).yTop(0d)
                .xRight(10d).yBottom(12d)
                .build()
            ),
            MARKER_TOKEN_YELLOW = register("maker_token_yellow", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("token_yellow.png"))
                .textureDimensions(10d, 24d)
                .xLeft(0d).yTop(0d)
                .xRight(10d).yBottom(12d)
                .build()
            ),
            MARKER_TOKEN_BLUE = register("maker_token_blue", builder()
                .texture(SPRITES_MAP_MARKERS.resolve("token_blue.png"))
                .textureDimensions(10d, 24d)
                .xLeft(0d).yTop(0d)
                .xRight(10d).yBottom(12d)
                .build()
            );


    private static Sprite register(String name, Sprite sprite) {
        Identifier identifier = new Identifier("terramap", name);
        sprites.put(identifier, sprite);
        return sprite;
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
